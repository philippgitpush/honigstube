package com.philippgitpush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.philippgitpush.commands.BedCommand;

public class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    Bukkit.getLogger().warning("[Honigstube] Hallo Welt o/");

    // Register Listeners
    getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);

    // Register Commands
    getCommand("bed").setExecutor(new BedCommand());
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().warning("[Honigstube] Byebye o/");
  }
}
