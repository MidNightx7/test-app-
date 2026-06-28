package com.ultimatedoughnut.donutweapons.listeners;

import com.ultimatedoughnut.donutweapons.DonutWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    private final DonutWeapons plugin;

    public CraftingListener(DonutWeapons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        ItemStack result = e.getRecipe().getResult();
        if (result == null || !result.hasItemMeta()) return;

        // Check permission to craft custom items
        if (!player.hasPermission("donutweapons.craft")) {
            e.setCancelled(true);
            player.sendMessage(Component.text("You don't have permission to craft DonutWeapons items!")
                    .color(NamedTextColor.RED));
            return;
        }

        var im = plugin.getItemManager();

        // Notify player on successful craft
        if (im.isSoulBlade(result)) {
            player.sendMessage(Component.text("⚡ You have crafted the ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Soul Blade").color(NamedTextColor.DARK_PURPLE))
                    .append(Component.text("!").color(NamedTextColor.GRAY)));
        } else if (im.isInfernoAxe(result)) {
            player.sendMessage(Component.text("🔥 You have crafted the ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Inferno Axe").color(NamedTextColor.RED))
                    .append(Component.text("!").color(NamedTextColor.GRAY)));
        } else if (im.isVoidBow(result)) {
            player.sendMessage(Component.text("🌀 You have crafted the ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Void Bow").color(NamedTextColor.DARK_GRAY))
                    .append(Component.text("!").color(NamedTextColor.GRAY)));
        } else if (im.isTitanChestplate(result)) {
            player.sendMessage(Component.text("🛡 You have crafted the ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Titan Chestplate").color(NamedTextColor.WHITE))
                    .append(Component.text("!").color(NamedTextColor.GRAY)));
        } else if (im.isDeathTotem(result)) {
            player.sendMessage(Component.text("💀 You have crafted the ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text("Death Totem").color(NamedTextColor.BLACK))
                    .append(Component.text("!").color(NamedTextColor.GRAY)));
        }
    }
}
