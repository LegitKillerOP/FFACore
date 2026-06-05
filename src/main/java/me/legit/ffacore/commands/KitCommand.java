package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import me.legit.ffacore.kits.KitCooldownManager;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

    private final FFACore plugin;
    public KitCommand(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length == 0) {
            plugin.getKitSelectorGUI().open(player);
            return true;
        }

        Kit kit = plugin.getKitManager().getKit(args[0]);
        if (kit == null) {
            player.sendMessage("§cKit not found.");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
            return true;
        }

        if (kit.getPermission() != null && !kit.getPermission().isEmpty()
                && !player.hasPermission(kit.getPermission())) {

            player.sendMessage("§cNo permission.");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1f, 1f);
            return true;
        }

        KitCooldownManager cd = plugin.getKitCooldownManager();

        if (cd.isOnCooldown(kit.getName(), player.getUniqueId())) {
            long sec = cd.getRemaining(kit.getName(), player.getUniqueId()) / 1000;
            player.sendMessage("§cCooldown: §f" + sec + "s");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 0.5f);
            return true;
        }

        plugin.getKitManager().applyKit(player, kit);
        cd.applyCooldown(kit.getName(), player.getUniqueId(), kit.getCooldownSeconds());

        return true;
    }
}