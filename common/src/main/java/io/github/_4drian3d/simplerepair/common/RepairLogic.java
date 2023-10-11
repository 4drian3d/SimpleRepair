package io.github._4drian3d.simplerepair.common;

import io.github._4drian3d.simplerepair.common.configuration.Configuration;
import net.kyori.adventure.audience.Audience;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

public interface RepairLogic<S, I> {
    RepairResult repairItem(S source, I hand, double percentage);

    default int calculatePercentage(double percentage, double durability) {
        return (int) (durability / 100 * percentage);
    }

    default void sendResult(Audience audience, RepairResult result, Configuration configuration) {
        audience.sendMessage(miniMessage().deserialize(switch (result) {
            case REPAIRED -> configuration.itemRepaired();
            case ALREADY_REPAIRED -> configuration.alreadyRepaired();
            case CANNOT_BE_REPAIRED -> configuration.nonRepairableItem();
        }));
    }
}
