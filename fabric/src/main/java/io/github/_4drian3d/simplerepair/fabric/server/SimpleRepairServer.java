package io.github._4drian3d.simplerepair.fabric.server;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github._4drian3d.simplerepair.fabric.base.HandSuggestion;
import io.github._4drian3d.simplerepair.fabric.base.SimpleRepairFabric;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.InteractionHand;

import java.util.Locale;
import java.util.function.Predicate;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class SimpleRepairServer extends SimpleRepairFabric implements DedicatedServerModInitializer {
  @Override
  public void onInitializeServer() {
    this.initializeCommands();
  }

  @Override
  protected void registerRepairCommand(final String name, final CommandDispatcher<CommandSourceStack> dispatcher) {
    final LiteralArgumentBuilder<CommandSourceStack> cmd = literal(name)
        .requires(this.hasPermission("simplerepair.use"))
        .executes(context ->
            itemRepairExecution(context, InteractionHand.MAIN_HAND, 100))
        .then(argument("hand", StringArgumentType.word())
            .suggests(HandSuggestion.INSTANCE)
            .requires(this.hasPermission("simplerepair.us.handtype"))
            .executes(context -> itemRepairExecution(
                context,
                HANDS.get(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                100
            ))
            .then(argument("percentage", DoubleArgumentType.doubleArg(1, 100))
                    .requires(this.hasPermission("simplerepair.use.percentage"))
                    .executes(context -> itemRepairExecution(
                        context,
                        HANDS.get(context.getArgument("hand", String.class).toUpperCase(Locale.ROOT)),
                        DoubleArgumentType.getDouble(context, "percentage")
                    ))
            )
        );

    dispatcher.register(cmd);
  }

  private Predicate<CommandSourceStack> hasPermission(final String permission) {
    return src -> src.isPlayer() && Permissions.check(src, permission, 2);
  }
}
