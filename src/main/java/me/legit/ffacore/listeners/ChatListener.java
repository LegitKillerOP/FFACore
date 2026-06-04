package me.legit.ffacore.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.legit.ffacore.FFACore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatListener implements Listener {

    private final FFACore plugin;
    private final Set<UUID> awaitingName = ConcurrentHashMap.newKeySet();

    public ChatListener(FFACore plugin) {
        this.plugin = plugin;
    }

    public void awaitName(Player player) {
        awaitingName.add(player.getUniqueId());
        player.sendMessage(plugin.getPrefix() + ChatColor.YELLOW + "Type the new kit name in chat.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        // KIT CREATION MODE
        if (awaitingName.remove(player.getUniqueId())) {
            e.setCancelled(true);

            String name = e.getMessage();

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (plugin.getKitManager().createKit(name)) {
                    player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Created kit " + name);
                } else {
                    player.sendMessage(plugin.getPrefix() + ChatColor.RED + "Failed to create kit (exists or invalid name)");
                }
            });

            return;
        }

        // CHAT FORMAT
        String format = plugin.getConfig().getString(
                "chat",
                "%luckperms_prefix%%player_name%&r: {message}"
        );

        format = format
                .replace("{message}", e.getMessage())
                .replace("%player%", player.getName());

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        e.setFormat(ChatColor.translateAlternateColorCodes('&', format));
    }
}