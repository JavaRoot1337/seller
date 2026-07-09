package ru.javaroot.seller.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.javaroot.seller.Seller;

public class EconHook {

    private final Seller plugin;
    private Economy economy;

    public EconHook(Seller plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().severe("Не найден плагин Vault! Экономика отключена.");
            return false;
        }
        if (Bukkit.getPluginManager().getPlugin("Essentials") == null) {
            plugin.getLogger().warning("EssentialsX не обнаружен. На всякий случай предупреждаем.");
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().severe("Не удалось зарегистрировать провайдер Vault Economy!");
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean deposit(Player p, double amount) {
        if (economy == null)
            return false;
        return economy.depositPlayer(p, amount).transactionSuccess();
    }

    public String format(double amount) {
        return economy == null ? String.format("%.2f", amount) : economy.format(amount);
    }
}
