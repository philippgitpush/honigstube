// TODO: Bugfix shulker saving empty inventory if interacting too fast. (Trying to save an inventory that doesn't exist anymore?)

package com.philippgitpush.listeners;

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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.philippgitpush.holders.HonigstubeHolder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class BackpackListener implements Listener {

  private InventoryHolder BackpackHolder = new HonigstubeHolder();

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
    Inventory shulker_inventory = Bukkit.getServer().createInventory(BackpackHolder, InventoryType.SHULKER_BOX, Component.text(shulker_name));

    // Fill virtual inventory with shulker box contents
    shulker_inventory.setContents(shulker.getInventory().getContents());

    // Open inventory, play open sound
    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_OPEN, 1, 1);
    player.openInventory(shulker_inventory);
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

    // Stop interaction with shulker box slot
    if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
      if (player.getInventory().getHeldItemSlot() == event.getHotbarButton()) event.setCancelled(true);
      if (player.getInventory().getHeldItemSlot() == event.getSlot()) event.setCancelled(true);
    }
  }

  private boolean hasShulkerInventoryOpen(Player player) {
    // Return if there is no open inventory
    if (player.getOpenInventory() == null) return false;

    // Return if there is no no open top inventory
    if (player.getOpenInventory().getTopInventory() == null) return false;

    // Return if open inventory isn't a shulker box
    if (!player.getOpenInventory().getTopInventory().getType().equals(InventoryType.SHULKER_BOX)) return false;

    // Return if inventory holder isn't from the player
    if (!player.getOpenInventory().getTopInventory().getHolder().equals(BackpackHolder)) return false;

    return true;
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();

    // Return if plugin isn't holder or if not shulker
    if (!(inventory.getHolder().equals(BackpackHolder))) return;
    if (inventory.getType() != InventoryType.SHULKER_BOX) return;

    // Trigger shulker save
    Player player = (Player) event.getPlayer();
    saveShulkerBox(player, inventory);

    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SHULKER_BOX_CLOSE, 1, 1);
  }

  private void saveShulkerBox(Player player, Inventory inventory) {
    ItemStack[] items = inventory.getContents().clone();

    // Return if shulker doesn't exist
    ItemStack shulker = player.getInventory().getItemInMainHand();
    if (shulker == null || !(shulker.getItemMeta() instanceof BlockStateMeta shulker_meta)) return;

    // Save shulkerbox
    ShulkerBox shulker_box = (ShulkerBox) shulker_meta.getBlockState();
    shulker_box.getInventory().setContents(items);
    shulker_meta.setBlockState(shulker_box);
    shulker.setItemMeta(shulker_meta);
  }
  
}
