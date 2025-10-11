package io.github._4drian3d.simplerepair.fabric.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github._4drian3d.simplerepair.fabric.base.HandSuggestion;
import io.github._4drian3d.simplerepair.fabric.base.SimpleRepairFabric;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.InteractionHand;

import java.util.Locale;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class SimpleRepairClient extends SimpleRepairFabric implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    this.initializeCommands();
  }

  @Override
  protected void registerRepairCommand(final String name, final CommandDispatcher<CommandSourceStack> dispatcher) {
    final LiteralArgumentBuilder<CommandSourceStack> cmd = literal(name)
        .requires(src -> src.isPlayer() && Minecraft.getInstance().hasSingleplayerServer())
        .then(argument("hand", StringArgumentType.word())
            .suggests(HandSuggestion.INSTANCE)
            .then(argument("percentage", DoubleArgumentType.doubleArg(1, 100))
                .executes(context -> itemRepairExecution(
                    context,
                    HANDS.get(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                    DoubleArgumentType.getDouble(context, "percentage")
                ))
            )
            .executes(context -> itemRepairExecution(
                context,
                HANDS.get(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                100
            ))
        )
        .executes(context ->
            itemRepairExecution(context, InteractionHand.MAIN_HAND, 100));

    dispatcher.register(cmd);
  }
}
