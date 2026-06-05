package me.legit.ffacore.kits;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class Kit {

    private final String name;
    private final String permission;

    private List<ItemStack> items;
    private List<ItemStack> armor;
    private List<PotionEffect> effects;

    private long cooldownSeconds;

    public Kit(String name,
               String permission,
               List<ItemStack> items,
               List<ItemStack> armor,
               List<PotionEffect> effects,
               long cooldownSeconds) {

        this.name = name;
        this.permission = permission;
        this.items = items;
        this.armor = armor;
        this.effects = effects;
        this.cooldownSeconds = cooldownSeconds;
    }

    public String getName() { return name; }
    public String getPermission() { return permission; }

    public List<ItemStack> getItems() { return items; }
    public List<ItemStack> getArmor() { return armor; }
    public List<PotionEffect> getEffects() { return effects; }

    public long getCooldownSeconds() { return cooldownSeconds; }

    public void setItems(List<ItemStack> items) { this.items = items; }
    public void setArmor(List<ItemStack> armor) { this.armor = armor; }
    public void setEffects(List<PotionEffect> effects) { this.effects = effects; }
}