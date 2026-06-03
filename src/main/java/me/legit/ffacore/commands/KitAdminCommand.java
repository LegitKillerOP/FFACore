package me.legit.ffacore.commands;

import me.legit.ffacore.FFACore;
import me.legit.ffacore.gui.KitAdminGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitAdminCommand implements CommandExecutor {

    private final FFACore plugin;

    public KitAdminCommand(FFACore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("ffacore.kit.admin") && !p.isOp()){
            p.sendMessage(plugin.getPrefix() + ChatColor.RED + "You do not have permission.");
            return true;
        }

        KitAdminGUI gui = new KitAdminGUI(plugin);

        if (args.length == 0){
            gui.openMain(p);
            return true;
        }

        if (args.length >= 1){
            String sub = args[0].toLowerCase();
            if (sub.equals("addprompt")){
                // trigger chat listener
                plugin.getChatListener().awaitName(p);
                return true;
            }
        }

        gui.openMain(p);
        return true;
    }
}
