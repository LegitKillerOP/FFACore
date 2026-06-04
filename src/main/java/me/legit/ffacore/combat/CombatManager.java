package me.legit.ffacore.combat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CombatManager {

    private final Map<UUID, Long> combatMap = new ConcurrentHashMap<>();
    private final int combatTimeSeconds;

    public CombatManager(int combatTimeSeconds) {
        this.combatTimeSeconds = combatTimeSeconds;
    }

    public void tag(UUID uuid) {
        combatMap.put(uuid, System.currentTimeMillis());
    }

    public boolean isTagged(UUID uuid) {
        Long time = combatMap.get(uuid);
        if (time == null) return false;

        return (System.currentTimeMillis() - time) < combatTimeSeconds * 1000L;
    }

    public int getRemaining(UUID uuid) {
        Long time = combatMap.get(uuid);
        if (time == null) return 0;

        long left = combatTimeSeconds - ((System.currentTimeMillis() - time) / 1000L);
        return (int) Math.max(left, 0);
    }

    public void remove(UUID uuid) {
        combatMap.remove(uuid);
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        combatMap.entrySet().removeIf(entry ->
                (now - entry.getValue()) >= combatTimeSeconds * 1000L
        );
    }
}