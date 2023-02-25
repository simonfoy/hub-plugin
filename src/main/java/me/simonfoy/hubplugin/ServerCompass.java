package me.simonfoy.hubplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
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
    private Map<Integer, String> serverIds;

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
        serverIds = new HashMap<>();
        ConfigurationSection serversSection = hubPlugin.getConfig().getConfigurationSection("servers");
        for (String serverId : serversSection.getKeys(false)) {
            ConfigurationSection serverSection = serversSection.getConfigurationSection(serverId);
            int slot = serverSection.getInt("slot");
            String name = serverSection.getString("name");
            serverSlots.put(slot, name);
            serverIds.put(slot, serverId);
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
                String serverId = serverIds.get(slot);

                ItemStack serverItem;
                String material = hubPlugin.getConfig().getString("servers." + serverId + ".item");
                if (material == null) {
                    serverItem = new ItemStack(Material.AIR);
                } else {
                    serverItem = new ItemStack(Material.getMaterial(material.toUpperCase()));
                }
                ItemMeta meta = serverItem.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + serverName);
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Click to connect to " + serverName);
                meta.setLore(lore);
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
    public void onNumberKeyInventoryClick(InventoryClickEvent event) {
        if (event.getClick() == ClickType.NUMBER_KEY) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        ItemStack item = event.getOffHandItem();
        if (item != null && item.getType() == Material.COMPASS && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.YELLOW + "Server Selector")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        int slot = hubPlugin.getConfig().getInt("server-compass-slot");

        boolean hasCompassItem = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(compassItem)) {
                hasCompassItem = true;
                break;
            }
        }

        if (!hasCompassItem) {
            player.getInventory().setItem(slot, compassItem);
        }
    }
}

