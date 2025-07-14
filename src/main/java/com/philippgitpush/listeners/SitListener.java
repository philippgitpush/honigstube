package com.philippgitpush.listeners;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class SitListener implements Listener {

  @EventHandler
  public void onEntityDismount(EntityDismountEvent event) {
    // Return if dismounted isn't tpye armor stand and mounted isn't type player
    if (!event.getDismounted().getType().equals(EntityType.ARMOR_STAND)) return;
    if (!event.getEntityType().equals(EntityType.PLAYER)) return;

    // Clear any leftover armor stand seats
    for (Entity entity : event.getDismounted().getWorld().getEntities()) {
      if (!(entity instanceof ArmorStand)) continue;
      if (entity.getCustomName().equals(event.getEntity().getUniqueId().toString())) entity.remove();
    }

    // Move player up to avoid falling through blocks below
    event.getEntity().teleport(event.getEntity().getLocation().add(0, 1, 0));
  }

}
