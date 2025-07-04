package com.philippgitpush;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.philippgitpush.commands.BedCommand;
import com.philippgitpush.listeners.BlockBreakListener;
import com.philippgitpush.listeners.ProjectileHitListener;
import com.philippgitpush.listeners.PlayerEggThrowListener;

public class Main extends JavaPlugin {
  
  @Override
  public void onEnable() {
    Bukkit.getLogger().warning("[Honigstube] Hallo Welt o/");

    // Register Listeners
    getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerEggThrowListener(), this);
    getServer().getPluginManager().registerEvents(new ProjectileHitListener(), this);

    // Register Commands
    getCommand("bed").setExecutor(new BedCommand());
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().warning("[Honigstube] Byebye o/");
  }
  
}
