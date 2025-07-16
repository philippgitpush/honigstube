package com.philippgitpush.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Nur Spieler können diesen Befehl verwenden");
      return true;
    }

    Player player = (Player) sender;

    LivingEntity waypoint = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
    waypoint.getAttribute(Attribute.WAYPOINT_TRANSMIT_RANGE).setBaseValue(100.0);

    return true;
  }
  
}
