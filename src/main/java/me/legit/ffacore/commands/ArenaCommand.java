package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.arena.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ArenaCommand implements CommandExecutor {

    private final FFACore plugin;

    public ArenaCommand(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!sender.hasPermission("ffacore.admin")) {
            sender.sendMessage(plugin.getPrefix() + "§cNo permission.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) return usage(sender, "/arena create <name>");
            String name = args[1].toLowerCase();
            if (plugin.getArenaManager().getArena(name) != null) {
                sender.sendMessage(plugin.getPrefix() + "§cArena already exists.");
                return true;
            }
            plugin.getArenaManager().createArena(name);
            sender.sendMessage(plugin.getPrefix() + "§aArena created: §f" + name);
            return true;
        }

        if (args[0].equalsIgnoreCase("setmin")) {
            if (!(sender instanceof Player)) return onlyPlayer(sender);
            if (args.length < 2) return usage(sender, "/arena setmin <name>");
            Player p = (Player) sender;
            Arena arena = plugin.getArenaManager().getArena(args[1]);
            if (arena == null) {
                sender.sendMessage(plugin.getPrefix() + "§cArena not found.");
                return true;
            }
            arena.setMin(p.getLocation());
            plugin.getArenaManager().saveArena(arena);
            sender.sendMessage(plugin.getPrefix() + "§aMin location set.");
            return true;
        }

        if (args[0].equalsIgnoreCase("setmax")) {
            if (!(sender instanceof Player)) return onlyPlayer(sender);
            if (args.length < 2) return usage(sender, "/arena setmax <name>");
            Player p = (Player) sender;
            Arena arena = plugin.getArenaManager().getArena(args[1]);
            if (arena == null) {
                sender.sendMessage(plugin.getPrefix() + "§cArena not found.");
                return true;
            }
            arena.setMax(p.getLocation());
            plugin.getArenaManager().saveArena(arena);
            sender.sendMessage(plugin.getPrefix() + "§aMax location set.");
            return true;
        }

        if (args[0].equalsIgnoreCase("join")) {
            if (!(sender instanceof Player)) {
                return onlyPlayer(sender);
            }
            Player player = (Player) sender;
            Arena active = plugin.getArenaManager().getActiveArena();
            if (active == null) {
                player.sendMessage(plugin.getPrefix() + "§cNo active arena is set.");
                return true;
            }
            boolean success = plugin.getArenaManager().sendToArena(player);
            if (success) {
                player.sendMessage(plugin.getPrefix() + "§aJoined arena §f" + active.getName());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                return usage(sender, "/arena delete <name>");
            }
            Arena arena = plugin.getArenaManager().getArena(args[1]);
            if (arena == null) {
                sender.sendMessage(plugin.getPrefix() + "§cArena not found.");
                return true;
            }
            if (plugin.getArenaManager().getActiveArena() != null
                    && plugin.getArenaManager().getActiveArena().getName()
                    .equalsIgnoreCase(arena.getName())) {
                plugin.getArenaManager().setActiveArena(null);
            }
            plugin.getArenaManager().deleteArena(arena.getName());
            sender.sendMessage(
                    plugin.getPrefix() +
                            "§aDeleted arena §f" +
                            arena.getName()
            );
            return true;
        }

        if (args[0].equalsIgnoreCase("setactive")) {
            if (args.length < 2) return usage(sender, "/arena setactive <name>");
            Arena arena = plugin.getArenaManager().getArena(args[1]);
            if (arena == null) {
                sender.sendMessage(plugin.getPrefix() + "§cArena not found.");
                return true;
            }
            plugin.getArenaManager().setActiveArena(arena.getName());
            sender.sendMessage(plugin.getPrefix() + "§aActive arena set to §f" + arena.getName());
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            sender.sendMessage("§8§m------------------------------");
            sender.sendMessage("§b§lArena Overview");
            sender.sendMessage("§7Total Arenas: §f" + plugin.getArenaManager().getArenas().size());
            sender.sendMessage("§8------------------------------");

            Arena active = plugin.getArenaManager().getActiveArena();
            sender.sendMessage("§eActive Arena: §f" +
                    (active != null ? active.getName() : "§cNone"));
            sender.sendMessage("§8------------------------------");

            plugin.getArenaManager().getArenas().forEach((name, arena) -> {
                boolean isActive = active != null &&
                        active.getName().equalsIgnoreCase(arena.getName());
                boolean minSet = arena.getMin() != null;
                boolean maxSet = arena.getMax() != null;
                String status;
                if (!minSet && !maxSet) {
                    status = "§cNot Setup";
                } else if (!minSet || !maxSet) {
                    status = "§6Partial Setup";
                } else {
                    status = "§aReady";
                }
                sender.sendMessage("§7• §f" + arena.getName() + (isActive ? " §a(Active)" : ""));
                sender.sendMessage("   §8└ §7Status: " + status);
                sender.sendMessage("   §8└ §7Min: §f" + (minSet ? format(arena.getMin()) : "§cNot set"));
                sender.sendMessage("   §8└ §7Max: §f" + (maxSet ? format(arena.getMax()) : "§cNot set"));
            });
            sender.sendMessage("§8§m------------------------------");
            return true;
        }

        return sendHelp(sender);
    }

    private boolean sendHelp(CommandSender sender) {
        sender.sendMessage("§8§m-------------------");
        sender.sendMessage("§bArena Commands:");
        sender.sendMessage("§f/arena create <name>");
        sender.sendMessage("§f/arena join");
        sender.sendMessage("§f/arena delete");
        sender.sendMessage("§f/arena setmin <name>");
        sender.sendMessage("§f/arena setmax <name>");
        sender.sendMessage("§f/arena setactive <name>");
        sender.sendMessage("§f/arena list");
        sender.sendMessage("§8§m-------------------");
        return true;
    }

    private boolean usage(CommandSender s, String msg) {
        s.sendMessage(plugin.getPrefix() + "§cUsage: " + msg);
        return true;
    }

    private boolean onlyPlayer(CommandSender s) {
        s.sendMessage(plugin.getPrefix() + "§cOnly players can use this.");
        return true;
    }

    private String format(Location loc) {
        return loc.getWorld().getName() + " §8| §f"
                + loc.getBlockX() + ", "
                + loc.getBlockY() + ", "
                + loc.getBlockZ();
    }
}