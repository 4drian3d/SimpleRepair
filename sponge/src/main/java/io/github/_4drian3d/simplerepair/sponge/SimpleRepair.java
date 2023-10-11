package io.github._4drian3d.simplerepair.sponge;

import com.google.inject.Inject;
import io.github._4drian3d.simplerepair.common.RepairLogic;
import io.github._4drian3d.simplerepair.common.RepairResult;
import io.github._4drian3d.simplerepair.common.configuration.ConfigurationContainer;
import io.leangen.geantyref.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.nio.file.Path;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

@Plugin("simplerepair")
public final class SimpleRepair implements RepairLogic<ServerPlayer, HandType> {
    private static final Logger LOGGER = LoggerFactory.getLogger("SimpleRepair");
    @Inject
    private PluginContainer pluginContainer;
    @Inject
    @ConfigDir(sharedRoot = true)
    private Path path;
    private ConfigurationContainer container;

    @Listener
    public void onServerStart(StartedEngineEvent<Server> event) {
        container = ConfigurationContainer.load(LOGGER, path, "simplerepair");
    }

    @Listener
    public void onCommandRegister(final RegisterCommandEvent<Command.Parameterized> event) {
        final Parameter.Value<HandType> handParameter = Parameter.registryElement(TypeToken.get(HandType.class), RegistryTypes.HAND_TYPE).key("hand").build();
        final Parameter.Value<Integer> percentageParameter = Parameter.integerNumber().key("percentage").build();
        final Command.Parameterized command = Command.builder()
                .permission("simplerepair.use")
                .executionRequirements(context -> context.cause().root() instanceof ServerPlayer)
                .addParameter(handParameter)
                .addParameter(percentageParameter)
                .executor(context -> {
                    final ServerPlayer player = context.cause().first(ServerPlayer.class).orElseThrow();
                    final HandType handType = context.one(handParameter).orElseGet(HandTypes.MAIN_HAND);
                    final int percentage = context.one(percentageParameter).orElse(100);

                    return switch (repairItem(player, handType, percentage)) {
                        case REPAIRED -> {
                            player.sendMessage(miniMessage().deserialize(container.get().itemRepaired()));
                            yield CommandResult.success();
                        }
                        case ALREADY_REPAIRED -> CommandResult.error(miniMessage().deserialize(container.get().alreadyRepaired()));
                        case CANNOT_BE_REPAIRED -> CommandResult.error(miniMessage().deserialize(container.get().nonRepairableItem()));
                    };
                })
                .build();
        event.register(pluginContainer, command, "simplerepair", "repair");
    }

    @Override
    public RepairResult repairItem(ServerPlayer source, HandType hand, double percentage) {
        final ItemStack item = source.itemInHand(hand);
        final int maxDurability = item.get(Keys.MAX_DURABILITY).orElse(0);
        final int itemDurability = item.get(Keys.ITEM_DURABILITY).orElse(0);
        if (maxDurability == 0) {
            return RepairResult.CANNOT_BE_REPAIRED;
        }
        if (maxDurability == itemDurability) {
            return RepairResult.ALREADY_REPAIRED;
        }
        if (percentage == 100) {
            item.offer(Keys.ITEM_DURABILITY, maxDurability);
            return RepairResult.REPAIRED;
        }
        final int newDurability = itemDurability + calculatePercentage(percentage, maxDurability);
        item.offer(Keys.ITEM_DURABILITY, Math.min(newDurability, maxDurability));
        return RepairResult.REPAIRED;
    }
}
