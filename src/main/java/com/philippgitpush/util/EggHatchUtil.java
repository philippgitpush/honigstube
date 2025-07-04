package com.philippgitpush.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Chicken.Variant;

import java.util.Random;

public class EggHatchUtil {

  private static final Random RANDOM = new Random();

  /**
   * Handles spawning chickens with Minecraft's vanilla probability.
   *
   * @param location The location where chicks should spawn.
   * @param material Material to determind variant.
   */
  public static void handleEggHatch(Location location, Material material) {
    // 1 in 8 chance (12.5%) to spawn chicks
    if (RANDOM.nextInt(8) == 0) {
      // Spawn 1 chick by default
      spawnChick(location, material);

      // 1 in 32 chance to spawn 3 extra chicks (total 4 chicks)
      if (RANDOM.nextInt(32) == 0) {
        for (int i = 0; i < 3; i++) {
          spawnChick(location, material);
        }
      }
    }
  }

  /**
   * Spawns a single chick at the given location.
   *
   * @param location Where to spawn.
   * @param material Material to determind variant.
   */
  private static void spawnChick(Location location, Material material) {
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
