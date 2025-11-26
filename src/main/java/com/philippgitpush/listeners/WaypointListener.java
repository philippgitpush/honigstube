package com.philippgitpush.listeners;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import com.philippgitpush.Main;
import com.philippgitpush.dialogs.TextInputDialog;
import com.philippgitpush.dialogs.WaypointSettingsDialog;
import com.philippgitpush.holders.HonigstubeHolder;
import com.philippgitpush.utils.WaypointManager;
import com.philippgitpush.utils.WaypointManager.Waypoint;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class WaypointListener implements Listener {

  private final Main plugin;

  private InventoryHolder WaypointsHolder = new HonigstubeHolder();

  public WaypointListener(Main plugin) {
    this.plugin = plugin;
    startWaypointParticlesTask();
  }

  /* Inventory and Interaction */

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();

    // Return if player is outside of overworld
    if (!player.getWorld().getEnvironment().equals(Environment.NORMAL)) return;

    // Return if nothing interacting with no item
    if (player.getInventory().getItemInMainHand() == null) return;

    // Return if not interacting with compass
    if (event.getMaterial() != Material.COMPASS) return;

    // Skip left click
    if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

    // Open Waypoint creation if interacting with bed
    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && Tag.BEDS.isTagged(event.getClickedBlock().getType())) {
      event.setCancelled(true);
      checkForWaypoint(player, event.getClickedBlock().getLocation());
    } else {
      event.setCancelled(true);
      openWaypointsInventory(player);
    }
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!event.getInventory().getHolder().equals(WaypointsHolder) || event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) return;
    event.setCancelled(true);

    attemptWaypointTeleport((Player) event.getWhoClicked(), event.getCurrentItem());
  }

  @EventHandler
  public void onInventoryDrag(InventoryDragEvent event) {
    if (event.getInventory().getHolder().equals(WaypointsHolder)) {
      event.setCancelled(true);
    }
  }

  /* Waypoint break detection */

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    if (Tag.BEDS.isTagged(event.getBlock().getType())) triggerWaypointPurge();
  }

  @EventHandler
  public void onBlockBreakBlock(BlockBreakBlockEvent event) {
    if (Tag.BEDS.isTagged(event.getBlock().getType())) triggerWaypointPurge();
  }

  @EventHandler
  public void onEntityExplode(EntityExplodeEvent event) {
    for (var block : event.blockList()) {
      if (Tag.BEDS.isTagged(block.getType())) {
        triggerWaypointPurge();
        break;
      }
    }
  }

  /* Waypoint creation & teleportation */

  private void checkForWaypoint(Player player, Location location) {
    WaypointManager waypoints = plugin.getWaypointManager();

    // Override location for correct bed part
    org.bukkit.block.data.type.Bed bed_data = (org.bukkit.block.data.type.Bed) location.getBlock().getBlockData();
    BlockFace facing = bed_data.getFacing();
    if (bed_data.getPart() == Part.HEAD) location = location.getBlock().getRelative(facing.getOppositeFace()).getLocation();

    if (waypoints.getWaypoint(location) != null) {
      openWaypointSettings(player, location);
    } else {
      openWaypointCreation(player, location);
    }
  }

  private void attemptWaypointTeleport(Player player, ItemStack item) {
    ItemMeta meta = item.getItemMeta();

    Double x = meta.getPersistentDataContainer().get(
      new NamespacedKey(plugin, "waypoint_x"),
      PersistentDataType.DOUBLE
    );

    Double y = meta.getPersistentDataContainer().get(
      new NamespacedKey(plugin, "waypoint_y"),
      PersistentDataType.DOUBLE
    );

    Double z = meta.getPersistentDataContainer().get(
      new NamespacedKey(plugin, "waypoint_z"),
      PersistentDataType.DOUBLE
    );

    String world = meta.getPersistentDataContainer().get(
      new NamespacedKey(plugin, "waypoint_world"),
      PersistentDataType.STRING
    );

    int cost = meta.getPersistentDataContainer().get(
      new NamespacedKey(plugin, "waypoint_cost"),
      PersistentDataType.INTEGER
    );

    // Check level cost
    if (player.getLevel() < cost) {
      player.sendActionBar(Component.text("Du hast nicht genug Level für diesen Wegpunkt", NamedTextColor.RED));
      player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
      player.closeInventory();
      return;
    }

    // Apply level cost
    if (cost > 0) player.setLevel(player.getLevel() - cost);

    // Destination location
    Location destination = new Location(Bukkit.getWorld(world), x, y, z);
    destination.setPitch(player.getPitch());
    destination.setYaw(player.getYaw());

    // Play sound at origin location
    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1, 1);
    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 20, 0, 0.5, 0, 0.05);

    // Teleport and close inventory
    player.teleport(findSafeLocation(destination, 1));
    player.closeInventory();

    // Play sound at destination location
    destination.getWorld().playSound(destination, Sound.ENTITY_PLAYER_TELEPORT, 1, 1);
    player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 1, 0), 20, 0, 0.5, 0, 0.05);
  }

  private void openWaypointCreation(Player player, Location location) {
    TextInputDialog creation_dialog = new TextInputDialog(
        "Wegpunkt erstellen",
        "Gib deinem Wegpunkt einen Namen",
        "Wegpunkt erstellen",
        "Klicke hier, um den Wegpunkt zu erstellen.",
        "Abbrechen",
        "Klicke hier, um das erstellen abzubrechen.");

    creation_dialog.open(player, input -> {
      if (input == "" || input == null)
        input = player.getName() + "'s Bett";

      WaypointManager waypoints = plugin.getWaypointManager();
      waypoints.createWaypoint(input, player.getUniqueId().toString(), location.getBlock().getType(), location);
      player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
    });
  }

  private void openWaypointSettings(Player player, Location location) {
    WaypointManager waypoints = plugin.getWaypointManager();
    Waypoint waypoint = waypoints.getWaypoint(location);

    WaypointSettingsDialog settings_dialog = new WaypointSettingsDialog(waypoint.getName());

    settings_dialog.open(
      player,
      input -> { // Rename
        TextInputDialog rename_dialog = new TextInputDialog(waypoint.getName(),
          "Gib deinem Wegpunkt einen neuen Namen",
          "Wegpunkt aktualisieren",
          "Klicke hier, um die Änderungen zu speichern.",
          "Abbrechen",
          "Klicke hier, um das ändern abzubrechen.");

        rename_dialog.open(player, new_name -> {
          if (new_name == "" || new_name == null)
          new_name = player.getName() + "'s Bett";
          waypoints.renameWaypoint(waypoint.getId(), new_name);
          openWaypointSettings(player, location);
          player.getWorld().playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1, 1);
        });
      },
      () -> { // Deletion
        waypoints.deleteWaypoint(waypoint.getId());
        player.getWorld().playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1, 1);
      }
    );
  }

  private void openWaypointsInventory(Player player) {
    Inventory quick_travel_inventory = Bukkit.createInventory(WaypointsHolder, 54, Component.text("Schnellreise"));

    WaypointManager waypointManager = plugin.getWaypointManager();
    Map<String, WaypointManager.Waypoint> waypoints = waypointManager.getAllWaypoints();

    for (WaypointManager.Waypoint waypoint : waypoints.values()) {
      if (!waypoint.getLocation().getWorld().equals(player.getLocation().getWorld())) continue;

      ItemStack item = new ItemStack(waypoint.getMaterial());
      ItemMeta meta = item.getItemMeta();

      // Waypoint-Data
      String author = Bukkit.getServer().getOfflinePlayer(UUID.fromString(waypoint.getCreator())).getName();
      long distance = Math.round(waypoint.getLocation().distance(player.getLocation()));
      int cost = Math.round(distance / 200);

      quick_travel_inventory.addItem(createWaypointItem(waypoint, author, distance, cost, item, meta));
    }

    player.openInventory(quick_travel_inventory);
  }

  private void triggerWaypointPurge() {
    new BukkitRunnable() {
      @Override
      public void run() {
        plugin.getWaypointManager().purgeWaypoints();
      }
    }.runTaskLater(plugin, 1L);
  }

  private void startWaypointParticlesTask() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          WaypointManager waypointManager = plugin.getWaypointManager();
          Map<String, WaypointManager.Waypoint> waypoints = waypointManager.getAllWaypoints();

          Location location = player.getLocation();

          for (WaypointManager.Waypoint hint : waypoints.values()) {
            if (!hint.getLocation().getWorld().equals(location.getWorld())) continue;
            if (hint.getLocation().distance(location) <= 100) {
              org.bukkit.block.data.type.Bed bed_data = (org.bukkit.block.data.type.Bed) hint.getLocation().getBlock().getBlockData();
              BlockFace facing = bed_data.getFacing();

              if (bed_data.getPart() == Part.HEAD) {
                Location hint_facing = hint.getLocation().getBlock().getRelative(facing.getOppositeFace()).getLocation();
                hint_facing.getWorld().spawnParticle(Particle.GLOW, hint_facing.add(0.5, 1, 0.5), 5, 0.25, 0.25, 0.25, 0);
              } else {
                Location hint_facing = hint.getLocation().getBlock().getRelative(facing).getLocation();
                hint_facing.getWorld().spawnParticle(Particle.GLOW, hint_facing.add(0.5, 1, 0.5), 5, 0.25, 0.25, 0.25, 0);
              }

              hint.getLocation().getWorld().spawnParticle(Particle.GLOW, hint.getLocation().add(0.5, 1, 0.5), 5, 0.25, 0.25, 0.25, 0);
            }
          }
        }
      }
    }.runTaskTimer(plugin, 0L, 5L);
  }

  /* Helpers */

  public ItemStack createWaypointItem(Waypoint waypoint, String author, long distance, int cost, ItemStack item, ItemMeta meta) {
    PersistentDataContainer pdc = meta.getPersistentDataContainer();

    // Name
    meta.displayName(Component.text(waypoint.getName(), NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));

    // Lore
    List<Component> lore = List.of(
      Component.text("Wegpunkt von ", NamedTextColor.GRAY)
        .append(Component.text(author, NamedTextColor.WHITE)).decoration(TextDecoration.ITALIC, false),
      Component.text("Noch ", NamedTextColor.GRAY)
        .append(Component.text(distance + " Blöcke", NamedTextColor.WHITE))
        .append(Component.text(" bis zum Ziel", NamedTextColor.GRAY)).decoration(TextDecoration.ITALIC, false),
      Component.text("Die Magie der Schnellreise verlangt ", NamedTextColor.GRAY)
        .append(Component.text(cost + " Level", NamedTextColor.BLUE)).decoration(TextDecoration.ITALIC, false)
    );

    meta.lore(lore);

    // Metadata
    pdc.set(new NamespacedKey(plugin, "waypoint_x"), PersistentDataType.DOUBLE, waypoint.getLocation().getX());
    pdc.set(new NamespacedKey(plugin, "waypoint_y"), PersistentDataType.DOUBLE, waypoint.getLocation().getY());
    pdc.set(new NamespacedKey(plugin, "waypoint_z"), PersistentDataType.DOUBLE, waypoint.getLocation().getZ());
    pdc.set(new NamespacedKey(plugin, "waypoint_world"), PersistentDataType.STRING, waypoint.getLocation().getWorld().getName());
    pdc.set(new NamespacedKey(plugin, "waypoint_cost"), PersistentDataType.INTEGER, cost);

    item.setItemMeta(meta);
    return item;
  }

  private Location findSafeLocation(Location dest, int range) {
    for (int dx = -range; dx <= range; dx++) {
      for (int dz = -range; dz <= range; dz++) {
        Location loc = dest.clone().add(dx, -1, dz);

        Block feet = loc.getBlock();
        Block head = loc.clone().add(0, 1, 0).getBlock();
        Block aboveHead = loc.clone().add(0, 2, 0).getBlock();

        if (locationIsPlayerSafe(feet, head, aboveHead)) return loc.clone().add(0.5, 1, 0.5);
      }
    }

    return dest.add(0.5, 0.57, 0.5);
  }

  private boolean locationIsPlayerSafe(Block feet, Block head, Block above_head) {
    return feet.getType().isSolid() && head.getType() == Material.AIR && above_head.getType() == Material.AIR;
  }

}
