package me.legit.ffacore.player;

public class PlayerData {

    private int kills;
    private int deaths;
    private int killStreak;
    private int highestStreak;
    private int coins;
    private int xp;

    public PlayerData() {}

    public PlayerData(int kills, int deaths, int killStreak, int highestStreak, int coins, int xp) {

        this.kills = kills;
        this.deaths = deaths;
        this.killStreak = killStreak;
        this.highestStreak = highestStreak;
        this.coins = coins;
        this.xp = xp;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
        killStreak++;

        if (killStreak > highestStreak) {
            highestStreak = killStreak;
        }
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
        killStreak = 0;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public int getHighestStreak() {
        return highestStreak;
    }

    public int getCoins() {
        return coins;
    }

    public void addCoins(int amount) {
        coins += amount;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int amount) {
        xp += amount;
    }

    public double getKDR() {

        if (deaths == 0) {
            return kills;
        }

        return (double) kills / deaths;
    }
}