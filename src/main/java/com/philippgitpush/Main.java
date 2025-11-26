package com.philippgitpush;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.philippgitpush.commands.BedCommand;
import com.philippgitpush.commands.SitCommand;
import com.philippgitpush.commands.TestCommand;
import com.philippgitpush.listeners.BackpackListener;
import com.philippgitpush.listeners.CatchEntitiesListener;
import com.philippgitpush.listeners.DimensionsHopListener;
import com.philippgitpush.listeners.PiggybackListener;
import com.philippgitpush.listeners.SitListener;
import com.philippgitpush.listeners.TimberListener;
import com.philippgitpush.listeners.WaypointListener;
import com.philippgitpush.utils.WaypointManager;
import com.philippgitpush.utils.YAMLDataManager;

import net.kyori.adventure.text.Component;

public class Main extends JavaPlugin {

  private YAMLDataManager data_manager;
  private WaypointManager waypoint_manager;
  
  public YAMLDataManager getDataManager() {
    return data_manager;
  }

  public WaypointManager getWaypointManager() {
    return waypoint_manager;
  }

  @Override
  public void onEnable() {
    Bukkit.getLogger().warning("[Honigstube] Hallo Welt o/");

    // Load presistent data
    data_manager = new YAMLDataManager(this, "data.yml");
    data_manager.reload();

    // Waypoints
    waypoint_manager = new WaypointManager(data_manager);

    // Tab info
    startTabInfoTask();

    // Register Listeners
    getServer().getPluginManager().registerEvents(new TimberListener(), this);
    getServer().getPluginManager().registerEvents(new CatchEntitiesListener(), this);
    getServer().getPluginManager().registerEvents(new PiggybackListener(this), this);
    getServer().getPluginManager().registerEvents(new BackpackListener(), this);
    getServer().getPluginManager().registerEvents(new SitListener(), this);
    getServer().getPluginManager().registerEvents(new DimensionsHopListener(), this);
    getServer().getPluginManager().registerEvents(new WaypointListener(this), this);

    // Register Commands
    getCommand("bed").setExecutor(new BedCommand());
    getCommand("sit").setExecutor(new SitCommand());
    getCommand("honigstube").setExecutor(new TestCommand());
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().warning("[Honigstube] Byebye o/");

    // Save presistent data
    if (data_manager != null) data_manager.saveAll();
  }

  private void startTabInfoTask() {
    new BukkitRunnable() {
      @Override
      public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
          if (player == null) return;

          String footerText = "\n\u00a7f" + player.getPing() + " \u00a77ms\n";
          String headerText = "\n   \u00a7eHonig\u00a76stube \u00a77- \u00a7fmap.philipp.sh   \n";

          player.sendPlayerListHeaderAndFooter(Component.text(headerText),Component.text(footerText));
        }
      }
    }.runTaskTimer(this, 0L, 20L);
  }
  
}
