package com.philippgitpush;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.philippgitpush.commands.BedCommand;
import com.philippgitpush.commands.SitCommand;
import com.philippgitpush.commands.TestCommand;
import com.philippgitpush.listeners.TimberListener;
import com.philippgitpush.listeners.CatchEntitiesListener;
import com.philippgitpush.listeners.DimensionsHopListener;
import com.philippgitpush.listeners.PiggybackListener;
import com.philippgitpush.listeners.SitListener;
import com.philippgitpush.listeners.BackpackListener;

public class Main extends JavaPlugin {
  
  @Override
  public void onEnable() {
    Bukkit.getLogger().warning("[Honigstube] Hallo Welt o/");

    // Register Listeners
    getServer().getPluginManager().registerEvents(new TimberListener(), this);
    getServer().getPluginManager().registerEvents(new CatchEntitiesListener(), this);
    getServer().getPluginManager().registerEvents(new PiggybackListener(this), this);
    getServer().getPluginManager().registerEvents(new BackpackListener(this), this);
    getServer().getPluginManager().registerEvents(new SitListener(), this);
    getServer().getPluginManager().registerEvents(new DimensionsHopListener(), this);

    // Register Commands
    getCommand("bed").setExecutor(new BedCommand());
    getCommand("sit").setExecutor(new SitCommand());
    getCommand("honigstube").setExecutor(new TestCommand());
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().warning("[Honigstube] Byebye o/");
  }
  
}
