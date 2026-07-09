package ru.javaroot.seller;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import ru.javaroot.seller.config.Settings;
import ru.javaroot.seller.economy.EconHook;
import ru.javaroot.seller.listener.MenuListener;
import ru.javaroot.seller.command.SellerCmd;

public class Seller extends JavaPlugin {

    private static Seller instance;
    private Settings settings;
    private EconHook econHook;

    @Override
    public void onEnable() {
        instance = this;

        settings = new Settings(this);
        settings.load();

        econHook = new EconHook(this);
        if (!econHook.setup()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        SellerCmd cmd = new SellerCmd(this);
        var command = getCommand("seller");
        if (command != null) {
            command.setExecutor(cmd);
            command.setTabCompleter(cmd);
        }

        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
    }

    @Override
    public void onDisable() {
    }

    public static Seller getInstance() {
        return instance;
    }

    public Settings getSettings() {
        return settings;
    }

    public EconHook getEconHook() {
        return econHook;
    }

    public String getMessage(String path) {
        String msg = settings.getMsg().getString(path);
        return msg == null ? "" : colorize(msg);
    }

    public String getMessageWithPrefix(String path) {
        return getMessage("prefix") + getMessage(path);
    }

    public String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
