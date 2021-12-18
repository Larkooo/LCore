package com.larko.LCore.Economy;

import com.larko.LCore.Structures.LPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.LinkedList;

public class MoneyModule implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        LPlayer lPlayer = LPlayer.findByUUID(player.getUniqueId());

        if (label.equalsIgnoreCase("lcoins")) {
            player.sendMessage(ChatColor.GOLD + "You have " + lPlayer.getLCoins() + " LCoins");
            return true;
        } else if (label.equalsIgnoreCase("transfer")) {
            if (args.length != 2) {
                player.sendMessage("This command takes 2 arguments, player name and amount of LCoins");
            }
            String playerName = args[0];
            double lCoins = Double.parseDouble(args[1]);
            if (lCoins < 0) {
                player.sendMessage(ChatColor.GRAY + "You cannot give a negative value of LCoins");
                return false;
            }
            if (lCoins > lPlayer.getLCoins()) {
                player.sendMessage(ChatColor.GRAY + "You cannot give more LCoins than you have");
                return false;
            }
            Player selectedPlayer = Bukkit.getPlayer(args[0]);
            if (selectedPlayer == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return false;
            }
            if (selectedPlayer == player) {
                player.sendMessage( ChatColor.BLACK + "You cannot give LCoins to yourself");
                return false;
            }
            LPlayer selectedLPlayer = LPlayer.findByUUID(selectedPlayer.getUniqueId());

            if (lPlayer.setLCoins(lPlayer.getLCoins() - lCoins)) {
                if (!selectedLPlayer.setLCoins(selectedLPlayer.getLCoins() + lCoins)) {
                    player.sendMessage(ChatColor.RED + "An error occurred, refunding you...");
                    lPlayer.setLCoins(lPlayer.getLCoins() + lCoins);
                    return false;
                }
                player.sendMessage("You gave " + ChatColor.GOLD + lCoins + ChatColor.WHITE + " LCoins to " +  ChatColor.BLUE + selectedPlayer.getName());
                selectedPlayer.sendMessage(ChatColor.BLUE + player.getName() + " gave you " + ChatColor.GOLD + lCoins);
            }
        }

        else if (label.equalsIgnoreCase("setlcoins")) {
            if (!player.isOp()) {
                player.sendMessage("You need to be OP to use this command");
                return false;
            }
            if (args.length != 2) {
                player.sendMessage("This command takes 2 arguments, player name and amount of LCoins");
            }
            String playerName = args[0];
            float lCoins = Float.parseFloat(args[1]);
            Player selectedPlayer = Bukkit.getPlayer(args[0]);
            if (selectedPlayer == null) {
                player.sendMessage(ChatColor.RED + "Player not found");
                return false;
            }

            if (!LPlayer.findByUUID(selectedPlayer.getUniqueId()).setLCoins(lCoins)) {
                player.sendMessage(ChatColor.RED + "An error occurred, could not set desired amount of LCoins to " + ChatColor.BLUE + selectedPlayer.getName());
                return false;
            };
            player.sendMessage(ChatColor.BLUE + selectedPlayer.getName() + ChatColor.WHITE + " balance has been set to " + ChatColor.GOLD + lCoins + ChatColor.WHITE + " LCoins");
            if (selectedPlayer != player)
                selectedPlayer.sendMessage("Your balance has been set to " + ChatColor.GOLD + lCoins + ChatColor.WHITE + " LCoins");
            return true;
        }
        return false;
    }
}