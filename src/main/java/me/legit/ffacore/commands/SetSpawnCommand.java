package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final FFACore plugin;

    public SetSpawnCommand(FFACore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        plugin.getSpawnManager().setSpawn(player.getLocation());
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Spawn location set.");
        return true;
    }
}