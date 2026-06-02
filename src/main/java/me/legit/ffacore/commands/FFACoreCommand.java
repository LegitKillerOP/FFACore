package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FFACoreCommand implements CommandExecutor {

    private final FFACore plugin;

    public FFACoreCommand(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            sender.sendMessage(plugin.getPrefix() + ChatColor.YELLOW +"/ffa reload");
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            plugin.getConfigManager().reloadAll();
            sender.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "FFACore configs reloaded.");
            return true;
        }

        return true;
    }
}