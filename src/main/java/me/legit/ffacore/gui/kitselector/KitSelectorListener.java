package me.legit.ffacore.gui.kitselector;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import me.legit.ffacore.kits.KitCooldownManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class KitSelectorListener implements Listener {

    private final FFACore plugin;

    public KitSelectorListener(FFACore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        if (e.getView() == null) return;
        if (!"§3Kit Selector".equals(e.getView().getTitle())) return;
        if (e.getClickedInventory() == null) return;
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;
        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;
        if (!item.getItemMeta().hasDisplayName()) return;
        String kitName = item.getItemMeta().getDisplayName().replace("§6", "");
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) return;

        Player p = player;
        // permission
        if (kit.getPermission() != null &&
                !kit.getPermission().isEmpty() &&
                !p.hasPermission(kit.getPermission())) {
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
            return;
        }

        // cooldown
        KitCooldownManager cd = plugin.getKitCooldownManager();

        if (cd.isOnCooldown(kit.getName(), p.getUniqueId())) {

            long sec = cd.getRemaining(kit.getName(), p.getUniqueId()) / 1000;

            p.sendMessage("§eCooldown: §f" + sec + "s");
            p.playSound(p.getLocation(), Sound.NOTE_BASS, 1f, 0.8f);
            return;
        }

        // apply kit
        plugin.getKitManager().applyKit(p, kit);

        cd.applyCooldown(kit.getName(), p.getUniqueId(), kit.getCooldownSeconds());

        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1f, 1.2f);

        p.closeInventory();
    }
}