package io.github._4drian3d.simplerepair.common.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public class Configuration {
    @Comment("Message to send when you want to repair an already repaired item")
    private String alreadyRepaired = "<red>The item is already repaired";
    @Comment("Message to be sent when the item cannot be repaired")
    private String nonRepairableItem = "This item cannot be repaired";
    @Comment("Message to be sent when the item has been repaired successfully")
    private String itemRepaired = "<green>The item was repaired";

    public String alreadyRepaired() {
        return alreadyRepaired;
    }

    public String nonRepairableItem() {
        return nonRepairableItem;
    }

    public String itemRepaired() {
        return itemRepaired;
    }
}
