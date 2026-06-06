package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillStreakCommand implements CommandExecutor {

    private final FFACore plugin;

    public KillStreakCommand(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        PlayerData data = plugin.getPlayerDataManager().get(p.getUniqueId());

        p.sendMessage("§6Your Killstreak: §e" + data.getKillStreak());
        p.sendMessage("§6Highest: §e" + data.getHighestStreak());

        return true;
    }
}