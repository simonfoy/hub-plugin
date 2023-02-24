package me.simonfoy.hubplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class WelcomeMessage implements Listener {

    private String serverName;

    public WelcomeMessage(HubPlugin hubPlugin) {
        serverName = hubPlugin.getConfig().getString("server-name");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.sendMessage(ChatColor.GREEN + "Welcome to " + serverName);
        player.sendTitle(ChatColor.GREEN + "Welcome to " + serverName, "", 10, 70, 20);
    }
}

