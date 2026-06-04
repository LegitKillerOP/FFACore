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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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

                List<Player> players = Bukkit.getOnlinePlayers()
                        .stream()
                        .sorted(Comparator.comparingInt(p -> {
                            PlayerData data = plugin.getPlayerDataManager().get(p.getUniqueId());
                            return data != null ? -data.getKills() : Integer.MIN_VALUE;
                        }))
                        .collect(Collectors.toList());

                int index = 0;
                for (Player player : players) {
                    updateTab(player, index++);
                    updateNametag(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void updateTab(Player player, int sortIndex) {

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        int kills = data != null ? data.getKills() : 0;
        int deaths = data != null ? data.getDeaths() : 0;
        int streak = data != null ? data.getKillStreak() : 0;

        String header = getConfig("tab.header");
        String footer = getConfig("tab.footer");
        String tabName = getConfig("tab.name", "%player_name%");

        header = applyPlaceholders(player, header, kills, deaths, streak);
        footer = applyPlaceholders(player, footer, kills, deaths, streak);
        tabName = applyPlaceholders(player, tabName, kills, deaths, streak);

        player.setPlayerListName(color(tabName));

        sendTab(player, color(header), color(footer));
    }

    private String applyPlaceholders(Player player, String text, int kills, int deaths, int streak) {

        if (text == null) return "";

        text = text
                .replace("%player%", player.getName())
                .replace("%kills%", String.valueOf(kills))
                .replace("%deaths%", String.valueOf(deaths))
                .replace("%streak%", String.valueOf(streak))
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    private String getConfig(String path) {
        return plugin.getConfigManager()
                .getScoreboard()
                .get()
                .getString(path, "");
    }

    private String getConfig(String path, String def) {
        return plugin.getConfigManager()
                .getScoreboard()
                .get()
                .getString(path, def);
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
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

    private void updateNametag(Player player) {

        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName = "ffa_" + player.getEntityId();

        Team team = board.getTeam(teamName);

        if (team == null) {
            team = board.registerNewTeam(teamName);
        }

        String prefix = getConfig("nametag.prefix");
        String suffix = getConfig("nametag.suffix");

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        int kills = data != null ? data.getKills() : 0;
        int deaths = data != null ? data.getDeaths() : 0;
        int streak = data != null ? data.getKillStreak() : 0;

        prefix = applyPlaceholders(player, prefix, kills, deaths, streak);
        suffix = applyPlaceholders(player, suffix, kills, deaths, streak);

        prefix = color(prefix);
        suffix = color(suffix);

        if (prefix.length() > 16) prefix = prefix.substring(0, 16);
        if (suffix.length() > 16) suffix = suffix.substring(0, 16);

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }
}