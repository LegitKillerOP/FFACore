package me.legit.ffacore.hologram;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class HologramLine {

    private final EntityArmorStand stand;

    public HologramLine(
            Location location,
            String text
    ) {

        stand = new EntityArmorStand(
                ((CraftWorld)
                        location.getWorld())
                        .getHandle()
        );

        stand.setLocation(
                location.getX(),
                location.getY(),
                location.getZ(),
                0,
                0
        );

        stand.setInvisible(true);

        stand.setCustomName(text);

        stand.setCustomNameVisible(true);

        stand.setGravity(false);

        stand.setSmall(true);
    }

    public void spawn(Player player) {

        PacketPlayOutSpawnEntityLiving packet =
                new PacketPlayOutSpawnEntityLiving(
                        stand
                );

        ((CraftPlayer)player)
                .getHandle()
                .playerConnection
                .sendPacket(packet);

    }

    public void destroy(Player player) {

        PacketPlayOutEntityDestroy packet =
                new PacketPlayOutEntityDestroy(
                        stand.getId()
                );

        ((CraftPlayer)player)
                .getHandle()
                .playerConnection
                .sendPacket(packet);

    }

}