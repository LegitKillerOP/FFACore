package me.legit.ffacore.scoreboard;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.List;

public class ScoreboardManager {

    private final FFACore plugin;

    public ScoreboardManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void update(Player player) {

        org.bukkit.scoreboard.Scoreboard board =
                Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective =
                board.registerNewObjective(
                        "ffa",
                        "dummy"
                );

        objective.setDisplaySlot(
                DisplaySlot.SIDEBAR
        );

        String title = plugin.getConfigManager()
                .getScoreboard()
                .get()
                .getString("title");

        if (title != null) {
            objective.setDisplayName(color(title));
        }

        PlayerData data =
                plugin.getPlayerDataManager()
                        .get(player.getUniqueId());

        if (data == null) {
            data = new PlayerData();
        }

        List<String> lines =
                plugin.getConfigManager()
                        .getScoreboard()
                        .get()
                        .getStringList("lines");

        int score = lines.size();

        for (String line : lines) {

            if (line == null) {
                score--;
                continue;
            }

            line = line
                    .replace("%kills%",
                            String.valueOf(data.getKills()))

                    .replace("%deaths%",
                            String.valueOf(data.getDeaths()))

                    .replace("%kdr%",
                            String.format(
                                    "%.2f",
                                    data.getKDR()
                            ))

                    .replace("%streak%",
                            String.valueOf(
                                    data.getKillStreak()
                            ))

                    .replace("%coins%",
                            String.valueOf(
                                    data.getCoins()
                            ));

            objective.getScore(
                    color(line)
            ).setScore(score);

            score--;
        }

        player.setScoreboard(board);

    }

    private String color(String text) {

        if (text == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes(
                '&',
                text
        );
    }

}