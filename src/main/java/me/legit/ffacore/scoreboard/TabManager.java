package me.legit.ffacore.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TabManager {

    private final FFACore plugin;

    public TabManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void startTabUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = Bukkit.getOnlinePlayers().stream()
                        .sorted(Comparator.comparingInt(p -> {
                            PlayerData data = plugin.getPlayerDataManager().get(p.getUniqueId());
                            return data != null ? -data.getKills() : Integer.MIN_VALUE;
                        }))
                        .collect(Collectors.toList());

                int index = 0;
                for (Player player : players) {
                    updateTab(player, index++);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateTab(Player player, int sortIndex) {
        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        int kills = data != null ? data.getKills() : 0;
        int deaths = data != null ? data.getDeaths() : 0;
        int streak = data != null ? data.getKillStreak() : 0;

        player.setPlayerListName("§" + Integer.toHexString(Math.min(sortIndex, 15)) + player.getName());

        String header = plugin.getConfigManager().getScoreboard().get().getString("tab.header", "")
                .replace("%player%", player.getName())
                .replace("%kills%", String.valueOf(kills))
                .replace("%deaths%", String.valueOf(deaths))
                .replace("%streak%", String.valueOf(streak));

        String footer = plugin.getConfigManager().getScoreboard().get().getString("tab.footer", "")
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        header = ChatColor.translateAlternateColorCodes('&', header);
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            header = PlaceholderAPI.setPlaceholders(player, header);
            footer = PlaceholderAPI.setPlaceholders(player, footer);
        }

        sendTab(player, header, footer);
    }

    private void sendTab(Player player, String header, String footer) {
        try {
            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

            Field a = packet.getClass().getDeclaredField("a");
            Field b = packet.getClass().getDeclaredField("b");

            a.setAccessible(true);
            b.setAccessible(true);

            a.set(packet, new ChatComponentText(header));
            b.set(packet, new ChatComponentText(footer));

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}