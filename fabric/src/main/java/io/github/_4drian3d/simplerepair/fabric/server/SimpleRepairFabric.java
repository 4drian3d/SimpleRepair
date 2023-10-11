package io.github._4drian3d.simplerepair.fabric.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github._4drian3d.simplerepair.common.RepairLogic;
import io.github._4drian3d.simplerepair.common.RepairResult;
import io.github._4drian3d.simplerepair.common.configuration.ConfigurationContainer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class SimpleRepairFabric implements ModInitializer, RepairLogic<ServerPlayer, InteractionHand> {
    private static final Logger LOGGER = LoggerFactory.getLogger("SimpleRepair");
    private static final Map<String, InteractionHand> HANDS = Map.of(
            "MAIN_HAND", InteractionHand.MAIN_HAND,
            "OFF_HAND", InteractionHand.OFF_HAND
    );

    private final ConfigurationContainer container = ConfigurationContainer.load(
            LOGGER, FabricLoader.getInstance().getConfigDir(), "simplerepair"
    );

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            this.registerRepairCommand("repair", dispatcher, environment);
            this.registerRepairCommand("simplerepair", dispatcher, environment);
            this.registerRepairCommand("itemrepair", dispatcher, environment);
        });
    }

    private void registerRepairCommand(String name, CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection environment) {
        final LiteralArgumentBuilder<CommandSourceStack> cmd = literal(name)
                .requires(environment == Commands.CommandSelection.INTEGRATED
                        ? src -> src.isPlayer() && Minecraft.getInstance().hasSingleplayerServer()
                        : src -> src.isPlayer() && Permissions.check(src, "simplerepair.use", 1)
                )
                .then(argument("hand", StringArgumentType.word())
                        .suggests((ctx, builder) -> builder.suggest("MAIN_HAND").suggest("OFF_HAND").buildFuture())
                        .then(argument("percentage", DoubleArgumentType.doubleArg())
                                .executes(context -> {
                                    RepairResult result = repairItem(requireNonNull(context.getSource().getPlayer()),
                                            HANDS.get(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                                            DoubleArgumentType.getDouble(context, "percentage"));
                                    this.sendResult(context.getSource(), result, container.get());
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                        .executes(context -> {
                            RepairResult result =repairItem(
                                    requireNonNull(context.getSource().getPlayer()),
                                    HANDS.get(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                                    100);
                            this.sendResult(context.getSource(), result, container.get());
                            return Command.SINGLE_SUCCESS;
                        })
                )
                .executes(context -> {
                    RepairResult result = repairItem(requireNonNull(context.getSource().getPlayer()), InteractionHand.MAIN_HAND, 100);
                    this.sendResult(context.getSource(), result, container.get());
                    return Command.SINGLE_SUCCESS;
                });

        dispatcher.register(cmd);
    }

    @Override
    public RepairResult repairItem(ServerPlayer source, InteractionHand hand, double percentage) {
        final ItemStack item = source.getItemInHand(hand);
        final int maxDamage = item.getMaxDamage();
        final int itemDamage = item.getDamageValue();
        if (maxDamage == 0) {
            return RepairResult.CANNOT_BE_REPAIRED;
        }
        if (itemDamage == 0) {
            return RepairResult.ALREADY_REPAIRED;
        }
        final int newDamage = itemDamage - this.calculatePercentage(percentage, maxDamage);
        item.setDamageValue(Math.max(newDamage, 0));
        return RepairResult.REPAIRED;
    }
}
