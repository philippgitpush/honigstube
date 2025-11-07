package com.philippgitpush.commands;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BedCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
      return true;
    }

    Player player = (Player) sender;

    Location bed = player.getRespawnLocation();

    if (bed == null) {
      player.sendMessage("Du hast kein Bett, zu dem du gehen kannst.");
      return true;
    }

    // Effect before
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1, 0);
    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 2, 0), 20, 0, 0.5, 0, 0.05);

    player.teleport(bed);
    
    // Effect afer
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1, 1);
    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 2, 0), 20, 0, 0.5, 0, 0.05);

    return true;
  }
  
}
