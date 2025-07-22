package com.philippgitpush.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PiggybackListener implements Listener {
  
  private final JavaPlugin plugin;

  public PiggybackListener(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    // Return if target is not a player
    if (event.getRightClicked().getType() != EntityType.PLAYER) return;

    // Return if player doesn't have a saddle
    if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.SADDLE) return;

    Player player = event.getPlayer();
    Player target = (Player) event.getRightClicked();

    // Reverse player and target if player is sneaking
    if (player.isSneaking()) {
      player = (Player) event.getRightClicked();
      target = event.getPlayer();
    }

    // Mount player on target
    target.addPassenger(player);
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    // Return if both aren't players
    if (!event.getDamager().getType().equals(EntityType.PLAYER)) return;
    if (!event.getEntity().getType().equals(EntityType.PLAYER)) return;

    Player damager = (Player) event.getDamager();
    Player damaged = (Player) event.getEntity();

    // Return if damaged is not mounted on damager
    if (!damager.getPassengers().contains(damaged)) return;

    // Dismount damaged / mounted
    damager.removePassenger(damaged);
    event.setCancelled(true);

    // Prepare throwing direction
    Vector direction = damager.getLocation().getDirection().normalize();
    direction.setY(0.5);
    direction.multiply(1.25);

    // Throw mounted player forward
    Bukkit.getScheduler().runTask(plugin, () -> damaged.setVelocity(direction));
  }
  
}
