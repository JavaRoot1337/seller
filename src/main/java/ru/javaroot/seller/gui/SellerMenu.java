package ru.javaroot.seller.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.javaroot.seller.Seller;

import java.util.List;

public class SellerMenu implements InventoryHolder {

    private final Seller plugin;
    private final Inventory inventory;

    public SellerMenu(Seller plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(this, 54, plugin.getMessage("gui-title"));
        setup();
    }

    public static void open(Seller plugin, Player p) {
        p.openInventory(new SellerMenu(plugin).getInventory());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void setup() {
        String borderMatName = plugin.getSettings().get().getString("seller-menu.border-material",
                "WHITE_STAINED_GLASS_PANE");
        Material borderMat = Material.matchMaterial(borderMatName);
        if (borderMat == null)
            borderMat = Material.WHITE_STAINED_GLASS_PANE;

        ItemStack glass = new ItemStack(borderMat);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);

        for (int i = 45; i < 53; i++) {
            inventory.setItem(i, glass);
        }

        updateSubmitButton();
    }

    public void updateSubmitButton() {
        double totalValue = calculateTotalValue();
        String submitMatName = plugin.getSettings().get().getString("seller-menu.submit-material", "PAPER");
        Material submitMat = Material.matchMaterial(submitMatName);
        if (submitMat == null)
            submitMat = Material.PAPER;

        ItemStack submitItem = new ItemStack(submitMat);
        ItemMeta submitMeta = submitItem.getItemMeta();

        submitMeta.setDisplayName(plugin.getMessage("gui-submit-name"));

        List<String> lore = plugin.getSettings().getMsg().getStringList("gui-submit-lore");
        List<String> formattedLore = lore.stream()
                .map(line -> plugin.colorize(line.replace("%amount%", String.format("%.1f", totalValue))
                        .replace("%amout%", String.format("%.1f", totalValue))))
                .toList();

        submitMeta.setLore(formattedLore);
        submitMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
        submitMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        submitItem.setItemMeta(submitMeta);

        inventory.setItem(53, submitItem);
    }

    public double calculateTotalValue() {
        double total = 0.0;
        var itemsSection = plugin.getSettings().get().getConfigurationSection("items");
        if (itemsSection == null)
            return total;

        for (int i = 0; i < 45; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType() == Material.AIR)
                continue;

            String materialName = item.getType().name();
            if (itemsSection.contains(materialName)) {
                double price = itemsSection.getDouble(materialName + ".price");
                total += price * item.getAmount();
            }
        }
        return total;
    }
}
