package me.legit.ffacore.kits;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Kit {
    private final String name;
    private final String permission;
    private final List<ItemStack> items;

    public Kit(String name, String permission,List<ItemStack> items){
        this.name = name;
        this.permission = permission;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}