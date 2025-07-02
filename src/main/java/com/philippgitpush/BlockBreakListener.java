package com.philippgitpush;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;

public class BlockBreakListener implements Listener {

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
      checkForTimber(event);
  }

  public void checkForTimber(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block startBlock = event.getBlock();

    // Require sneaking
    if (!player.isSneaking()) return;

    // Require player holding an axe
    ItemStack tool = player.getInventory().getItemInMainHand();
    if (tool == null || !Tag.ITEMS_AXES.isTagged(tool.getType())) return;

    // Require the block to be a LOG
    if (!Tag.LOGS.isTagged(startBlock.getType())) return;

    Set<Material> allowedSoilBlocks = Set.of(
        Material.GRASS_BLOCK,
        Material.DIRT,
        Material.COARSE_DIRT,
        Material.PODZOL,
        Material.FARMLAND,
        Material.MYCELIUM
    );

    // Return if block below isn't from known grass type
    Block below = startBlock.getLocation().add(0, -1, 0).getBlock();
    if (!allowedSoilBlocks.contains(below.getType())) return;

    Set<Block> visited = new HashSet<>();
    Queue<Block> toVisit = new LinkedList<>();

    List<Block> targetLogs = new ArrayList<>();
    List<Block> targetLeaves = new ArrayList<>();

    final int maxLeaves = 50;
    int leafCount = 0;

    toVisit.add(startBlock);
    visited.add(startBlock);

    List<int[]> directions = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          if (x == 0 && y == 0 && z == 0) continue; // skip the block itself
          directions.add(new int[]{x, y, z});
        }
      }
    }

    while (!toVisit.isEmpty()) {
        Block current = toVisit.poll();

        if (Tag.LOGS.isTagged(current.getType())) {
            targetLogs.add(current);
        } else if (Tag.LEAVES.isTagged(current.getType())) {
            if (leafCount >= maxLeaves) continue;
            targetLeaves.add(current);
            leafCount++;
        } else {
            continue;
        }

        for (int[] dir : directions) {
            Block neighbor = current.getLocation().add(dir[0], dir[1], dir[2]).getBlock();
            if (!visited.contains(neighbor) &&
                (Tag.LOGS.isTagged(neighbor.getType()) || Tag.LEAVES.isTagged(neighbor.getType()))) {
                visited.add(neighbor);
                toVisit.add(neighbor);
            }
        }
    }

    // Return if no leaves are found
    if (targetLeaves.size() == 0) return;

    for (Block log : targetLogs) log.breakNaturally();
    for (Block leaf : targetLeaves) leaf.breakNaturally();

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
