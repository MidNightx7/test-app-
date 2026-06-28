package com.ultimatedoughnut.donutweapons;

import com.ultimatedoughnut.donutweapons.commands.DonutWeaponsCommand;
import com.ultimatedoughnut.donutweapons.listeners.CraftingListener;
import com.ultimatedoughnut.donutweapons.listeners.ItemEffectListener;
import com.ultimatedoughnut.donutweapons.managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public class DonutWeapons extends JavaPlugin {

    private static DonutWeapons instance;
    private ItemManager itemManager;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();

        // Init managers
        itemManager = new ItemManager(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new ItemEffectListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);

        // Register commands
        DonutWeaponsCommand cmdExecutor = new DonutWeaponsCommand(this);
        getCommand("donutweapons").setExecutor(cmdExecutor);
        getCommand("donutweapons").setTabCompleter(cmdExecutor);
        getCommand("dwgive").setExecutor(cmdExecutor);
        getCommand("dwgive").setTabCompleter(cmdExecutor);

        // Register custom recipes
        itemManager.registerRecipes();

        getLogger().info("DonutWeapons enabled! 5 overpowered items loaded.");
        getLogger().info("UltimateDonutSMP — may God have mercy on your players.");
    }

    @Override
    public void onDisable() {
        getLogger().info("DonutWeapons disabled.");
    }

    public static DonutWeapons getInstance() {
        return instance;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }
}
