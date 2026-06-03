package me.legit.ffacore.listeners;

import me.legit.ffacore.FFACore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatListener implements Listener {

    private final FFACore plugin;
    private final Set<UUID> awaitingName = new HashSet<>();

    public ChatListener(FFACore plugin){
        this.plugin = plugin;
    }

    public void awaitName(Player player){
        awaitingName.add(player.getUniqueId());
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Type the new kit name in chat.");
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        if (!awaitingName.contains(p.getUniqueId())) {
            return;
        }
        e.setCancelled(true);
        awaitingName.remove(p.getUniqueId());
        String name = e.getMessage();
        if (plugin.getKitManager().createKit(name)){
            p.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Created kit " + name);
        } else {
            p.sendMessage(plugin.getPrefix() + ChatColor.RED + "Failed to create kit (exists or invalid name)");
        }
    }
}
