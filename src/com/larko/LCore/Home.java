package com.larko.LCore;

import net.minecraft.server.v1_16_R3.DimensionManager;
import net.minecraft.server.v1_16_R3.IRegistryCustom;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Set;

public class Home implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("sethome")){
            if((args.length < 1)) return false;
            String homeName = args[0];
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            boolean home = Utils.addHome(player, homeName);
            if(!(player.getWorld() == Bukkit.getWorld("world"))) {
                player.sendMessage("You cannot set a home in the nether");
                return false;
            };

            if(Utils.getHomes(player.getUniqueId()).size() > 10) {
                player.sendMessage("You cannot have more than 10 homes");
                return false;
            }
           // System.out.println(home);
            if(home) {
                player.sendMessage("Home has been set");
                return true;
            } else {
                player.sendMessage("Home could not be set");
                return false;
            }
        } else if(label.equalsIgnoreCase("home")) {
            if((args.length < 1)) return false;
            String homeName = args[0];
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            Location home = Utils.getHome(player.getUniqueId(), homeName);
            if(home != null) {
                player.teleport(home);
                return true;
            } else {
                player.sendMessage("This home does not exist");
                return false;
            }
        } else if(label.equalsIgnoreCase("delhome")) {
            if((args.length < 1)) return false;
            String homeName = args[0];
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            boolean deletedHome = Utils.delHome(player.getUniqueId(), homeName);
            if(deletedHome){
                player.sendMessage("Deleted home");
                return true;
            } else {
                player.sendMessage("This home does not exist");
                return false;
            }
        } else if(label.equalsIgnoreCase("homes")) {
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            Set<String> homes = Utils.getHomes(player.getUniqueId());
            if(homes == null) {
                player.sendMessage("You don't have any homes");
                return false;
            } else {
                String homesString = "";
                for(String home : homes) {
                    homesString += home + " ";
                }
                player.sendMessage("Homes : " + homes.size() + "\n" + homesString);
                return true;
            }
        }
        return false;
    }
}
