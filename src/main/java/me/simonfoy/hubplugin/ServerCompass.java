package me.simonfoy.hubplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerCompass implements Listener {

    private HubPlugin hubPlugin;
    private ItemStack compassItem;
    private Map<Integer, String> serverSlots;

    public ServerCompass(HubPlugin hubPlugin) {
        this.hubPlugin = hubPlugin;
        hubPlugin.saveDefaultConfig();
        hubPlugin.reloadConfig();

        compassItem = new ItemStack(Material.COMPASS);
        ItemMeta meta = compassItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Server Selector");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click me to connect to a server");
        meta.setLore(lore);
        compassItem.setItemMeta(meta);

        serverSlots = new HashMap<>();
        ConfigurationSection section = hubPlugin.getConfig().getConfigurationSection("servers");
        for (String key : section.getKeys(false)) {
            int slot = section.getInt(key + ".slot");
            String name = section.getString(key + ".name");
            serverSlots.put(slot, name);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (item != null && item.equals(compassItem)) {

            Inventory inv = Bukkit.createInventory(player, 45, ChatColor.GRAY + "Server Selector");

            for (int slot : serverSlots.keySet()) {
                String serverName = serverSlots.get(slot);

                ItemStack serverItem;
                String material = hubPlugin.getConfig().getString("servers." + slot + ".item");
                if (material == null) {
                    serverItem = new ItemStack(Material.AIR);
                } else {
                    serverItem = new ItemStack(Material.getMaterial(material.toUpperCase()));
                }
                ItemMeta meta = serverItem.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + serverName);
                serverItem.setItemMeta(meta);

                inv.setItem(slot, serverItem);
            }

            player.openInventory(inv);
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().equals(compassItem)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem != null && e.getCurrentItem().equals(compassItem)) {
            e.setCancelled(true);
        } else if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
            String displayName = clickedItem.getItemMeta().getDisplayName();
            for (int slot : serverSlots.keySet()) {
                if (serverSlots.get(slot).equals(ChatColor.stripColor(displayName))) {

                    player.sendMessage(ChatColor.GREEN + "Sending to server " + ChatColor.YELLOW + displayName);
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.getInventory().setItem(0, compassItem);
    }
}

