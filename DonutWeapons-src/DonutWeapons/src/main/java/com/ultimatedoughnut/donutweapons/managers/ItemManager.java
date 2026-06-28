package com.ultimatedoughnut.donutweapons.managers;

import com.ultimatedoughnut.donutweapons.DonutWeapons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

public class ItemManager {

    private final DonutWeapons plugin;

    // PDC Keys for identifying items
    public final NamespacedKey SOUL_BLADE_KEY;
    public final NamespacedKey INFERNO_AXE_KEY;
    public final NamespacedKey VOID_BOW_KEY;
    public final NamespacedKey TITAN_CHESTPLATE_KEY;
    public final NamespacedKey DEATH_TOTEM_KEY;
    public final NamespacedKey DEATH_TOTEM_COOLDOWN_KEY;

    public ItemManager(DonutWeapons plugin) {
        this.plugin = plugin;
        SOUL_BLADE_KEY = new NamespacedKey(plugin, "soul_blade");
        INFERNO_AXE_KEY = new NamespacedKey(plugin, "inferno_axe");
        VOID_BOW_KEY = new NamespacedKey(plugin, "void_bow");
        TITAN_CHESTPLATE_KEY = new NamespacedKey(plugin, "titan_chestplate");
        DEATH_TOTEM_KEY = new NamespacedKey(plugin, "death_totem");
        DEATH_TOTEM_COOLDOWN_KEY = new NamespacedKey(plugin, "death_totem_cooldown");
    }

    // ─────────────────────────────────────────────
    // ITEM BUILDERS
    // ─────────────────────────────────────────────

    public ItemStack buildSoulBlade() {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Soul Blade")
                .color(NamedTextColor.DARK_PURPLE)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(Arrays.asList(
                Component.text(""),
                Component.text("Instant kill on critical hit.")
                        .color(NamedTextColor.LIGHT_PURPLE)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Calls lightning upon your fallen foes.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("✦ Forged from stolen souls")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true)
        ));

        meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 3, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 3, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.getPersistentDataContainer().set(SOUL_BLADE_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack buildInfernoAxe() {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Inferno Axe")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(Arrays.asList(
                Component.text(""),
                Component.text("Sets targets ablaze for 10 seconds.")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Kills trigger a fiery explosion.")
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("✦ Tempered in the nether's core")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true)
        ));

        meta.addEnchant(Enchantment.DAMAGE_ALL, 8, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 5, true);
        meta.addEnchant(Enchantment.DIG_SPEED, 8, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.getPersistentDataContainer().set(INFERNO_AXE_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack buildVoidBow() {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Void Bow")
                .color(NamedTextColor.DARK_GRAY)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(Arrays.asList(
                Component.text(""),
                Component.text("Arrows teleport targets to your location.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("No escape. No distance. No mercy.")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("✦ Woven from the void between worlds")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true)
        ));

        meta.addEnchant(Enchantment.ARROW_DAMAGE, 10, true);
        meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 5, true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.getPersistentDataContainer().set(VOID_BOW_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack buildTitanChestplate() {
        ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Titan Chestplate")
                .color(NamedTextColor.GRAY)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(Arrays.asList(
                Component.text(""),
                Component.text("90% damage reduction.")
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Immune to knockback.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Slowly regenerates health.")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("✦ Worn by those who cannot be stopped")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true)
        ));

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
        meta.addEnchant(Enchantment.THORNS, 5, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        meta.getPersistentDataContainer().set(TITAN_CHESTPLATE_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack buildDeathTotem() {
        ItemStack item = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(Component.text("Death Totem")
                .color(NamedTextColor.BLACK)
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(Arrays.asList(
                Component.text(""),
                Component.text("On death: survive, explode, and respawn.")
                        .color(NamedTextColor.DARK_RED)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Grants Strength II + Speed II on respawn.")
                        .color(NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("5 minute cooldown.")
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text(""),
                Component.text("✦ Death itself fears the holder")
                        .color(NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, true)
        ));

        meta.getPersistentDataContainer().set(DEATH_TOTEM_KEY, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    // ─────────────────────────────────────────────
    // RECIPE REGISTRATION
    // ─────────────────────────────────────────────

    public void registerRecipes() {
        registerSoulBladeRecipe();
        registerInfernoAxeRecipe();
        registerVoidBowRecipe();
        registerTitanChestplateRecipe();
        registerDeathTotemRecipe();
        plugin.getLogger().info("All 5 custom recipes registered.");
    }

    private void registerSoulBladeRecipe() {
        // Soul Blade recipe:
        // N = Nether Star, D = Diamond Sword, B = Blaze Rod
        //  N N N
        //  D N D
        //  B   B
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "soul_blade_recipe"),
                buildSoulBlade()
        );
        recipe.shape("NNN", "DND", "B B");
        recipe.setIngredient('N', Material.NETHER_STAR);
        recipe.setIngredient('D', Material.DIAMOND_SWORD);
        recipe.setIngredient('B', Material.BLAZE_ROD);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerInfernoAxeRecipe() {
        // Inferno Axe recipe:
        // M = Magma Block, B = Blaze Rod, N = Netherite Ingot
        //  M M M
        //  B N B
        //  M B M
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "inferno_axe_recipe"),
                buildInfernoAxe()
        );
        recipe.shape("MMM", "BNB", "MBM");
        recipe.setIngredient('M', Material.MAGMA_BLOCK);
        recipe.setIngredient('B', Material.BLAZE_ROD);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerVoidBowRecipe() {
        // Void Bow recipe:
        // E = Eye of Ender, O = Obsidian, S = String
        //  E O E
        //  O E O
        //  S O S
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "void_bow_recipe"),
                buildVoidBow()
        );
        recipe.shape("EOE", "OEO", "SOS");
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setIngredient('O', Material.OBSIDIAN);
        recipe.setIngredient('S', Material.STRING);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerTitanChestplateRecipe() {
        // Titan Chestplate recipe:
        // N = Netherite Ingot, B = Beacon, D = Diamond
        //  N B N
        //  D N D
        //  N D N
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "titan_chestplate_recipe"),
                buildTitanChestplate()
        );
        recipe.shape("NBN", "DND", "NDN");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('B', Material.BEACON);
        recipe.setIngredient('D', Material.DIAMOND);
        plugin.getServer().addRecipe(recipe);
    }

    private void registerDeathTotemRecipe() {
        // Death Totem recipe:
        // T = Totem of Undying, W = Wither Skeleton Skull, G = Gold Block
        //  W G W
        //  G T G
        //  W G W
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(plugin, "death_totem_recipe"),
                buildDeathTotem()
        );
        recipe.shape("WGW", "GTG", "WGW");
        recipe.setIngredient('W', Material.WITHER_SKELETON_SKULL);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('T', Material.TOTEM_OF_UNDYING);
        plugin.getServer().addRecipe(recipe);
    }

    // ─────────────────────────────────────────────
    // ITEM IDENTIFICATION HELPERS
    // ─────────────────────────────────────────────

    public boolean isSoulBlade(ItemStack item) {
        return hasKey(item, SOUL_BLADE_KEY);
    }

    public boolean isInfernoAxe(ItemStack item) {
        return hasKey(item, INFERNO_AXE_KEY);
    }

    public boolean isVoidBow(ItemStack item) {
        return hasKey(item, VOID_BOW_KEY);
    }

    public boolean isTitanChestplate(ItemStack item) {
        return hasKey(item, TITAN_CHESTPLATE_KEY);
    }

    public boolean isDeathTotem(ItemStack item) {
        return hasKey(item, DEATH_TOTEM_KEY);
    }

    private boolean hasKey(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(key, PersistentDataType.BOOLEAN);
    }
}
