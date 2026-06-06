package me.legit.ffacore.combat;

import me.legit.ffacore.FFACore;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTask extends BukkitRunnable {

    private final FFACore plugin;

    public CombatTask(FFACore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        CombatManager combat = plugin.getCombatManager();
        combat.cleanup();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!combat.isTagged(p.getUniqueId())) continue;
            int time = combat.getRemaining(p.getUniqueId());
            if (time <= 0) continue;
            sendActionBar(p,"§cCombat: §f" + time + "s");
        }
    }

    private void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(
                new ChatComponentText(message),
                (byte) 2 // ACTION_BAR
        );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private String color(String s) {
        return s.replace("&", "§");
    }
}