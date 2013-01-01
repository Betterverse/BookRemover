package net.betterverse.bookremover;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class BookRemover extends JavaPlugin implements Listener {
    List<String> illegalNames;
    
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        illegalNames = getConfig().getStringList("bannedBooks");
        if (illegalNames.isEmpty()) {
            illegalNames.add("Chasing Passion");
        }
        getConfig().set("bannedBooks", illegalNames);
        saveConfig();
    }
    
    public void onDisable() {
        getConfig().set("bannedBooks", illegalNames);
        saveConfig();
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        scanInventory((Player)e.getPlayer(), e.getInventory());
        scanInventory((Player)e.getPlayer(), e.getPlayer().getInventory());
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        scanInventory((Player)e.getPlayer(), e.getInventory());
        scanInventory((Player)e.getPlayer(), e.getPlayer().getInventory());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        scanInventory(e.getPlayer(), e.getPlayer().getInventory());
        scanInventory(e.getPlayer(), e.getPlayer().getInventory());

    }
    
    public void modMessage(Player player, String book) {
        for (Player p : getServer().getOnlinePlayers()) {
            if (p.hasPermission("bookremover.mod")) {
                p.sendMessage(ChatColor.GOLD+"[BookRemover] "+ChatColor.RED+player.getName()+" found an illegal book: "+ChatColor.WHITE+book);
            }
        }
    }
    
    
    public void scanInventory(Player p, Inventory inventory) {
        List<Integer> illegal = new ArrayList<Integer>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack current = inventory.getItem(i);
            if (current == null) {
                continue;
            }
            if (current.getType() == Material.WRITTEN_BOOK) {
                BookMeta meta = (BookMeta)current.getItemMeta();
                for (String s : illegalNames) {
                    if (meta.getTitle().equalsIgnoreCase(s)) {
                        illegal.add(i);
                        modMessage(p, meta.getTitle());
                    }
                }
            }
        }
        for (int i : illegal) {
            inventory.setItem(i, null);
        }
    }

}
