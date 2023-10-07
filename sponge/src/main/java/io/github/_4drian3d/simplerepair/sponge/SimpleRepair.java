package io.github._4drian3d.simplerepair.sponge;

import com.google.inject.Inject;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("simplerepair")
public class SimpleRepair {
    @Inject
    private PluginContainer container;

    @Listener
    public void onCommandRegister(final RegisterCommandEvent<Command.Parameterized> event) {
        final Parameter.Value<HandType> handParameter = Parameter.registryElement(TypeToken.get(HandType.class), RegistryTypes.HAND_TYPE).key("hand").build();
        final Parameter.Value<Integer> percentageParameter = Parameter.integerNumber().key("percentage").build();
        final Command.Parameterized command = Command.builder()
                .permission("simplerepair.use")
                .addParameter(handParameter)
                .addParameter(percentageParameter)
                .executor(context -> {
                    final ServerPlayer player = context.cause().first(ServerPlayer.class).orElseThrow();
                    final HandType handType = context.one(handParameter).orElseGet(HandTypes.MAIN_HAND);
                    final int percentage = context.one(percentageParameter).orElse(100);

                    return repairOnHand(player, handType, percentage);
                })
                .executionRequirements(context -> context.cause().root() instanceof ServerPlayer)
                .build();
        event.register(container, command, "simplerepair", "repair");
    }

    private CommandResult repairOnHand(ServerPlayer player, HandType hand, int percentage) {
        final ItemStack item = player.itemInHand(hand);
        final int maxDurability = item.get(Keys.MAX_DURABILITY).orElse(0);
        final int itemDurability = item.get(Keys.ITEM_DURABILITY).orElse(0);

        if (maxDurability == 0 || maxDurability == itemDurability) {
            return CommandResult.error(Component.text(""));
        }
        final int newDurability = itemDurability + calculateDurability(percentage, maxDurability);
        item.offer(Keys.ITEM_DURABILITY, Math.min(newDurability, maxDurability));
        return CommandResult.success();
    }

    private int calculateDurability(double percentage, double durability) {
        return (int) (durability / 100 * percentage);
    }
}
