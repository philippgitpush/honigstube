package com.philippgitpush.listeners;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class ProjectileHitListener implements Listener {

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {

    // Return if not an egg
    if (event.getEntity().getType() != EntityType.EGG) return;

    // Return if there is no hit entity
    if (event.getHitEntity() == null) return;

    // Return if no matching spawn egg
    ItemStack egg = getSpawnEgg(event.getHitEntity().getType());
    if (egg.getType() == Material.EGG) return;

    // Drop the egg
    event.getHitEntity().getWorld().dropItemNaturally(event.getHitEntity().getLocation(), egg);

    // Remove the entity, cleanup
    event.getHitEntity().remove();
    event.getEntity().remove();

    // Effects and sound
    event.getHitEntity().getWorld().spawnParticle(Particle.CLOUD, event.getHitEntity().getLocation(), 20, 1, 1, 1, 0.1);
    event.getHitEntity().getWorld().playSound(event.getHitEntity().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0);
  }

  public static ItemStack getSpawnEgg(EntityType type) {
    if (type == null) return new ItemStack(Material.EGG);

    Material eggMaterial;

    try {
      eggMaterial = Material.valueOf(type.name() + "_SPAWN_EGG");
    } catch (IllegalArgumentException e) {
      return new ItemStack(Material.EGG);
    }

    ItemStack egg = new ItemStack(eggMaterial);
    if (egg.getItemMeta() instanceof SpawnEggMeta meta) egg.setItemMeta(meta);

    return egg;
  }
  
}
