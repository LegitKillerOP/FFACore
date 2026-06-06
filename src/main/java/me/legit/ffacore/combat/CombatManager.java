package me.legit.ffacore.combat;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final int combatTimeSeconds;

    private final Map<UUID, Long> combatMap = new ConcurrentHashMap<>();
    private final Map<UUID, UUID> lastHit = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Double>> damageMap = new ConcurrentHashMap<>();

    public CombatManager(int combatTimeSeconds) {
        this.combatTimeSeconds = combatTimeSeconds;
    }

    public void tag(UUID uuid) {
        combatMap.put(uuid, System.currentTimeMillis());
    }

    public boolean isTagged(UUID uuid) {
        Long time = combatMap.get(uuid);
        return time != null && (System.currentTimeMillis() - time) < combatTimeSeconds * 1000L;
    }

    public int getRemaining(UUID uuid) {
        Long time = combatMap.get(uuid);
        if (time == null) return 0;

        long left = combatTimeSeconds - ((System.currentTimeMillis() - time) / 1000L);
        return (int) Math.max(left, 0);
    }

    public void remove(UUID uuid) {
        combatMap.remove(uuid);
        lastHit.remove(uuid);
        damageMap.remove(uuid);
    }

    public void setLastHit(UUID victim, UUID attacker) {
        if (victim == null || attacker == null) return;
        lastHit.put(victim, attacker);
    }

    public UUID getLastHit(UUID victim) {
        return lastHit.get(victim);
    }

    public void addDamage(UUID victim, UUID damager, double dmg) {
        damageMap.computeIfAbsent(victim, k -> new HashMap<>())
                .merge(damager, dmg, Double::sum);
    }

    public UUID getTopAssister(UUID victim, UUID killer) {
        Map<UUID, Double> map = damageMap.get(victim);
        if (map == null) return null;

        UUID best = null;
        double bestDmg = 0;

        for (Map.Entry<UUID, Double> entry : map.entrySet()) {
            if (entry.getKey().equals(killer)) continue;
            if (entry.getValue() > bestDmg) {
                bestDmg = entry.getValue();
                best = entry.getKey();
            }
        }
        return best;
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        combatMap.entrySet().removeIf(e ->
                now - e.getValue() > combatTimeSeconds * 1000L
        );

        damageMap.entrySet().removeIf(e ->
                !isTagged(e.getKey())
        );
    }
}