package io.github._4drian3d.simplerepair.common.configuration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Configuration {
    private String itemNotDamageable = "El item no se puede reparar";
    private String itemWithoutDamage = "El item no tiene daño";
    private String itemRepaired = "El item fue reparado";
}
