package com.philippgitpush.commands;

import org.bukkit.Tag;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public class SitCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
      return true;
    }

    Player player = (Player) sender;
    
    // Return if player is already seated
    if (player.getVehicle() != null) {
      player.sendMessage("Du sitzt schon auf etwas.");
      return true;
    }

    // Return if there is no block to sit on
    if (Tag.AIR.isTagged(player.getLocation().add(0, -1, 0).getBlock().getType())) {
      player.sendMessage("Hier gibt es keinen Platz zum Sitzen.");
      return true;
    }

    // Clear any leftover armor stand seats
    for (Entity entity : player.getWorld().getEntities()) {
      if (!(entity instanceof ArmorStand)) continue;
      if (entity.customName() == null) continue;
      if (entity.customName().toString().equals(player.getUniqueId().toString())) entity.remove();
    }

    // Create seat
    ArmorStand seat = (ArmorStand) player.getWorld().spawnEntity(player.getLocation().add(0, -2, 0), EntityType.ARMOR_STAND);
    seat.setVisible(false);
    seat.customName(Component.text(player.getUniqueId().toString()));
    seat.setCustomNameVisible(false);
    seat.setInvulnerable(true);
    seat.setGravity(false);
    seat.setCollidable(false);

    // Mount player
    seat.addPassenger(player);

    return true;
  }
  
}
