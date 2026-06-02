package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final FFACore plugin;

    public SpawnCommand(FFACore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
        if(!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        plugin.getSpawnManager().teleport(player);
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN +"Teleported to spawn.");
        return true;
    }
}