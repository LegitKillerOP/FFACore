package me.legit.ffacore.kits;

import me.legit.ffacore.FFACore;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class KitManager {

    private final FFACore plugin;
    private final Map<String, Kit> kits = new HashMap<>();

    public KitManager(FFACore plugin) {
        this.plugin = plugin;
        loadKits();
    }

    public void loadKits() {
        kits.clear();

        ConfigurationSection section =
                plugin.getConfigManager().getKits().get().getConfigurationSection("kits");

        if (section == null) return;

        for (String name : section.getKeys(false)) {

            ConfigurationSection kitSec = section.getConfigurationSection(name);
            if (kitSec == null) continue;

            List<ItemStack> items = (List<ItemStack>) kitSec.getList("items", new ArrayList<>());
            List<ItemStack> armor = (List<ItemStack>) kitSec.getList("armor", new ArrayList<>());
            List<PotionEffect> effects = (List<PotionEffect>) kitSec.getList("effects", new ArrayList<>());

            long cooldown = kitSec.getLong("cooldown", 0);

            String permission = kitSec.getString("permission");

            kits.put(name.toLowerCase(),
                    new Kit(name, permission, items, armor, effects, cooldown));
        }
    }

    public void applyKit(Player player, Kit kit) {
        if (player == null || kit == null) return;
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        if (kit.getItems() != null) {
            for (ItemStack item : kit.getItems()) {
                if (item != null) {
                    player.getInventory().addItem(item);
                }
            }
        }
        if (kit.getArmor() != null && !kit.getArmor().isEmpty()) { //todo: fix this
            ItemStack[] armor = new ItemStack[4];
            for (ItemStack item : kit.getArmor()) {
                if (item == null) continue;
                switch (item.getType()) {

                    case DIAMOND_HELMET:
                    case IRON_HELMET:
                    case GOLD_HELMET:
                    case LEATHER_HELMET:
                        armor[3] = item;
                        break;

                    case DIAMOND_CHESTPLATE:
                    case IRON_CHESTPLATE:
                    case GOLD_CHESTPLATE:
                    case LEATHER_CHESTPLATE:
                        armor[2] = item;
                        break;

                    case DIAMOND_LEGGINGS:
                    case IRON_LEGGINGS:
                    case GOLD_LEGGINGS:
                    case LEATHER_LEGGINGS:
                        armor[1] = item;
                        break;

                    case DIAMOND_BOOTS:
                    case IRON_BOOTS:
                    case GOLD_BOOTS:
                    case LEATHER_BOOTS:
                        armor[0] = item;
                        break;

                    default:
                        break;
                }
            }

            org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                player.getInventory().setArmorContents(armor);
                player.updateInventory();
            });
        }
        if (kit.getEffects() != null) {
            for (PotionEffect effect : kit.getEffects()) {
                player.addPotionEffect(effect, true);
            }
        }

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1.2f);
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1.5f);
        player.getWorld().playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 1);
        player.sendMessage("§aEquipped kit §f" + kit.getName());
    }

    public Kit getKit(String name) {
        if (name == null) return null;
        return kits.get(name.toLowerCase());
    }

    public Map<String, Kit> getKits() {
        return kits;
    }

    public boolean createKit(String name) {
        if (name == null || name.trim().isEmpty()) return false;

        String key = name.toLowerCase();

        if (kits.containsKey(key)) return false;

        kits.put(key, new Kit(name, null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                0));

        saveKits();
        return true;
    }

    public boolean deleteKit(String name) {
        if (name == null) return false;

        String key = name.toLowerCase();
        if (!kits.containsKey(key)) return false;

        kits.remove(key);
        saveKits();
        return true;
    }

    public void updateKitItems(String name, List<ItemStack> items) {
        Kit kit = getKit(name);
        if (kit == null) return;

        kit.setItems(new ArrayList<>(items));
        saveKits();
    }

    public void saveKits() {

        FileConfiguration config = plugin.getConfigManager().getKits().get();
        config.set("kits", null);

        for (Map.Entry<String, Kit> entry : kits.entrySet()) {

            Kit kit = entry.getValue();
            String path = "kits." + entry.getKey();

            config.set(path + ".permission", kit.getPermission());
            config.set(path + ".items", kit.getItems());
            config.set(path + ".armor", kit.getArmor());
            config.set(path + ".effects", kit.getEffects());
            config.set(path + ".cooldown", kit.getCooldownSeconds());
        }

        plugin.getConfigManager().getKits().save();
    }
}