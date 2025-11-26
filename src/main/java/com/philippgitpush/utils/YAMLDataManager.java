package com.philippgitpush.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class YAMLDataManager {

    private final JavaPlugin plugin;
    private File data_file;
    private FileConfiguration data_config;

    public YAMLDataManager(JavaPlugin plugin, String file_name) {
        this.plugin = plugin;
        this.data_file = new File(plugin.getDataFolder(), file_name);
        if (!data_file.exists()) plugin.saveResource(file_name, false);
        this.data_config = YamlConfiguration.loadConfiguration(data_file);
    }

    public void save() {
        try {
            data_config.save(data_file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        this.data_config = YamlConfiguration.loadConfiguration(data_file);
    }

    public void saveData(String path, Object value) {
        data_config.set(path, value);
        save();
    }

    public Object loadData(String path) {
        return data_config.get(path);
    }

    public Set<String> getKeys(String path) {
        if (data_config.contains(path)) return data_config.getConfigurationSection(path).getKeys(false);
        return null;
    }

    public FileConfiguration getAllData() {
        return data_config;
    }

    public void saveAll() {
        save();
    }

    public void deleteData(String path) {
        data_config.set(path, null);
        save();
    }
}
