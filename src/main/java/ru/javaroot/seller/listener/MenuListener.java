package ru.javaroot.seller.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.javaroot.seller.Seller;
import ru.javaroot.seller.gui.SellerMenu;
import ru.javaroot.seller.gui.SellerListMenu;

public class MenuListener implements Listener {

    private final Seller plugin;

    public MenuListener(Seller plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof SellerListMenu) {
            event.setCancelled(true);
            return;
        }

        if (inv.getHolder() instanceof SellerMenu gui) {
            int slot = event.getRawSlot();
            var clickedInv = event.getClickedInventory();

            if (clickedInv != null && clickedInv.equals(event.getView().getTopInventory())) {
                if (slot >= 45 && slot <= 52) {
                    event.setCancelled(true);
                    return;
                }

                if (slot == 53) {
                    event.setCancelled(true);
                    if (event.getWhoClicked() instanceof Player player) {
                        sellItems(player, gui);
                    }
                    return;
                }
            }

            if (event.isShiftClick() && clickedInv != null && clickedInv.equals(event.getView().getBottomInventory())) {
                ItemStack toShift = event.getCurrentItem();
                if (toShift != null && toShift.getType() != Material.AIR) {
                    boolean hasSpace = false;
                    for (int i = 0; i < 45; i++) {
                        ItemStack topItem = inv.getItem(i);
                        if (topItem == null || topItem.getType() == Material.AIR ||
                                (topItem.isSimilar(toShift) && topItem.getAmount() < topItem.getMaxStackSize())) {
                            hasSpace = true;
                            break;
                        }
                    }
                    if (!hasSpace) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            Bukkit.getScheduler().runTask(plugin, gui::updateSubmitButton);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getHolder() instanceof SellerListMenu) {
            event.setCancelled(true);
            return;
        }

        if (inv.getHolder() instanceof SellerMenu gui) {
            for (int slot : event.getRawSlots()) {
                if (slot >= 45) {
                    event.setCancelled(true);
                    return;
                }
            }
            Bukkit.getScheduler().runTask(plugin, gui::updateSubmitButton);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof SellerMenu && event.getPlayer() instanceof Player player) {
            Inventory inv = event.getInventory();
            for (int i = 0; i < 45; i++) {
                ItemStack item = inv.getItem(i);
                if (item != null && item.getType() != Material.AIR) {
                    var remaining = player.getInventory().addItem(item);
                    if (!remaining.isEmpty()) {
                        for (ItemStack remainingItem : remaining.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), remainingItem);
                        }
                    }
                    inv.setItem(i, null);
                }
            }
        }
    }

    private void sellItems(Player player, SellerMenu gui) {
        double payout = 0.0;
        var itemsSection = plugin.getSettings().get().getConfigurationSection("items");
        if (itemsSection == null) {
            player.sendMessage(plugin.getMessageWithPrefix("no-items-to-sell"));
            return;
        }

        Inventory inv = gui.getInventory();
        boolean soldAny = false;

        for (int i = 0; i < 45; i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR)
                continue;

            String matName = item.getType().name();
            if (itemsSection.contains(matName)) {
                double price = itemsSection.getDouble(matName + ".price");
                payout += price * item.getAmount();
                inv.setItem(i, null);
                soldAny = true;
            }
        }

        if (soldAny && payout > 0.0) {
            plugin.getEconHook().deposit(player, payout);
            player.sendMessage(plugin.getMessageWithPrefix("sold-success")
                    .replace("%amount%", String.format("%.1f", payout)));
        } else {
            player.sendMessage(plugin.getMessageWithPrefix("no-items-to-sell"));
        }

        gui.updateSubmitButton();
    }
}
