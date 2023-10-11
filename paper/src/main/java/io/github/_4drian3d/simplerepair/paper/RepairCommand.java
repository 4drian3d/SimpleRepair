package io.github._4drian3d.simplerepair.paper;

import io.github._4drian3d.simplerepair.common.RepairLogic;
import io.github._4drian3d.simplerepair.common.RepairResult;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class RepairCommand extends Command implements PluginIdentifiableCommand, RepairLogic<Player, EquipmentSlot> {
    private static final Map<String, EquipmentSlot> HANDS = Map.of(
            "HAND", EquipmentSlot.HAND,
            "OFF_HAND", EquipmentSlot.OFF_HAND
    );

    private final SimpleRepair plugin;

    public RepairCommand(final SimpleRepair plugin) {
        super("simplerepair", "Repair some item", "/repair <hand> <percentage>", List.of("itemrepair", "repair"));
        this.plugin = plugin;
    }

    @Override
    public RepairResult repairItem(Player source, EquipmentSlot hand, double percentage) {
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

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            final EquipmentSlot hand;
            final int percentage;
            switch (args.length) {
                case 0 -> {
                    hand = EquipmentSlot.HAND;
                    percentage = 100;
                }
                case 1 -> {
                    hand = HANDS.get(args[0].toUpperCase(Locale.ROOT));
                    percentage = 100;
                }
                default -> {
                    hand = HANDS.get(args[0].toUpperCase(Locale.ROOT));
                    percentage = Integer.parseInt(args[1]);
                }
            }
            RepairResult result = repairItem(player, hand, percentage);
            this.sendResult(player, result, plugin.configurationContainer().get());
            return true;
        }
        return false;
    }

    @Override
    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (sender instanceof Player) {
            return switch (args.length) {
                case 0, 1 -> List.of("HAND", "OFF_HAND");
                case 2 -> List.of("25", "50", "75", "100");
                default -> List.of();
            };
        }
        return List.of();
    }
}
