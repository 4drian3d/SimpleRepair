package io.github._4drian3d.simplerepair.paper;

import io.github._4drian3d.simplerepair.common.configuration.ConfigurationContainer;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleRepair extends JavaPlugin {

    private ConfigurationContainer configurationContainer;

    @Override
    public void onEnable() {
        this.configurationContainer = ConfigurationContainer.load(getSLF4JLogger(), getDataFolder().toPath(), "config");
        getServer().getCommandMap().register("simplerepair", new RepairCommand(this));
    }

    ConfigurationContainer configurationContainer() {
        return this.configurationContainer;
    }

}
