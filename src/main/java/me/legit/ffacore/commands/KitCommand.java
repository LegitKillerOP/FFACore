package me.legit.ffacore.commands;
import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitCommand implements CommandExecutor {

    private final FFACore plugin;
    public KitCommand(FFACore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!(sender instanceof Player))
            return true;
        Player player = (Player) sender;
        if(args.length == 0){
            player.sendMessage(plugin.getPrefix() + "§c/kit <name>");
            return true;
        }
        Kit kit = plugin.getKitManager().getKit(args[0]);
        if(kit == null){
            player.sendMessage(plugin.getPrefix() + "§cKit not found.");
            return true;
        }

        String permission = kit.getPermission();
        if (permission != null && !permission.trim().isEmpty() && !player.hasPermission(permission)){
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "No permission.");
            return true;
        }

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        for (ItemStack item : kit.getItems()) {
            if (item == null) continue;
            if (isHelmet(item.getType())) {
                player.getInventory().setHelmet(item);
                continue;
            }
            if (isChestplate(item.getType())) {
                player.getInventory().setChestplate(item);
                continue;
            }
            if (isLeggings(item.getType())) {
                player.getInventory().setLeggings(item);
                continue;
            }
            if (isBoots(item.getType())) {
                player.getInventory().setBoots(item);
                continue;
            }
            player.getInventory().addItem(item);
        }
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Selected " + kit.getName());

        return true;
    }

    private boolean isHelmet(Material type) {
        return type == Material.LEATHER_HELMET || type == Material.CHAINMAIL_HELMET || type == Material.IRON_HELMET || type == Material.GOLD_HELMET || type == Material.DIAMOND_HELMET || type == Material.PUMPKIN;
    }

    private boolean isChestplate(Material type) {
        return type == Material.LEATHER_CHESTPLATE || type == Material.CHAINMAIL_CHESTPLATE || type == Material.IRON_CHESTPLATE || type == Material.GOLD_CHESTPLATE || type == Material.DIAMOND_CHESTPLATE;
    }

    private boolean isLeggings(Material type) {
        return type == Material.LEATHER_LEGGINGS || type == Material.CHAINMAIL_LEGGINGS || type == Material.IRON_LEGGINGS || type == Material.GOLD_LEGGINGS || type == Material.DIAMOND_LEGGINGS;
    }

    private boolean isBoots(Material type) {
        return type == Material.LEATHER_BOOTS || type == Material.CHAINMAIL_BOOTS || type == Material.IRON_BOOTS || type == Material.GOLD_BOOTS || type == Material.DIAMOND_BOOTS;
    }
}
