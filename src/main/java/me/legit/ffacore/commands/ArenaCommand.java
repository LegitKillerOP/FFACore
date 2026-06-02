package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.command.*;

public class ArenaCommand implements CommandExecutor {

    private final FFACore plugin;
    public ArenaCommand(FFACore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args.length < 2){
            sender.sendMessage(plugin.getPrefix() + "§c/arena create <name>");
            return true;
        }
        if(args[0].equalsIgnoreCase("create")){
            plugin.getArenaManager().createArena(args[1]);
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Arena created.");
        }
        return true;
    }
}