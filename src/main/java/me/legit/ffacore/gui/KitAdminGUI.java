package me.legit.ffacore.gui;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KitAdminGUI {

    private final FFACore plugin;

    public KitAdminGUI(FFACore plugin){
        this.plugin = plugin;
    }

    public Inventory openMain(Player player){
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_AQUA + "Kit Admin");

        setBackground(inv, (short) 7);

        inv.setItem(10, buildItem(Material.EMERALD, ChatColor.GREEN + "Add Kit", ChatColor.GRAY + "Create a new kit."));
        inv.setItem(13, buildItem(Material.BOOK, ChatColor.YELLOW + "Edit Kits", ChatColor.GRAY + "Choose a kit to edit its items."));
        inv.setItem(16, buildItem(Material.REDSTONE, ChatColor.RED + "Delete Kit", ChatColor.GRAY + "Choose a kit to remove permanently."));
        inv.setItem(22, buildItem(Material.BARRIER, ChatColor.DARK_RED + "Close", ChatColor.GRAY + "Close the kit admin panel."));
        inv.setItem(4, buildItem(Material.PAPER, ChatColor.AQUA + "FFA Kit Manager", ChatColor.GRAY + "Use the buttons below to manage kits.", ChatColor.GRAY + "Click Add to create a new kit.", ChatColor.GRAY + "Edit lets you change kit items.", ChatColor.GRAY + "Delete removes the kit immediately."));

        player.openInventory(inv);
        return inv;
    }

    public Inventory openEditList(Player player){
        List<Kit> kits = new ArrayList<>(plugin.getKitManager().getKits().values());
        int size = 9;
        while (size < kits.size() + 9) size += 9;
        Inventory inv = Bukkit.createInventory(null, Math.max(18, size), ChatColor.AQUA + "Edit Kits");

        setBackground(inv, (short) 7);
        inv.setItem(inv.getSize() - 9, buildItem(Material.ARROW, ChatColor.YELLOW + "Back", ChatColor.GRAY + "Return to the main menu."));
        inv.setItem(inv.getSize() - 1, buildItem(Material.BARRIER, ChatColor.DARK_RED + "Close", ChatColor.GRAY + "Close the kit admin menu."));

        for (int i = 0; i < kits.size(); i++){
            Kit k = kits.get(i);
            inv.setItem(i, buildItem(Material.CHEST, ChatColor.GOLD + k.getName(), ChatColor.GRAY + "Click to edit this kit.", ChatColor.GRAY + "Items: " + k.getItems().size()));
        }

        player.openInventory(inv);
        return inv;
    }

    public Inventory openDeleteList(Player player){
        List<Kit> kits = new ArrayList<>(plugin.getKitManager().getKits().values());
        int size = 9;
        while (size < kits.size() + 9) size += 9;
        Inventory inv = Bukkit.createInventory(null, Math.max(18, size), ChatColor.RED + "Delete Kits");

        setBackground(inv, (short) 7);
        inv.setItem(inv.getSize() - 9, buildItem(Material.ARROW, ChatColor.YELLOW + "Back", ChatColor.GRAY + "Return to the main menu."));
        inv.setItem(inv.getSize() - 1, buildItem(Material.BARRIER, ChatColor.DARK_RED + "Close", ChatColor.GRAY + "Close the kit admin menu."));

        for (int i = 0; i < kits.size(); i++){
            Kit k = kits.get(i);
            inv.setItem(i, buildItem(Material.TNT, ChatColor.RED + k.getName(), ChatColor.GRAY + "Click to delete this kit.", ChatColor.GRAY + "This action cannot be undone."));
        }

        player.openInventory(inv);
        return inv;
    }

    public Inventory openKitEditor(Player player, String kitName){
        Kit kit = plugin.getKitManager().getKit(kitName);
        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.RED + "Editing: " + kitName);

        if (kit == null) {
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Kit not found.");
            return null;
        }

        if (kit != null){
            List<ItemStack> items = kit.getItems();
            for (int i = 0; i < items.size() && i < 45; i++){
                inv.setItem(i, items.get(i));
            }
        }

        for (int slot = 46; slot <= 52; slot++){
            inv.setItem(slot, buildItem(Material.STAINED_GLASS_PANE, " ", ""));
            ItemMeta paneMeta = inv.getItem(slot).getItemMeta();
            paneMeta.setDisplayName(" ");
            inv.getItem(slot).setItemMeta(paneMeta);
            inv.getItem(slot).setDurability((short) 15);
        }

        inv.setItem(45, buildItem(Material.ARROW, ChatColor.YELLOW + "Back", ChatColor.GRAY + "Return to kit list without saving."));
        inv.setItem(53, buildItem(Material.EMERALD, ChatColor.GREEN + "Save & Close", ChatColor.GRAY + "Save changes and close the editor."));

        player.openInventory(inv);
        return inv;
    }

    private ItemStack buildItem(Material material, String name, String... loreLines){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (loreLines != null && loreLines.length > 0){
            List<String> lore = new ArrayList<>();
            for (String line : loreLines) lore.add(line);
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private void setBackground(Inventory inv, short color){
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE,1,color);
        ItemMeta pm = pane.getItemMeta();
        pm.setDisplayName(" ");
        pane.setItemMeta(pm);
        for (int slot = 0; slot < inv.getSize(); slot++){
            if (inv.getItem(slot) == null){
                inv.setItem(slot, pane);
            }
        }
    }
}
