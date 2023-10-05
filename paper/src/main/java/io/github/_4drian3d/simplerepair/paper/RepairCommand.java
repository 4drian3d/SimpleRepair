package io.github._4drian3d.simplerepair.paper;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public final class RepairCommand extends Command implements PluginIdentifiableCommand {
    private final SimpleRepair plugin;

    RepairCommand(final SimpleRepair plugin) {
        super("simplerepair", "SimpleRepair command", "/simplerepair", List.of("itemrepair", "repair"));
        setPermission("simplerepair.use");
        this.plugin = plugin;
    }

    // /repair offhand 50

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
                    hand = EquipmentSlot.valueOf(args[0].toUpperCase(Locale.ROOT));
                    percentage = 100;
                }
                default -> {
                    hand = EquipmentSlot.valueOf(args[0].toUpperCase(Locale.ROOT));
                    percentage = Integer.parseInt(args[1]);
                }
            }
            repairOnHand(player, hand, percentage);
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
        //TODO: Fox this
        if (sender instanceof Player) {
            return switch (args.length) {
                case 0 -> List.of("HAND", "OFF_HAND");
                case 1 -> List.of("25", "50", "75", "100");
                default -> List.of();
            };
        }
        return List.of();
    }

    private void repairOnHand(Player player, EquipmentSlot hand, int percentage) {
        final ItemStack item = player.getInventory().getItem(hand);
        final short maxDurability = item.getType().getMaxDurability();
        if (maxDurability != 0) {
            item.editMeta(Damageable.class, meta -> {
                if (!meta.hasDamage()) {
                    return;
                }
                if (percentage == 100) {
                    meta.setDamage(0);
                    return;
                }
                final int damage = meta.getDamage();
                if (damage == 0) {
                    return;
                }
                final int calculatedDamage = calculateDamage(percentage, maxDurability);
                final int newDamage = damage - calculatedDamage;
                meta.setDamage(Math.max(newDamage, 0));
            });
        }
    }

    private int calculateDamage(double percentage, double durability) {
        return (int) (durability / 100 * percentage);
    }
}
