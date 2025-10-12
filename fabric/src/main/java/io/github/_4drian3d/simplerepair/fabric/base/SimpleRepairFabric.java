package io.github._4drian3d.simplerepair.fabric.base;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github._4drian3d.simplerepair.common.RepairLogic;
import io.github._4drian3d.simplerepair.common.RepairResult;
import io.github._4drian3d.simplerepair.common.configuration.ConfigurationContainer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public abstract class SimpleRepairFabric implements RepairLogic<Player, InteractionHand> {
  public static final Logger LOGGER = LoggerFactory.getLogger("SimpleRepair");
  public static final Map<String, InteractionHand> HANDS = Map.of(
      "MAIN_HAND", InteractionHand.MAIN_HAND,
      "OFF_HAND", InteractionHand.OFF_HAND
  );
  protected final ConfigurationContainer container = ConfigurationContainer.load(
      LOGGER, FabricLoader.getInstance().getConfigDir(), "simplerepair"
  );

  protected abstract void registerRepairCommand(String name, CommandDispatcher<CommandSourceStack> dispatcher);

  protected void initializeCommands() {
    CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
      this.registerRepairCommand("repair", dispatcher);
      this.registerRepairCommand("simplerepair", dispatcher);
      this.registerRepairCommand("itemrepair", dispatcher);
    });
  }

  @Override
  public RepairResult repairItem(final Player source, final InteractionHand hand, final double percentage) {
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

  protected int itemRepairExecution(final CommandContext<CommandSourceStack> context, final InteractionHand hand, final double percentage) {
    final RepairResult result = repairItem(requireNonNull(context.getSource().getPlayer()), hand, percentage);
    this.sendResult(context.getSource(), result, requireNonNull(container).get());
    return Command.SINGLE_SUCCESS;
  }
}
