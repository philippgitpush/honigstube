package com.philippgitpush.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(ChatColor.RED + "Nur Spieler können diesen Befehl verwenden.");
      return true;
    }

    Player player = (Player) sender;

    Location bed = player.getRespawnLocation();

    if (bed == null) {
      player.sendMessage(ChatColor.RED + "Du hast kein Bett, zu dem du gehen kannst.");
      return true;
    }

    player.teleport(bed);
    return true;
  }
}
