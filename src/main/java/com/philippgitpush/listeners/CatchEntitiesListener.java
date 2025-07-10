package com.philippgitpush.listeners;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Chicken.Variant;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class CatchEntitiesListener implements Listener {
  
  private static final Random RANDOM = new Random();

  @EventHandler
  public void onProjectileHit(ProjectileHitEvent event) {
    // Return if not an egg
    if (event.getEntity().getType() != EntityType.EGG) return;

    // Return if there is no hit entity, allow chicken hatching
    if (event.getHitEntity() == null) {
      Egg egg = (Egg) event.getEntity();
      handleEggHatch(event.getEntity().getLocation().getBlock().getLocation().add(0.5, 0.5, 0.5), egg.getItem().getType());
      
      return;
    }

    // Return if no matching spawn egg
    ItemStack egg = getSpawnEgg(event.getHitEntity().getType());
    if (egg.getType() == Material.EGG) return;

    // Return if hit entity is ENDER_DRAGON
    if (event.getHitEntity().getType() == EntityType.ENDER_DRAGON);

    // Drop the egg
    event.getHitEntity().getWorld().dropItemNaturally(event.getHitEntity().getLocation().add(0, 0.5, 0), egg);

    // Remove the entity, cleanup
    event.getHitEntity().remove();
    event.getEntity().remove();

    // Effects and sound
    event.getHitEntity().getWorld().spawnParticle(Particle.CLOUD, event.getHitEntity().getLocation(), 20, 1, 1, 1, 0.1);
    event.getHitEntity().getWorld().playSound(event.getHitEntity().getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0);
  }

  @EventHandler
  public void onPlayerEggThrow(PlayerEggThrowEvent event) {
    event.setHatching(false);
  }

  private ItemStack getSpawnEgg(EntityType type) {
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
  
  private void handleEggHatch(Location location, Material material) {
    if (RANDOM.nextInt(8) == 0) {
      spawnChicken(location, material);

      if (RANDOM.nextInt(32) == 0) for (int i = 0; i < 3; i++) spawnChicken(location, material);
    }
  }

  private void spawnChicken(Location location, Material material) {
    World world = location.getWorld();
    if (world == null) return;

    world.spawn(location, Chicken.class, chicken -> {
      chicken.setBaby();

      switch (material) {
        case BLUE_EGG -> chicken.setVariant(Variant.COLD);
        case BROWN_EGG -> chicken.setVariant(Variant.WARM);
        default -> {}
      }
    });
  }

}
