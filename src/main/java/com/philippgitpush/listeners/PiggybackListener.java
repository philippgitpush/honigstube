package com.philippgitpush.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PiggybackListener implements Listener {
  
  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    // Return if target is not a player
    if (event.getRightClicked().getType() != EntityType.PLAYER) return;

    // Return if player doesn't have a saddle
    if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.SADDLE) return;

    Player player = event.getPlayer();
    Player target = (Player) event.getRightClicked();

    // Mount player on target
    target.addPassenger(player);
  }

}
