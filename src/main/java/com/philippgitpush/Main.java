package com.philippgitpush;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    Bukkit.getLogger().warning("Hallo Welt o/");
    getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
  }

  @Override
  public void onDisable() {
    Bukkit.getLogger().warning("Byebye o/");
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("bed")) {

      if (!(sender instanceof Player)) return true;

      Player player = (Player) sender;

      Location bed = player.getRespawnLocation();

      if (bed == null) {
        player.sendMessage(ChatColor.RED + "Du hast kein Bett, zu dem du gehen kannst.");
        return true;
      }

      player.teleport(bed);
      return true;
    }

    return false;
  }
}