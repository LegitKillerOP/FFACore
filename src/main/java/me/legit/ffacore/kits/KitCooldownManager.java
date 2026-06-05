package me.legit.ffacore.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitCooldownManager {

    private final Map<String, Map<UUID, Long>> cooldowns = new HashMap<>();

    public boolean isOnCooldown(String kit, UUID uuid) {
        return getRemaining(kit, uuid) > 0;
    }

    public long getRemaining(String kit, UUID uuid) {
        Map<UUID, Long> map = cooldowns.get(kit.toLowerCase());
        if (map == null) return 0;

        return Math.max(0, map.getOrDefault(uuid, 0L) - System.currentTimeMillis());
    }

    public void applyCooldown(String kit, UUID uuid, long seconds) {
        cooldowns.computeIfAbsent(kit.toLowerCase(), k -> new HashMap<>())
                .put(uuid, System.currentTimeMillis() + (seconds * 1000));
    }
}