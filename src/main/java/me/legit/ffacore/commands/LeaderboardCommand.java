package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.leaderboard.LeaderboardManager;
import me.legit.ffacore.leaderboard.LeaderboardType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaderboardCommand implements CommandExecutor {

    private final FFACore plugin;

    public LeaderboardCommand(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ffacore.admin") && !sender.isOp()) {
            sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission.");
            return true;
        }

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        LeaderboardManager manager = plugin.getLeaderboardManager();
        String sub = args[0].toLowerCase();

        switch (sub) {
            case "add":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can add a leaderboard.");
                    return true;
                }

                if (args.length != 3) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Usage: /leaderboard add <id> <type>");
                    sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Types: coins, kills, death, kdr, steak");
                    return true;
                }

                Player player = (Player) sender;
                String id = args[1].toLowerCase();
                LeaderboardType type = LeaderboardType.fromString(args[2]);

                if (type == null) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Unknown type. Use coins, kills, death, kdr, steak.");
                    return true;
                }

                if (!manager.createLeaderboard(id, type, player.getLocation())) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "A leaderboard with that id already exists.");
                    return true;
                }

                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Leaderboard " + id + " created at your location.");
                return true;
            case "movehere":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Only players can move a leaderboard.");
                    return true;
                }

                if (args.length != 2) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Usage: /leaderboard movehere <id>");
                    return true;
                }

                if (!manager.moveLeaderboard(args[1].toLowerCase(), ((Player) sender).getLocation())) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Leaderboard not found.");
                    return true;
                }

                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Leaderboard moved to your location.");
                return true;
            case "reload":
                manager.reloadLeaderboards();
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Leaderboards reloaded.");
                return true;
            case "remove":
                if (args.length != 2) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Usage: /leaderboard remove <id>");
                    return true;
                }

                if (!manager.removeLeaderboard(args[1].toLowerCase())) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.RED + "Leaderboard not found.");
                    return true;
                }

                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Leaderboard removed.");
                return true;
            case "list":
                List<String> list = manager.listLeaderboards();

                if (list.isEmpty()) {
                    sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "No leaderboards configured.");
                    return true;
                }

                sender.sendMessage(plugin.getPrefix() + ChatColor.GOLD + "Configured leaderboards:");
                for (String line : list) {
                    sender.sendMessage(ChatColor.GRAY + "- " + line);
                }
                return true;
            case "update":
                manager.updateAll();
                sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Leaderboards updated.");
                return true;
            default:
                sendUsage(sender);
                return true;
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Leaderboard commands:");
        sender.sendMessage(ChatColor.YELLOW + "/leaderboard add <id> <type>");
        sender.sendMessage(ChatColor.YELLOW + "/leaderboard movehere <id>");
        sender.sendMessage(ChatColor.YELLOW + "/leaderboard remove <id>");
        sender.sendMessage(ChatColor.YELLOW + "/leaderboard list");
        sender.sendMessage(ChatColor.YELLOW + "/leaderboard reload");
        sender.sendMessage(ChatColor.YELLOW + "/leaderboard update");
    }
}
