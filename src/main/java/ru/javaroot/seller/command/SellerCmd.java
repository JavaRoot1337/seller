package ru.javaroot.seller.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.javaroot.seller.Seller;
import ru.javaroot.seller.gui.SellerMenu;
import ru.javaroot.seller.gui.SellerListMenu;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SellerCmd implements CommandExecutor, TabCompleter {

    private final Seller plugin;

    public SellerCmd(Seller plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String sub = args[0].toLowerCase();

            if (sub.equals("reload")) {
                if (!sender.hasPermission("seller.reload")) {
                    sender.sendMessage(plugin.getMessageWithPrefix("no-permission"));
                    return true;
                }
                plugin.getSettings().reload();
                sender.sendMessage(plugin.getMessageWithPrefix("reloaded"));
                return true;
            }

            if (sub.equals("list")) {
                if (sender instanceof Player p) {
                    SellerListMenu.open(plugin, p);
                } else {
                    sender.sendMessage(plugin.getMessageWithPrefix("only-players"));
                }
                return true;
            }
        }

        if (sender instanceof Player p) {
            SellerMenu.open(plugin, p);
        } else {
            sender.sendMessage(plugin.getMessageWithPrefix("only-players"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("list", "reload")
                    .filter(sub -> !sub.equals("reload") || sender.hasPermission("seller.reload"))
                    .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return Collections.emptyList();
    }
}
