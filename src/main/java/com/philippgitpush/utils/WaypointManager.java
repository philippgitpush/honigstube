package com.philippgitpush.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;

public class WaypointManager {

  private final YAMLDataManager data_manager;

  public WaypointManager(YAMLDataManager data_manager) {
    this.data_manager = data_manager;
    purgeWaypoints();
  }

  public static class Waypoint {

    private final String id;
    private final String name;
    private final String creator;
    private final Material material;
    private final Location location;

    public Waypoint(String id, String name, String creator, Material material, Location location) {
      this.id = id;
      this.name = name;
      this.creator = creator;
      this.material = material;
      this.location = location;
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getCreator() {
      return creator;
    }

    public Material getMaterial() {
      return material;
    }

    public Location getLocation() {
      return location;
    }
  }

  private String getLocationId(Location location) {
    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();
    return x + "_" + y + "_" + z;
  }

  public String createWaypoint(String display_name, String creator, Material material, Location location) {
    String id = getLocationId(location);
    String base = "waypoints." + id;

    data_manager.saveData(base + ".name", display_name);
    data_manager.saveData(base + ".creator", creator);
    data_manager.saveData(base + ".material", material.name());

    data_manager.saveData(base + ".location.world", location.getWorld().getName());
    data_manager.saveData(base + ".location.x", location.getX());
    data_manager.saveData(base + ".location.y", location.getY());
    data_manager.saveData(base + ".location.z", location.getZ());

    purgeWaypoints();

    return id;
  }

  public void renameWaypoint(String id, String new_name) {
    data_manager.saveData("waypoints." + id + ".name", new_name);
  }

  public Waypoint getWaypoint(Location location) {
    String id = getLocationId(location);
    String base = "waypoints." + id;

    Object waypoint = data_manager.loadData(base + ".name");
    if (waypoint == null) return null;

    String name = (String) waypoint;
    String creator = (String) data_manager.loadData(base + ".creator");
    Material material = Material.valueOf((String) data_manager.loadData(base + ".material"));

    String world = (String) data_manager.loadData(base + ".location.world");
    double x = data_manager.getAllData().getDouble(base + ".location.x");
    double y = data_manager.getAllData().getDouble(base + ".location.y");
    double z = data_manager.getAllData().getDouble(base + ".location.z");

    Location loc = new Location(Bukkit.getWorld(world), x, y, z);

    return new Waypoint(id, name, creator, material, loc);
  }

  public void deleteWaypoint(String id) {
    data_manager.deleteData("waypoints." + id);
  }

  public void purgeWaypoints() {
    Map<String, Waypoint> waypoints = getAllWaypoints();

    for (Waypoint waypoint : waypoints.values()) {
      Location loc = waypoint.getLocation();
      if (loc.getBlock() == null || !Tag.BEDS.isTagged(loc.getBlock().getType())) deleteWaypoint(waypoint.getId());
    }
  }

  public Map<String, Waypoint> getAllWaypoints() {
    Map<String, Waypoint> waypoints = new HashMap<>();

    Set<String> keys = data_manager.getKeys("waypoints");
    if (keys == null)
      return waypoints;

    for (String id : keys) {
      String base = "waypoints." + id;

      String name = (String) data_manager.loadData(base + ".name");
      String creator = (String) data_manager.loadData(base + ".creator");
      Material material = Material.valueOf((String) data_manager.loadData(base + ".material"));

      String world = (String) data_manager.loadData(base + ".location.world");
      double x = data_manager.getAllData().getDouble(base + ".location.x");
      double y = data_manager.getAllData().getDouble(base + ".location.y");
      double z = data_manager.getAllData().getDouble(base + ".location.z");

      Location loc = new Location(Bukkit.getWorld(world), x, y, z);

      waypoints.put(id, new Waypoint(id, name, creator, material, loc));
    }

    return waypoints;
  }
}
