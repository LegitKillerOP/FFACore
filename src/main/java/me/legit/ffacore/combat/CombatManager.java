package me.legit.ffacore.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final Map<UUID, Long> combatMap = new HashMap<>();
    private final int combatTime = 15;

    public void tag(UUID uuid) {
        combatMap.put(uuid, System.currentTimeMillis());
    }

    public boolean isTagged(UUID uuid) {
        if (!combatMap.containsKey(uuid)) {
            return false;
        }
        long time = combatMap.get(uuid);
        return (System.currentTimeMillis() - time) < (combatTime * 1000);
    }

    public int getRemaining(UUID uuid) {
        if (!isTagged(uuid)) {
            return 0;
        }
        long time = combatMap.get(uuid);
        return combatTime - (int)((System.currentTimeMillis() - time) / 1000);
    }

    public void remove(UUID uuid) {
        combatMap.remove(uuid);
    }
}