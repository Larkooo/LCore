package com.larko.LCore.World;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerModule implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (label.equalsIgnoreCase("safechest")) {
            // TODO : Check if player fighting
            player.openInventory(player.getEnderChest());
            return true;
        }
        return false;
    }
}
