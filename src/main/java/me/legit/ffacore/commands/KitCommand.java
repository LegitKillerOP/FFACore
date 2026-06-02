package me.legit.ffacore.commands;
import me.legit.ffacore.FFACore;
import me.legit.ffacore.kits.Kit;
import org.bukkit.ChatColor;
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
        if(!player.hasPermission(kit.getPermission())){
            player.sendMessage(plugin.getPrefix() + ChatColor.RED + "No permission.");
            return true;
        }
        player.getInventory().clear();
        for (ItemStack item : kit.getItems()) {
            player.getInventory().addItem(item);
        }
        player.sendMessage(plugin.getPrefix() + ChatColor.GREEN + "Selected " + kit.getName());

        return true;
    }
}