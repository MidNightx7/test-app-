package com.ultimatedoughnut.donutweapons.listeners;

import com.ultimatedoughnut.donutweapons.DonutWeapons;
import com.ultimatedoughnut.donutweapons.managers.ItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemEffectListener implements Listener {

    private final DonutWeapons plugin;
    private final ItemManager im;

    // Track arrows shot by void bow: arrowUUID -> shooterUUID
    private final Map<UUID, UUID> voidArrows = new HashMap<>();
    // Track death totem cooldowns: playerUUID -> cooldown expiry timestamp
    private final Map<UUID, Long> deathTotemCooldowns = new HashMap<>();
    // Track titan chestplate regen task per player
    private final Map<UUID, Integer> regenTasks = new HashMap<>();

    public ItemEffectListener(DonutWeapons plugin) {
        this.plugin = plugin;
        this.im = plugin.getItemManager();
    }

    // ─────────────────────────────────────────────
    // SOUL BLADE — instant kill on crit + lightning
    // ─────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH)
    public void onSoulBladeHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;
        if (!(e.getEntity() instanceof LivingEntity victim)) return;

        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (!im.isSoulBlade(hand)) return;

        boolean isCrit = attacker.getFallDistance() > 0
                && !attacker.isOnGround()
                && !attacker.hasPotionEffect(PotionEffectType.BLINDNESS)
                && attacker.getVelocity().getY() < 0;

        if (isCrit && plugin.getConfig().getBoolean("soul-blade.instant-kill-on-crit")) {
            // Instant kill — set damage to victim's max health
            if (victim instanceof Player) {
                e.setDamage(((Player) victim).getMaxHealth());
            } else {
                e.setDamage(victim.getMaxHealth());
            }

            // Particles
            if (plugin.getConfig().getBoolean("particles.soul-blade-hit")) {
                victim.getWorld().spawnParticle(Particle.SPELL_WITCH, victim.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5, 0.1);
                victim.getWorld().spawnParticle(Particle.PORTAL, victim.getLocation().add(0, 1, 0), 60, 0.5, 1, 0.5, 0.5);
            }
        } else {
            // Non-crit: apply damage multiplier
            double mult = plugin.getConfig().getDouble("soul-blade.damage-multiplier", 3.0);
            e.setDamage(e.getDamage() * mult);
        }
    }

    @EventHandler
    public void onSoulBladeKill(EntityDeathEvent e) {
        LivingEntity victim = e.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        ItemStack hand = killer.getInventory().getItemInMainHand();
        if (!im.isSoulBlade(hand)) return;

        if (plugin.getConfig().getBoolean("soul-blade.lightning-on-kill")) {
            victim.getWorld().strikeLightningEffect(victim.getLocation());

            if (plugin.getConfig().getBoolean("particles.soul-blade-hit")) {
                victim.getWorld().spawnParticle(Particle.SOUL, victim.getLocation().add(0, 1, 0), 30, 0.3, 1, 0.3, 0.05);
            }
        }

        // Broadcast kill message
        String msg = plugin.getConfig().getString("messages.soul-blade-kill", "")
                .replace("{killer}", killer.getName())
                .replace("{victim}", victim instanceof Player ? ((Player) victim).getName() : victim.getName());
        if (!msg.isEmpty()) {
            Bukkit.broadcast(Component.text(msg.replace("§", "\u00a7")));
        }
    }

    // ─────────────────────────────────────────────
    // INFERNO AXE — fire + explosion on kill
    // ─────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH)
    public void onInfernoAxeHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player attacker)) return;
        if (!(e.getEntity() instanceof LivingEntity victim)) return;

        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (!im.isInfernoAxe(hand)) return;

        int fireDuration = plugin.getConfig().getInt("inferno-axe.fire-duration", 10);
        victim.setFireTicks(fireDuration * 20);

        if (plugin.getConfig().getBoolean("particles.inferno-axe-hit")) {
            victim.getWorld().spawnParticle(Particle.FLAME, victim.getLocation().add(0, 1, 0), 30, 0.3, 0.5, 0.3, 0.1);
            victim.getWorld().spawnParticle(Particle.LAVA, victim.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0);
        }
    }

    @EventHandler
    public void onInfernoAxeKill(EntityDeathEvent e) {
        LivingEntity victim = e.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        ItemStack hand = killer.getInventory().getItemInMainHand();
        if (!im.isInfernoAxe(hand)) return;

        if (plugin.getConfig().getBoolean("inferno-axe.explosion-on-kill")) {
            float power = (float) plugin.getConfig().getDouble("inferno-axe.explosion-power", 2.5);
            boolean breaksBlocks = plugin.getConfig().getBoolean("inferno-axe.explosion-breaks-blocks", false);
            victim.getWorld().createExplosion(victim.getLocation(), power, true, breaksBlocks);
        }

        String msg = plugin.getConfig().getString("messages.inferno-kill", "")
                .replace("{killer}", killer.getName())
                .replace("{victim}", victim instanceof Player ? ((Player) victim).getName() : victim.getName());
        if (!msg.isEmpty()) {
            Bukkit.broadcast(Component.text(msg.replace("§", "\u00a7")));
        }
    }

    // ─────────────────────────────────────────────
    // VOID BOW — tag arrows, teleport on hit
    // ─────────────────────────────────────────────

    @EventHandler
    public void onVoidBowShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player shooter)) return;
        if (!im.isVoidBow(e.getBow())) return;
        if (!(e.getProjectile() instanceof Arrow arrow)) return;

        voidArrows.put(arrow.getUniqueId(), shooter.getUniqueId());
    }

    @EventHandler
    public void onVoidArrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow arrow)) return;
        if (!voidArrows.containsKey(arrow.getUniqueId())) return;

        UUID shooterUUID = voidArrows.remove(arrow.getUniqueId());
        Player shooter = Bukkit.getPlayer(shooterUUID);
        if (shooter == null) return;

        Entity hitEntity = e.getHitEntity();
        if (!(hitEntity instanceof LivingEntity target)) return;
        if (target.getUniqueId().equals(shooterUUID)) return;

        boolean pullToShooter = plugin.getConfig().getBoolean("void-bow.pull-target-to-shooter", false);

        if (pullToShooter) {
            // Pull target to shooter
            target.teleport(shooter.getLocation());
            shooter.sendMessage(Component.text("Target pulled to you!")
                    .color(NamedTextColor.DARK_GRAY));
        } else {
            // Teleport shooter to target
            shooter.teleport(target.getLocation());
            shooter.sendMessage(Component.text("Teleported to target!")
                    .color(NamedTextColor.DARK_GRAY));
        }

        if (plugin.getConfig().getBoolean("void-bow.effect-on-teleport")) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 1));
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 2));
        }

        if (plugin.getConfig().getBoolean("particles.void-bow-teleport")) {
            target.getWorld().spawnParticle(Particle.PORTAL, target.getLocation().add(0, 1, 0), 60, 0.5, 1, 0.5, 0.3);
            target.getWorld().spawnParticle(Particle.REVERSE_PORTAL, target.getLocation().add(0, 1, 0), 30, 0.5, 1, 0.5, 0.1);
        }

        if (target instanceof Player targetPlayer) {
            String msg = plugin.getConfig().getString("messages.void-teleport", "");
            if (!msg.isEmpty()) {
                targetPlayer.sendMessage(Component.text(msg.replace("§", "\u00a7")));
            }
        }
    }

    // Clean up void arrows that miss
    @EventHandler
    public void onArrowDespawn(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow arrow && e.getHitEntity() == null) {
            voidArrows.remove(arrow.getUniqueId());
        }
    }

    // ─────────────────────────────────────────────
    // TITAN CHESTPLATE — damage reduction + knockback immunity + regen
    // ─────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGH)
    public void onTitanChestplateDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        ItemStack chest = player.getInventory().getChestplate();
        if (!im.isTitanChestplate(chest)) return;

        // Apply damage reduction
        double reduction = plugin.getConfig().getDouble("titan-chestplate.damage-reduction", 0.90);
        e.setDamage(e.getDamage() * (1.0 - reduction));

        if (plugin.getConfig().getBoolean("particles.titan-chestplate-hit")) {
            player.getWorld().spawnParticle(Particle.BLOCK_CRACK,
                    player.getLocation().add(0, 1, 0), 20, 0.3, 0.5, 0.3, 0,
                    Material.NETHERITE_BLOCK.createBlockData());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTitanKnockback(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;

        ItemStack chest = player.getInventory().getChestplate();
        if (!im.isTitanChestplate(chest)) return;

        if (plugin.getConfig().getBoolean("titan-chestplate.knockback-immunity")) {
            // Cancel knockback by scheduling velocity reset
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    player.setVelocity(player.getVelocity().setX(0).setZ(0)), 1L);
        }
    }

    // Start regen when chestplate is equipped (checked on move as lightweight tick)
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!plugin.getConfig().getBoolean("titan-chestplate.regeneration-enabled")) return;

        ItemStack chest = player.getInventory().getChestplate();
        boolean hasTitan = im.isTitanChestplate(chest);
        boolean hasTask = regenTasks.containsKey(player.getUniqueId());

        if (hasTitan && !hasTask) {
            startRegenTask(player);
        } else if (!hasTitan && hasTask) {
            stopRegenTask(player);
        }
    }

    private void startRegenTask(Player player) {
        int interval = plugin.getConfig().getInt("titan-chestplate.regeneration-interval", 40);
        int taskId = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    regenTasks.remove(player.getUniqueId());
                    return;
                }
                ItemStack chest = player.getInventory().getChestplate();
                if (!im.isTitanChestplate(chest)) {
                    cancel();
                    regenTasks.remove(player.getUniqueId());
                    return;
                }
                if (player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(Math.min(player.getHealth() + 1.0, player.getMaxHealth()));
                }
            }
        }.runTaskTimer(plugin, interval, interval).getTaskId();
        regenTasks.put(player.getUniqueId(), taskId);
    }

    private void stopRegenTask(Player player) {
        Integer taskId = regenTasks.remove(player.getUniqueId());
        if (taskId != null) Bukkit.getScheduler().cancelTask(taskId);
    }

    // ─────────────────────────────────────────────
    // DEATH TOTEM — survive death, explode, respawn with effects
    // ─────────────────────────────────────────────

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathTotemActivate(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // Check main hand or off hand for death totem
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        ItemStack totem = null;
        if (im.isDeathTotem(mainHand)) totem = mainHand;
        else if (im.isDeathTotem(offHand)) totem = offHand;

        if (totem == null) return;

        // Check cooldown
        long now = System.currentTimeMillis();
        Long cooldownExpiry = deathTotemCooldowns.get(player.getUniqueId());
        if (cooldownExpiry != null && now < cooldownExpiry) {
            long remaining = (cooldownExpiry - now) / 1000;
            String msg = plugin.getConfig().getString("messages.death-totem-cooldown", "")
                    .replace("{time}", String.valueOf(remaining));
            player.sendMessage(Component.text(msg.replace("§", "\u00a7")));
            return;
        }

        // Cancel death
        e.setCancelled(true);

        // Consume one totem
        if (totem.getAmount() > 1) {
            totem.setAmount(totem.getAmount() - 1);
        } else {
            if (im.isDeathTotem(mainHand)) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        }

        // Set cooldown
        int cooldownSeconds = plugin.getConfig().getInt("death-totem.cooldown-seconds", 300);
        deathTotemCooldowns.put(player.getUniqueId(), now + (cooldownSeconds * 1000L));

        // Restore health
        if (plugin.getConfig().getBoolean("death-totem.respawn-with-full-health")) {
            player.setHealth(player.getMaxHealth());
        } else {
            player.setHealth(4.0); // 2 hearts minimum
        }
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);

        // Explosion at death location
        if (plugin.getConfig().getBoolean("death-totem.explosion-on-death")) {
            float power = (float) plugin.getConfig().getDouble("death-totem.explosion-power", 3.0);
            boolean breaksBlocks = plugin.getConfig().getBoolean("death-totem.explosion-breaks-blocks", false);
            player.getWorld().createExplosion(player.getLocation(), power, true, breaksBlocks);
        }

        // Particles
        if (plugin.getConfig().getBoolean("particles.death-totem-explode")) {
            player.getWorld().spawnParticle(Particle.TOTEM, player.getLocation().add(0, 1, 0), 80, 0.5, 1, 0.5, 0.5);
            player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, player.getLocation().add(0, 1, 0), 40, 0.5, 1, 0.5, 0.1);
        }

        // Post-survive effects
        if (plugin.getConfig().getBoolean("death-totem.respawn-with-effects")) {
            int duration = plugin.getConfig().getInt("death-totem.effect-duration", 200);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, 1));
        }

        // Announce
        String msg = plugin.getConfig().getString("messages.death-totem-activate", "");
        if (!msg.isEmpty()) {
            Bukkit.broadcast(Component.text("§0§l[Death Totem] §r" + player.getName() + " " + msg.replace("§", "\u00a7")));
        }
    }
}
