package io.github._4drian3d.simplerepair.paper;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github._4drian3d.simplerepair.common.RepairLogic;
import io.github._4drian3d.simplerepair.common.RepairResult;
import io.github._4drian3d.simplerepair.common.configuration.ConfigurationContainer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public final class RepairBootstrap implements PluginBootstrap, RepairLogic<Player, EquipmentSlot> {
  private static final Map<String, EquipmentSlot> HANDS = Map.of(
      "HAND", EquipmentSlot.HAND,
      "OFF_HAND", EquipmentSlot.OFF_HAND
  );

  @Override
  public void bootstrap(final BootstrapContext context) {
    final ConfigurationContainer configurationContainer =
        ConfigurationContainer.load(context.getLogger(), context.getDataDirectory(), "config");

    context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
      final List<String> aliases = List.of("itemrepair", "repair");
      final LiteralCommandNode<CommandSourceStack> node = this.commandNode(configurationContainer);
      event.registrar().register(node, aliases);
    });
  }

  private LiteralCommandNode<CommandSourceStack> commandNode(final ConfigurationContainer container) {
    return Commands.literal("simplerepair")
        .requires(src -> src.getSender() instanceof Player p && p.hasPermission("simplerepair.use"))
        .then(Commands.argument("hand", StringArgumentType.word())
            .requires(src -> src.getSender().hasPermission("simplerepair.use.handtype"))
            .suggests((context, builder) -> {
              HANDS.forEach((hand, slot) -> builder.suggest(hand));
              return builder.buildFuture();
            })
            .executes(ctx -> {
              final Player player = (Player) ctx.getSource().getSender();
              final EquipmentSlot slot = HANDS.get(StringArgumentType.getString(ctx, "hand"));
              final RepairResult result = repairItem(player, slot, 100);
              this.sendResult(player, result, container.get());
              return Command.SINGLE_SUCCESS;
            })
            .then(Commands.argument("percentage", DoubleArgumentType.doubleArg(1, 100))
                .requires(src -> src.getSender().hasPermission("simplerepair.use.percentage"))
                .executes(ctx -> {
                  final Player player = (Player) ctx.getSource().getSender();
                  final EquipmentSlot slot = HANDS.get(StringArgumentType.getString(ctx, "hand"));
                  final double percentage = DoubleArgumentType.getDouble(ctx, "percentage");
                  final RepairResult result = repairItem(player, slot, percentage);
                  this.sendResult(player, result, container.get());
                  return Command.SINGLE_SUCCESS;
                })
            )
        )
        .executes(ctx -> {
          final Player player = (Player) ctx.getSource().getSender();
          RepairResult result = repairItem(player, EquipmentSlot.HAND, 100);
          this.sendResult(player, result, container.get());
          return Command.SINGLE_SUCCESS;
        }).build();
  }

  @Override
  public RepairResult repairItem(final Player source, final EquipmentSlot hand, final double percentage) {
    final ItemStack item = source.getInventory().getItem(hand);
    final short maxDurability = item.getType().getMaxDurability();
    if (maxDurability != 0) {
      final ItemMeta meta = item.getItemMeta();
      if (!(meta instanceof Damageable damageable)) {
        return RepairResult.CANNOT_BE_REPAIRED;
      }
      if (!damageable.hasDamage()) {
        return RepairResult.CANNOT_BE_REPAIRED;
      }
      final int damage = damageable.getDamage();
      if (damage == 0) {
        return RepairResult.ALREADY_REPAIRED;
      }
      final int calculatedDamage = this.calculatePercentage(percentage, maxDurability);
      final int newDamage = damage - calculatedDamage;
      damageable.setDamage(Math.max(newDamage, 0));
      item.setItemMeta(damageable);
      return RepairResult.REPAIRED;
    } else {
      return RepairResult.CANNOT_BE_REPAIRED;
    }
  }
}
