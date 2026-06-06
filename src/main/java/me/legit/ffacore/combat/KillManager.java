package me.legit.ffacore.combat;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.player.PlayerData;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.*;

public class KillManager {

    private final FFACore plugin;

    public KillManager(FFACore plugin) {
        this.plugin = plugin;
    }

    public void handleKill(Player victim, Player killer) {
        PlayerData victimData = plugin.getPlayerDataManager().get(victim.getUniqueId());
        if (victimData != null) {
            victimData.addDeath();
            victim.getActivePotionEffects().forEach(effect ->
                    victim.removePotionEffect(effect.getType()));
        }

        if (killer == null) return;

        PlayerData killerData = plugin.getPlayerDataManager().get(killer.getUniqueId());
        if (killerData == null) return;

        // Core rewards
        killerData.addKill();
        killerData.addCoins(5);
        killerData.addXp(10);

        plugin.getCombatManager().markKill(killer.getUniqueId());

        killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 4));
        killer.sendMessage("§a+1 Kill §7(§e+5 coins§7)");
        killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1f, 1.2f);

        handleKillstreak(killer, killerData.getKillStreak());
    }

    private void handleKillstreak(Player killer, int streak) {
        if (streak == 3) {
            killer.removePotionEffect(PotionEffectType.SPEED);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, 0));
            killer.sendMessage("§bSpeed I unlocked!");
        }

        if (streak == 5) {
            killer.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0));
            killer.sendMessage("§cStrength I unlocked!");
        }

        if (streak == 10) {
            Bukkit.broadcastMessage("§6§l" + killer.getName() + " is on a 10 killstreak!");
            spawnFirework(killer);
        }
    }

    private void spawnFirework(Player p) {
        Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .withFade(Color.RED)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .build());

        meta.setPower(1);
        fw.setFireworkMeta(meta);
    }
}