package me.legit.ffacore.leaderboard;

import me.legit.ffacore.player.PlayerData;

public enum LeaderboardType {
    COINS("Coins"),
    KILLS("Kills"),
    DEATHS("Deaths"),
    KDR("KDR"),
    STREAK("Streak");

    private final String displayName;

    LeaderboardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getScore(PlayerData data) {
        switch (this) {
            case COINS:
                return data.getCoins();
            case KILLS:
                return data.getKills();
            case DEATHS:
                return data.getDeaths();
            case KDR:
                return data.getKDR();
            case STREAK:
                return data.getHighestStreak();
            default:
                return 0;
        }
    }

    public String formatScore(PlayerData data) {
        if (this == KDR) {
            return String.format("%.2f", getScore(data));
        }
        return String.valueOf((long) getScore(data));
    }

    public static LeaderboardType fromString(String value) {
        if (value == null) {
            return null;
        }

        switch (value.toLowerCase()) {
            case "coins":
                return COINS;
            case "kills":
                return KILLS;
            case "death":
            case "deaths":
                return DEATHS;
            case "kdr":
                return KDR;
            case "steak":
            case "streak":
                return STREAK;
            default:
                return null;
        }
    }
}