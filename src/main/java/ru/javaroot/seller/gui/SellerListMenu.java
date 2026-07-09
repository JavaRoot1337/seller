package ru.javaroot.seller.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.javaroot.seller.Seller;

import java.util.List;

public class SellerListMenu implements InventoryHolder {

    private final Seller plugin;
    private final Inventory inventory;

    public SellerListMenu(Seller plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54, plugin.getMessage("list-title"));
        setup();
    }

    public static void open(Seller plugin, Player p) {
        p.openInventory(new SellerListMenu(plugin).getInventory());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void setup() {
        String borderMatName = plugin.getSettings().get().getString("list-menu.border-material",
                "GRAY_STAINED_GLASS_PANE");
        Material borderMat = Material.matchMaterial(borderMatName);
        if (borderMat == null)
            borderMat = Material.GRAY_STAINED_GLASS_PANE;

        ItemStack border = new ItemStack(borderMat);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(" ");
        border.setItemMeta(borderMeta);

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i >= 45 || i % 9 == 0 || i % 9 == 8) {
                inventory.setItem(i, border);
            }
        }

        var itemsSection = plugin.getSettings().get().getConfigurationSection("items");
        if (itemsSection == null)
            return;

        int slot = 10;
        String loreFormat = plugin.getSettings().getMsg().getString("list-item-lore",
                "&7Стоимость сдачи: &b%price% &7монеток");

        for (String key : itemsSection.getKeys(false)) {
            Material mat = Material.matchMaterial(key);
            if (mat == null)
                continue;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            String customName = itemsSection.getString(key + ".name");
            double price = itemsSection.getDouble(key + ".price");

            meta.setDisplayName(customName != null ? plugin.colorize(customName) : plugin.colorize("&f" + mat.name()));

            String formattedPrice = String.format("%.2f", price);
            meta.setLore(List.of(
                    "",
                    plugin.colorize(loreFormat.replace("%price%", formattedPrice))));
            item.setItemMeta(meta);

            while (slot < 45 && inventory.getItem(slot) != null) {
                slot++;
            }

            if (slot >= 45)
                break;

            inventory.setItem(slot, item);
            slot++;
        }
    }
}
