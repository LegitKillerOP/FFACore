package me.legit.ffacore.gui.kitselector;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import me.legit.ffacore.kits.KitCooldownManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitSelectorGUI {
    private final FFACore plugin;
    public KitSelectorGUI(FFACore plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§3Kit Selector");
        fillBorders(inv);
        int slot = 10;
        for (Kit kit : plugin.getKitManager().getKits().values()) {
            if (slot == 17 || slot == 18) slot += 2; // skip middle divider line
            if (slot >= 17) break;
            inv.setItem(slot, buildKitItem(player, kit));
            slot++;
        }
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1f, 1.2f);
    }

    private ItemStack buildKitItem(Player player, Kit kit) {
        KitCooldownManager cd = plugin.getKitCooldownManager();
        boolean hasPerm = kit.getPermission() == null || kit.getPermission().isEmpty() || player.hasPermission(kit.getPermission());
        boolean cooldown = cd.isOnCooldown(kit.getName(), player.getUniqueId());
        long sec = cd.getRemaining(kit.getName(), player.getUniqueId()) / 1000;
        Material mat;

        if (!hasPerm) {
            mat = Material.BARRIER;
        } else if (cooldown) {
            mat = Material.MAGMA_CREAM;
        } else {
            mat = Material.CHEST;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6" + kit.getName());
        List<String> lore = new ArrayList<>();
        lore.add("§7Click to select this kit");
        lore.add("");
        lore.add("§fItems: §a" + kit.getItems().size());

        if (!hasPerm) {
            lore.add("");
            lore.add("§cNo Permission");
            lore.add("§8" + kit.getPermission());
        }
        if (cooldown) {
            lore.add("");
            lore.add("§eCooldown: §f" + sec + "s");
        } else {
            lore.add("");
            lore.add("§aAvailable");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void fillBorders(Inventory inv) {
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(" ");
        pane.setItemMeta(meta);

        for (int i = 0; i < inv.getSize(); i++) {
            int row = i / 9;
            // border only (premium clean UI style)
            if (row == 0 || row == 2 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, pane);
            }
        }
    }
}