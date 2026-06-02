package me.legit.ffacore.kits;

import me.legit.ffacore.FFACore;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class KitManager {

    private final FFACore plugin;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(FFACore plugin){
        this.plugin = plugin;
        loadKits();
    }

    public void loadKits(){
        kits.clear();
        ConfigurationSection section = plugin.getConfigManager().getKits().get().getConfigurationSection("kits");
        if(section == null)
            return;
        for(String name : section.getKeys(false)){
            List<ItemStack> items = new ArrayList<>();
            List<String> itemList = section.getStringList(name + ".items");

            for(String item : itemList){
                String[] split = item.split(":");
                Material material = Material.valueOf(split[0]);
                int amount = split.length > 1? Integer.parseInt(split[1]) : 1;
                items.add(new ItemStack(material,amount));
            }

            kits.put(name.toLowerCase(),
                    new Kit(name,section.getString(name +".permission"),items)
            );
        }
    }

    public Kit getKit(String name){
        return kits.get(name.toLowerCase());
    }
}