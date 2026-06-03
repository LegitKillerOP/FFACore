package me.legit.ffacore.gui;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GUIListener implements Listener {

    private final FFACore plugin;
    private final Map<UUID, String> editing = new HashMap<>();
    private final Set<UUID> saving = new HashSet<>();

    public GUIListener(FFACore plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        String title = e.getInventory().getTitle();

        if (title == null) return;

        KitAdminGUI gui = new KitAdminGUI(plugin);

        if (title.contains("Kit Admin")){
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            ItemStack curr = e.getCurrentItem();
            ItemMeta currMeta = curr.getItemMeta();
            String name = (currMeta != null && currMeta.hasDisplayName()) ? ChatColor.stripColor(currMeta.getDisplayName()) : "";
            if (name.isEmpty()) return;
            switch (name.toLowerCase()){
                case "add kit":
                    plugin.getChatListener().awaitName(p);
                    p.closeInventory();
                    break;
                case "edit kits":
                    gui.openEditList(p);
                    break;
                case "delete kit":
                    gui.openDeleteList(p);
                    break;
                case "close":
                    p.closeInventory();
                    break;
            }
            return;
        }

        if (title.contains("Edit Kits")){
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            ItemStack curr = e.getCurrentItem();
            ItemMeta currMeta = curr.getItemMeta();
            String name = (currMeta != null && currMeta.hasDisplayName()) ? ChatColor.stripColor(currMeta.getDisplayName()) : "";
            if (name.isEmpty()) return;
            if (name.equalsIgnoreCase("back")){
                gui.openMain(p);
                return;
            }
            if (name.equalsIgnoreCase("close")){
                p.closeInventory();
                return;
            }
            String kitName = name;
            gui.openKitEditor(p, kitName);
            editing.put(p.getUniqueId(), kitName);
            return;
        }

        if (title.contains("Delete Kits")){
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            ItemStack curr = e.getCurrentItem();
            ItemMeta currMeta = curr.getItemMeta();
            String name = (currMeta != null && currMeta.hasDisplayName()) ? ChatColor.stripColor(currMeta.getDisplayName()) : "";
            if (name.isEmpty()) return;
            if (name.equalsIgnoreCase("back")){
                gui.openMain(p);
                return;
            }
            if (name.equalsIgnoreCase("close")){
                p.closeInventory();
                return;
            }
            if (plugin.getKitManager().deleteKit(name)){
                p.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Deleted kit " + name);
                gui.openDeleteList(p);
            } else {
                p.sendMessage(plugin.getPrefix() + ChatColor.RED + "Failed to delete kit " + name);
            }
            return;
        }

        if (title.startsWith("Editing:" ) || title.contains("Editing:")){
            if (e.getCurrentItem() == null) return;
            ItemStack curr = e.getCurrentItem();
            ItemMeta currMeta = curr.getItemMeta();
            String name = (currMeta != null && currMeta.hasDisplayName()) ? ChatColor.stripColor(currMeta.getDisplayName()) : "";
            if (name.equalsIgnoreCase("save & close")){
                saving.add(p.getUniqueId());
                e.setCancelled(true);
                p.closeInventory();
                return;
            }
            if (name.equalsIgnoreCase("back")){
                editing.remove(p.getUniqueId());
                e.setCancelled(true);
                p.closeInventory();
                return;
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        String title = e.getInventory().getTitle();
        if (title == null) {
            return;
        }
        if (!title.contains("Editing:")) {
            return;
        }
        UUID uuid = p.getUniqueId();
        String kitName = editing.remove(uuid);
        if (kitName == null) {
            return;
        }
        if (!saving.remove(uuid)) {
            return; // closed without saving
        }
        java.util.List<ItemStack> items = new java.util.ArrayList<>();
        for (int slot = 0; slot < 45; slot++) {
            ItemStack item = e.getInventory().getItem(slot);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            items.add(item.clone());
        }
        plugin.getKitManager().updateKitItems(kitName, items);
        p.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Saved kit " + kitName);
    }
}
