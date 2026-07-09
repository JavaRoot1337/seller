package ru.javaroot.seller.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.javaroot.seller.Seller;

import java.io.File;

public class Settings {

    private final Seller plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public Settings(Seller plugin) {
        this.plugin = plugin;
    }

    public void load() {
        saveIfNotExists("config.yml");
        saveIfNotExists("message.yml");

        config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
        messages = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "message.yml"));
    }

    private void saveIfNotExists(String name) {
        if (!new File(plugin.getDataFolder(), name).exists()) {
            plugin.saveResource(name, false);
        }
    }

    public FileConfiguration get() {
        if (config == null)
            load();
        return config;
    }

    public FileConfiguration getMsg() {
        if (messages == null)
            load();
        return messages;
    }

    public void reload() {
        load();
    }
}
