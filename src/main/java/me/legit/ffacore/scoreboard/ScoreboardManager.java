package me.legit.ffacore.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public class ScoreboardManager {

    private final FFACore plugin;

    public ScoreboardManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void update(Player player) {

        Scoreboard board = player.getScoreboard();

        if (board == null || board == Bukkit.getScoreboardManager().getMainScoreboard()) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(board);
        }

        Objective obj = board.getObjective("ffa");

        if (obj == null) {
            obj = board.registerNewObjective("ffa", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        PlayerData data = plugin.getPlayerDataManager().get(player.getUniqueId());
        if (data == null) data = new PlayerData();

        String title = color(apply(player,
                plugin.getConfigManager().getScoreboard().get().getString("title", "")
        ));

        obj.setDisplayName(title);

        List<String> lines = plugin.getConfigManager()
                .getScoreboard()
                .get()
                .getStringList("lines");

        int score = lines.size();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String formatted = line
                    .replace("%kills%", String.valueOf(data.getKills()))
                    .replace("%deaths%", String.valueOf(data.getDeaths()))
                    .replace("%kdr%", String.format("%.2f", data.getKDR()))
                    .replace("%streak%", String.valueOf(data.getKillStreak()))
                    .replace("%coins%", String.valueOf(data.getCoins()));

            formatted = color(apply(player, formatted));
            formatted = formatted + ChatColor.values()[i % ChatColor.values().length];
            obj.getScore(formatted).setScore(score--);
        }
    }

    private String apply(Player p, String text) {
        if (text == null) return "";
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
                ? PlaceholderAPI.setPlaceholders(p, text)
                : text;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}