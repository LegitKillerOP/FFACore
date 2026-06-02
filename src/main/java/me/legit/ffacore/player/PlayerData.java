package me.legit.ffacore.player;

public class PlayerData {

    private int kills;
    private int deaths;
    private int killStreak;
    private int highestStreak;
    private int coins;
    private int xp;

    public PlayerData() {
        this.kills = 0;
        this.deaths = 0;
        this.killStreak = 0;
        this.highestStreak = 0;
        this.coins = 0;
        this.xp = 0;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
        killStreak++;

        if(killStreak > highestStreak){
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

    public void addCoins(int amount){
        coins += amount;
    }

    public double getKDR(){

        if(deaths == 0){
            return kills;
        }

        return (double) kills / deaths;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int amount){
        xp += amount;
    }
}