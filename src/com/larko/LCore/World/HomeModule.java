package com.larko.LCore.World;

import com.larko.LCore.Structures.Home;
import com.larko.LCore.Structures.LPlayer;
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
import java.util.ArrayList;
import java.util.Set;

import com.larko.LCore.Utils.HomeUtils;

public class HomeModule implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("sethome")){
            if((args.length < 1)) return false;
            String homeName = args[0];
            if(!(commandSender instanceof Player)) return false;
            Player player = (Player) commandSender;
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());

            if(!(player.getWorld() == Bukkit.getWorld("world"))) {
                player.sendMessage("You cannot set a home in the nether");
                return false;
            };

            if(lPlayer.getHomes().size() == 10) {
                player.sendMessage("You cannot have more than 10 homes");
                return false;
            }
            boolean home = lPlayer.addHome(homeName, player.getLocation());
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
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            Home home = lPlayer.getHome(homeName);
            if(home != null) {
                player.teleport(home.getPosition().toLocation());
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
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            boolean deletedHome = lPlayer.removeHome(homeName);
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
            LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());
            ArrayList<Home> homes = lPlayer.getHomes();
            if(homes.size() == 0) {
                player.sendMessage("You don't have any homes");
                return false;
            } else {
                String homesString = "";
                for(Home home : homes) {
                    homesString += home.getName() + " ";
                }
                player.sendMessage("Homes : " + homes.size() + "\n" + homesString);
                return true;
            }
        }
        return false;
    }
}
