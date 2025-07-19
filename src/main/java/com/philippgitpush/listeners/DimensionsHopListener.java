package com.philippgitpush.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DimensionsHopListener implements Listener {

  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    if (player.getLocation().getY() >= -32 && player.getLocation().getY() <= 352) return;

    // Determind environment type
    Environment environment = player.getWorld().getEnvironment();
    Boolean is_overworld = environment.equals(Environment.NORMAL) ? true : false;
    Double target_y = (is_overworld) ? 0.0 : 320.0;

    World destination = Bukkit.getWorld(is_overworld ? "world_the_end" : "world");

    // Add effect
    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 120, 1));

    // Get current pitch and yaw
    Location location = player.getLocation();
    float pitch = location.getPitch();
    float yaw = location.getYaw();

    // Play leave sound / effect for nearby players
    player.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRAVEL, 1, 0.5F);
    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(0, 1, 0), 20, 0, 0.5, 0, 0.05);

    // Teleport player, preserving pitch & yaw
    Location targetLoc = new Location(destination, location.getX(), target_y, location.getZ(), yaw, pitch);
    player.teleport(targetLoc);

    // Play appear sound / effect for nearby players
    player.getWorld().playSound(location, Sound.BLOCK_PORTAL_TRAVEL, 1, 1.5F);
    player.getWorld().spawnParticle(Particle.DRAGON_BREATH, player.getLocation().add(0, 1, 0), 20, 0, 0.5, 0, 0.05);
  }

}
