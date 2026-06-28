package com.ultimatedoughnut.donutweapons.commands;

import com.ultimatedoughnut.donutweapons.DonutWeapons;
import com.ultimatedoughnut.donutweapons.managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DonutWeaponsCommand implements CommandExecutor, TabCompleter {

    private final DonutWeapons plugin;
    private final ItemManager im;

    private static final List<String> ITEM_NAMES = Arrays.asList(
            "soul-blade", "inferno-axe", "void-bow", "titan-chestplate", "death-totem"
    );

    public DonutWeaponsCommand(DonutWeapons plugin) {
        this.plugin = plugin;
        this.im = plugin.getItemManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("donutweapons.admin")) {
            sender.sendMessage(Component.text(
                    plugin.getConfig().getString("messages.no-permission", "§cNo permission.").replace("§", "\u00a7"))
                    .color(NamedTextColor.RED));
            return true;
        }

        // /donutweapons <item> [player]
        // /dwgive <player> <item>
        String itemName;
        Player target;

        if (label.equalsIgnoreCase("dwgive")) {
            if (args.length < 2) {
                sender.sendMessage(Component.text("Usage: /dwgive <player> <item>").color(NamedTextColor.RED));
                return true;
            }
            target = Bukkit.getPlayer(args[0]);
            itemName = args[1].toLowerCase();
        } else {
            if (args.length < 1) {
                sender.sendMessage(Component.text("Usage: /donutweapons <item> [player]").color(NamedTextColor.RED));
                sendItemList(sender);
                return true;
            }
            itemName = args[0].toLowerCase();
            if (args.length >= 2) {
                target = Bukkit.getPlayer(args[1]);
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Component.text("Console must specify a player.").color(NamedTextColor.RED));
                    return true;
                }
                target = (Player) sender;
            }
        }

        if (target == null) {
            sender.sendMessage(Component.text(
                    plugin.getConfig().getString("messages.player-not-found", "§cPlayer not found.").replace("§", "\u00a7")));
            return true;
        }

        ItemStack item = resolveItem(itemName);
        if (item == null) {
            sender.sendMessage(Component.text(
                    plugin.getConfig().getString("messages.invalid-item",
                            "§cInvalid item.").replace("§", "\u00a7")));
            sendItemList(sender);
            return true;
        }

        target.getInventory().addItem(item);
        target.sendMessage(Component.text("You received: ")
                .color(NamedTextColor.GREEN)
                .append(item.displayName()));

        if (!target.equals(sender)) {
            sender.sendMessage(Component.text("Gave ")
                    .color(NamedTextColor.GREEN)
                    .append(item.displayName())
                    .append(Component.text(" to " + target.getName()).color(NamedTextColor.GREEN)));
        }

        return true;
    }

    private ItemStack resolveItem(String name) {
        return switch (name) {
            case "soul-blade"        -> im.buildSoulBlade();
            case "inferno-axe"       -> im.buildInfernoAxe();
            case "void-bow"          -> im.buildVoidBow();
            case "titan-chestplate"  -> im.buildTitanChestplate();
            case "death-totem"       -> im.buildDeathTotem();
            default                  -> null;
        };
    }

    private void sendItemList(CommandSender sender) {
        sender.sendMessage(Component.text("Available items: ").color(NamedTextColor.YELLOW)
                .append(Component.text(String.join(", ", ITEM_NAMES)).color(NamedTextColor.WHITE)));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("donutweapons.admin")) return List.of();

        if (label.equalsIgnoreCase("dwgive")) {
            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 2) {
                return ITEM_NAMES.stream()
                        .filter(n -> n.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else {
            if (args.length == 1) {
                return ITEM_NAMES.stream()
                        .filter(n -> n.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(n -> n.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }
}
