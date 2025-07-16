package com.philippgitpush.listeners;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class BackpackListener implements Listener {

  private final JavaPlugin plugin;

  public BackpackListener(JavaPlugin plugin) {
      this.plugin = plugin;
  }

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    
    // Return if not sneaking
    if (!player.isSneaking()) return;

    // Return if not clicking in air / on block
    if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;

    // Return if not interacting with shulker box
    if (!Tag.SHULKER_BOXES.isTagged(player.getInventory().getItemInMainHand().getType())) return;
    event.setCancelled(true);

    // Prepare shulker box item and meta
    ItemStack shulker_item = player.getInventory().getItemInMainHand();
    BlockStateMeta shulker_meta = (BlockStateMeta) shulker_item.getItemMeta();
    ShulkerBox shulker = (ShulkerBox) shulker_meta.getBlockState();

    // Create shulker box inventory
    String shulker_name = shulker_meta.hasDisplayName() ? PlainTextComponentSerializer.plainText().serialize(shulker_meta.displayName()) : "Shulker Box";
    Inventory shulker_inventory = Bukkit.getServer().createInventory(player, InventoryType.SHULKER_BOX, Component.text(shulker_name));

    // Fill virtual inventory with shulker box contents
    shulker_inventory.setContents(shulker.getInventory().getContents());

    // Open inventory, play open sound
    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
    player.openInventory(shulker_inventory);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    // Return if not custom shulker box
    if (!event.getInventory().getType().equals(InventoryType.SHULKER_BOX)) return;
    if (event.getInventory().getHolder() == null) return;
    if (!event.getInventory().getHolder().equals(event.getPlayer())) return;

    Player player = (Player) event.getPlayer();

    // Play close sound
    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1, 1);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    // Return if there is no inventory action
    if (event.getAction().equals(InventoryAction.NOTHING)) return;

    // Return if not a player
    if (!event.getWhoClicked().getType().equals(EntityType.PLAYER)) return;

    Player player = (Player) event.getWhoClicked();

    // Return if not a shulker box inventory
    if (!hasShulkerInventoryOpen(player)) return;

    // Save contents to the shulker box
    saveShulkerBox(player);

    // Stop interaction with shulker box slot
    if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      if (player.getInventory().getHeldItemSlot() == event.getHotbarButton()) event.setCancelled(true);
      if (player.getInventory().getHeldItemSlot() == event.getSlot()) event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    // Return if not a player
    if (!event.getWhoClicked().getType().equals(EntityType.PLAYER)) return;

    Player player = (Player) event.getWhoClicked();

    // Return if not a shulker box inventory
    if (!hasShulkerInventoryOpen(player)) return;

    // Save contents to the shulker box
    saveShulkerBox(player);
  }

  private boolean hasShulkerInventoryOpen(Player player) {
    // Return if there is no open inventory
    if (player.getOpenInventory() == null) return false;

    // Return if there is no no open top inventory
    if (player.getOpenInventory().getTopInventory() == null) return false;

    // Return if open inventory isn't a shulker box
    if (!player.getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) return false;

    // Return if inventory holder isn't from the player
    if (!player.getOpenInventory().getTopInventory().getHolder().equals(player)) return false;

    return true;
  }

  private void saveShulkerBox(Player player) {
    Bukkit.getScheduler().runTask(plugin, () -> {
      ItemStack[] items = player.getOpenInventory().getTopInventory().getContents();
      ItemStack shulker = player.getInventory().getItemInMainHand();

      // Return if shulkerbox is missing
      if (shulker == null || !Tag.SHULKER_BOXES.isTagged(shulker.getType())) return;

      BlockStateMeta shulker_meta = (BlockStateMeta) shulker.getItemMeta();
      ShulkerBox shulker_box = (ShulkerBox) shulker_meta.getBlockState();

      // Skip if inventory contents haven't changed
      if (Arrays.equals(shulker_box.getInventory().getContents(), items)) return;

      // Save contents to shulker
      shulker_box.getInventory().setContents(items);
      shulker_meta.setBlockState(shulker_box);
      shulker.setItemMeta(shulker_meta);
    });
  }
}
