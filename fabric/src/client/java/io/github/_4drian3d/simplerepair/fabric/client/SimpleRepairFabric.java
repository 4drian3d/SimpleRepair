package io.github._4drian3d.simplerepair.fabric.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

import static java.util.Objects.requireNonNull;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


public class SimpleRepairFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            final var cmd = literal("simplerepair")
                    .requires(src ->  src.isPlayer() && Minecraft.getInstance().hasSingleplayerServer())
                    .then(argument("hand", StringArgumentType.word())
                            .suggests((ctx, builder) -> builder.suggest("MAIN_HAND").suggest("OFF_HAND").buildFuture())
                            .then(argument("percentage", DoubleArgumentType.doubleArg())
                                    .executes(context -> repairOnHand(requireNonNull(context.getSource().getPlayer()),
                                            InteractionHand.valueOf(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                                            DoubleArgumentType.getDouble(context, "percentage"))
                                    )
                            )
                            .executes(context -> repairOnHand(
                                    requireNonNull(context.getSource().getPlayer()),
                                    InteractionHand.valueOf(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                                    100)
                            )
                    )
                    .executes(context -> repairOnHand(requireNonNull(context.getSource().getPlayer()), InteractionHand.MAIN_HAND, 100));

            dispatcher.register(cmd);
            dispatcher.register(literal("itemrepair").redirect(cmd.build()));
        });
    }

    private int repairOnHand(ServerPlayer player, InteractionHand hand, double percentage) {
        final ItemStack item = player.getItemInHand(hand);

        final int maxDamage = item.getMaxDamage();
        final int itemDamage = item.getDamageValue();

        if (maxDamage == 0 || itemDamage == 0) {
            player.sendSystemMessage(Component.literal("la wea es maxDamage o itemDamage"));
            return 0;
        }
        final int newDamage = itemDamage - calculateDamage(percentage, maxDamage);
        item.setDamageValue(Math.max(newDamage, 0));

        return Command.SINGLE_SUCCESS;
    }

    private int calculateDamage(double percentage, double durability) {
        return (int) (durability / 100 * percentage);
    }
}
