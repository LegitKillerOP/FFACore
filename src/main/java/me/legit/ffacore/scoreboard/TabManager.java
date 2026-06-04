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
    private BukkitRunnable task;

    public TabManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void initPlayer(Player player) {
        updateTab(player, 0);
        updateNametag(player);
    }

    public void startTabUpdater() {
        stop();
        task = new BukkitRunnable() {
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
        };

        task.runTaskTimer(plugin, 0L, 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void updateTab(Player player, int sortIndex) {

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        int kills = data != null ? data.getKills() : 0;
        int deaths = data != null ? data.getDeaths() : 0;
        int streak = data != null ? data.getKillStreak() : 0;

        String header = get("tab.header");
        String footer = get("tab.footer");
        String tabName = get("tab.name", "%player_name%");

        header = apply(player, header, kills, deaths, streak);
        footer = apply(player, footer, kills, deaths, streak);
        tabName = apply(player, tabName, kills, deaths, streak);

        player.setPlayerListName(color(tabName));

        sendTab(player, color(header), color(footer));
    }

    public void removePlayer(Player player) {
        Scoreboard board = player.getScoreboard();

        if (board != null) {
            Team team = board.getTeam("ffa_" + player.getEntityId());
            if (team != null) {
                team.unregister();
            }
        }
    }

    private String apply(Player p, String text, int k, int d, int s) {
        if (text == null) return "";

        text = text.replace("%player%", p.getName())
                .replace("%kills%", String.valueOf(k))
                .replace("%deaths%", String.valueOf(d))
                .replace("%streak%", String.valueOf(s))
                .replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
                ? PlaceholderAPI.setPlaceholders(p, text)
                : text;
    }

    private String get(String path) {
        return plugin.getConfigManager().getScoreboard().get().getString(path, "");
    }

    private String get(String path, String def) {
        return plugin.getConfigManager().getScoreboard().get().getString(path, def);
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
        Scoreboard board = player.getScoreboard();

        if (board == null || board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }

        String teamName = "ffa_" + player.getEntityId();
        if (teamName.length() > 16) {
            teamName = teamName.substring(0, 16);
        }

        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
        }

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());

        int k = data != null ? data.getKills() : 0;
        int d = data != null ? data.getDeaths() : 0;
        int s = data != null ? data.getKillStreak() : 0;

        String prefix = apply(player, get("nametag.prefix"), k, d, s);
        String suffix = apply(player, get("nametag.suffix"), k, d, s);

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