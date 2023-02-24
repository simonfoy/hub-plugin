package me.simonfoy.hubplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class HubPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new WelcomeMessage(this), this);
        getServer().getPluginManager().registerEvents(new ServerCompass(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
