package com.philippgitpush.listeners;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomCreaturesListener implements Listener {
  
  private static final Random RANDOM = new Random();

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent event) {
    if (event.getEntity().getType() == EntityType.CREEPER) spawnChickenCreeper(event.getEntity());
  }

  @EventHandler
  public void onCreatureDeath(EntityDeathEvent event) {
    if (event.getEntityType().equals(EntityType.CHICKEN) && event.getEntity().getVehicle() != null) removeChickenCreeperEffect(event);
  }

  private void removeChickenCreeperEffect(EntityDeathEvent event) {
    if (!event.getEntity().getVehicle().getType().equals(EntityType.CREEPER)) return;

    LivingEntity creeper = (LivingEntity) event.getEntity().getVehicle();
    creeper.removePotionEffect(PotionEffectType.SLOW_FALLING);
  }

  private void spawnChickenCreeper(Entity creeper) {
    if (RANDOM.nextInt(8) != 0) return;
      
    LivingEntity chicken = (LivingEntity) creeper.getWorld().spawnEntity(creeper.getLocation(), EntityType.CHICKEN);
    chicken.setRemoveWhenFarAway(true);
    creeper.addPassenger(chicken);
    
    PotionEffect effect = new PotionEffect(PotionEffectType.SLOW_FALLING, Integer.MAX_VALUE, 2);
    effect.apply((LivingEntity) creeper);
  }

}
