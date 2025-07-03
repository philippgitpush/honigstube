package com.philippgitpush.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockBreakListener implements Listener {

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    checkForTimber(event);
  }

  private boolean isLog(Block block) {
    Set<Material> CUSTOM_LOGS = Set.of(
        Material.MANGROVE_ROOTS
    );

    return Tag.LOGS.isTagged(block.getType()) || CUSTOM_LOGS.contains(block.getType());
  }

  private boolean isLeaf(Block block) {
    Set<Material> CUSTOM_LEAVES = Set.of(
        Material.NETHER_WART_BLOCK,
        Material.WARPED_WART_BLOCK,
        Material.SHROOMLIGHT,
        Material.MOSS_CARPET,
        Material.VINE
    );
    
    return Tag.LEAVES.isTagged(block.getType()) || CUSTOM_LEAVES.contains(block.getType());
  }

  public void checkForTimber(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block startBlock = event.getBlock();

    // Return if player is not sneaking
    if (!player.isSneaking()) return;

    // Return if player isn't holding an axe
    ItemStack tool = player.getInventory().getItemInMainHand();
    if (tool == null || !Tag.ITEMS_AXES.isTagged(tool.getType())) return;

    // Return if not a log
    if (!isLog(startBlock)) return;

    Set<Material> allowedSoilBlocks = Set.of(
      Material.GRASS_BLOCK,
      Material.DIRT,
      Material.COARSE_DIRT,
      Material.ROOTED_DIRT,
      Material.PODZOL,
      Material.FARMLAND,
      Material.MYCELIUM,
      Material.NETHERRACK,
      Material.CRIMSON_NYLIUM,
      Material.WARPED_NYLIUM,
      Material.MUDDY_MANGROVE_ROOTS,
      Material.MUD
    );

    // Return if soil block isn't listed
    Block below = startBlock.getLocation().add(0, -1, 0).getBlock();
    if (!allowedSoilBlocks.contains(below.getType())) return;

    Set<Block> visited = new HashSet<>();
    Queue<Block> toVisit = new LinkedList<>();

    List<Block> targetLogs = new ArrayList<>();
    List<Block> targetLeaves = new ArrayList<>();

    final int maxLeaves = 150;
    int leafCount = 0;

    // Direction map
    List<int[]> directions = Arrays.asList(
      // Current layer
      new int[] { 1, 0, 0 },
      new int[] { 1, 0, 1 },
      new int[] { 0, 0, 1 },
      new int[] { -1, 0, 1 },
      new int[] { -1, 0, 0 },
      new int[] { -1, 0, -1 },
      new int[] { 0, 0, -1 },
      new int[] { 1, 0, -1 },
      // Layer above
      new int[] { 1, 1, 0 },
      new int[] { 1, 1, 1 },
      new int[] { 0, 1, 1 },
      new int[] { -1, 1, 1 },
      new int[] { -1, 1, 0 },
      new int[] { -1, 1, -1 },
      new int[] { 0, 1, -1 },
      new int[] { 1, 1, -1 },
      new int[] { 0, 1, 0 } // Middle
    );

    // Phase 1: Logs
    toVisit.add(startBlock);
    visited.add(startBlock);

    while (!toVisit.isEmpty()) {
      Block current = toVisit.poll();

      if (!isLog(current)) continue;
      targetLogs.add(current);

      for (int[] dir : directions) {
        Location neighborLoc = current.getLocation().clone().add(dir[0], dir[1], dir[2]);
        Block neighbor = neighborLoc.getBlock();

        if (visited.contains(neighbor)) continue;
        if (!isLog(neighbor)) continue;

        visited.add(neighbor);
        toVisit.add(neighbor);
      }
    }

    if (targetLogs.isEmpty()) return;

    // Phase 2: Leaves around logs
    Queue<Block> leavesToExpand = new LinkedList<>();
    for (Block log : targetLogs) {
      for (int[] dir : directions) {
        Location leafLoc = log.getLocation().clone().add(dir[0], dir[1], dir[2]);
        Block neighbor = leafLoc.getBlock();

        if (visited.contains(neighbor)) continue;

        if (isLeaf(neighbor)) {
          if (leafCount >= maxLeaves) continue;
          targetLeaves.add(neighbor);
          leavesToExpand.add(neighbor);
          leafCount++;
          visited.add(neighbor);
        }
      }
    }

    // Phase 3: One expansion from leaves
    while (!leavesToExpand.isEmpty()) {
      Block leaf = leavesToExpand.poll();

      for (int[] dir : directions) {
        Location neighborLoc = leaf.getLocation().clone().add(dir[0], dir[1], dir[2]);
        Block neighbor = neighborLoc.getBlock();

        if (visited.contains(neighbor)) continue;

        if (isLeaf(neighbor)) {
          if (leafCount >= maxLeaves) continue;
          targetLeaves.add(neighbor);
          leafCount++;
          visited.add(neighbor);
        }
      }
    }

    // Return if its just logs
    if (targetLeaves.isEmpty()) return;

    for (Block log : targetLogs) {
      log.getWorld().spawnParticle(Particle.CLOUD, log.getLocation(), 2, 1, 1, 1, 0.1);
      log.breakNaturally();
    } 
    for (Block leaf : targetLeaves) {
      leaf.getWorld().spawnParticle(Particle.CLOUD, leaf.getLocation(), 2, 1, 1, 1, 0.1);
      leaf.breakNaturally();
    }

    startBlock.getWorld().playSound(startBlock.getLocation(), Sound.BLOCK_WOOD_BREAK, 1, new Random().nextFloat());
    startBlock.getWorld().playSound(startBlock.getLocation(), Sound.BLOCK_VINE_BREAK, 1, new Random().nextFloat());

    damageHeldItem(player, targetLogs.size());
  }

  public void damageHeldItem(Player player, int damageAmount) {
    ItemStack item = player.getInventory().getItemInMainHand();

    // Return if player isn't holding an item
    if (item == null || item.getType().getMaxDurability() <= 0) return;

    // Return if held item isn't damageable
    ItemMeta meta = item.getItemMeta();
    if (!(meta instanceof Damageable damageable)) return;

    // Check for unbreaking enchantment
    int unbreakingLevel = item.getEnchantmentLevel(Enchantment.UNBREAKING);
    Random random = new Random();

    // Chance that the damage is ignored by unbreaking
    int damageToApply = 0;
    for (int i = 0; i < damageAmount; i++) if (random.nextInt(unbreakingLevel + 1) == 0) damageToApply++;

    // Apply damage, set item meta
    int currentDamage = damageable.getDamage();
    damageable.setDamage(currentDamage + damageToApply);
    item.setItemMeta(meta);
  }

}
