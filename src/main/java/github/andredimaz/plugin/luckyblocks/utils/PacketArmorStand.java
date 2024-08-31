package github.andredimaz.plugin.luckyblocks.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.andredimaz.plugin.core.utils.basics.HologramUtils;
import github.andredimaz.plugin.core.utils.effects.ParticleEffect;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PacketArmorStand {

    private JavaPlugin plugin;
    private final Map<Integer, Location> armorStands = new HashMap<>();
    private BukkitRunnable rotationTask;

    public PacketArmorStand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void placeAnimation(Player player, Location location, org.bukkit.inventory.ItemStack head, double speed, double acceleration, int seconds, double upSpeed, String[] hologramLines) {
        int timer = seconds * 20;

        // Criação da animação principal (ArmorStand)
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle());
        armorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setSmall(false);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        this.addHelmet(player, armorStand, head);
        this.armorStands.put(armorStand.getId(), location);

        int[] hologramIds = HologramUtils.createMultiLineHologram(player, location.clone().add(0, 0.3, 0), hologramLines);

        playSound(player, Sound.CHEST_OPEN, location, 1f, 1f);

        // Iniciar animação de rotação e subida
        this.RotateArmorStand(armorStand, speed, acceleration);
        this.goUp(armorStand, upSpeed, hologramIds);
        this.playSound(player, Sound.NOTE_PLING, location, 0.5f, 0.5f);


        new BukkitRunnable() {
            @Override
            public void run() {
                removeArmorStand(armorStand.getId());
                HologramUtils.removeMultiLineHologram(player, hologramIds);
                explosionEffect(location, player);

                player.playSound(location, Sound.EXPLODE, 1f, 1f);

            }
        }.runTaskLater(plugin, timer);
    }

    public void removeArmorStand(int entityId) {
        PacketPlayOutEntityDestroy destroyPacket = new PacketPlayOutEntityDestroy(entityId);
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyPacket);
        }
        this.armorStands.remove(entityId);
    }

    private void addHelmet(Player player, EntityArmorStand armorStand, org.bukkit.inventory.ItemStack head) {
        ItemStack nmsHelmet = CraftItemStack.asNMSCopy(head);
        PacketPlayOutEntityEquipment equipmentPacket = new PacketPlayOutEntityEquipment(
                armorStand.getId(), 4, nmsHelmet);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(equipmentPacket);
    }

    private void RotateArmorStand(EntityArmorStand armorStand, double initialSpeed, double acceleration) {
        new BukkitRunnable() {
            private float yaw = 0.0f;
            private double speed = initialSpeed;

            @Override
            public void run() {
                if (!armorStand.isAlive()) {
                    this.cancel();
                    return;
                }

                yaw += speed;
                if (yaw >= 360.0f) {
                    yaw -= 360.0f;
                }

                armorStand.yaw = yaw;

                PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(
                        armorStand.getId(),
                        MathHelper.floor(armorStand.locX * 32.0D),
                        MathHelper.floor(armorStand.locY * 32.0D),
                        MathHelper.floor(armorStand.locZ * 32.0D),
                        (byte) ((yaw * 256.0F) / 360.0F),
                        (byte) ((armorStand.pitch * 256.0F) / 360.0F),
                        false
                );

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.sendPacket(teleportPacket);
                }

                // Incrementar a velocidade de rotação
                speed += acceleration;

            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void updateHologramPosition(int[] entityIds, Location location, double spacing) {
        for (int i = 0; i < entityIds.length; i++) {
            PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(
                    entityIds[i],
                    MathHelper.floor(location.getX() * 32.0D),
                    MathHelper.floor(location.getY() * 32.0D - (i * 0.3 * 32.0D)), // Adjust each line's Y position
                    MathHelper.floor(location.getZ() * 32.0D),
                    (byte) 0,
                    (byte) 0,
                    false
            );

            for (Player player : Bukkit.getOnlinePlayers()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(teleportPacket);
            }
        }
    }

    private void FloatArmorStand(EntityArmorStand armorStand) {
        new BukkitRunnable() {
            private double t = 0;  // Time counter
            private final double initialY = armorStand.locY;  // Initial Y position
            private final double amplitude = 0.3;  // Amplitude for the floating motion

            @Override
            public void run() {
                if (!armorStand.isAlive()) {
                    this.cancel();
                    return;
                }

                double yOffset = Math.sin(t) * amplitude;

                t += 0.15;

                armorStand.setPosition(armorStand.locX, initialY + yOffset, armorStand.locZ);

                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(
                        armorStand.getId(), armorStand.getDataWatcher(), true);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadataPacket);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);  // Run every tick (1L = 1 tick)
    }

    private void goUp(EntityArmorStand armorStand, double speed, int[] hologramIds) {
        new BukkitRunnable() {
            private double offsetY = 0;  // Y offset from the initial position
            private final double initialY = armorStand.locY;  // Initial Y position

            @Override
            public void run() {
                if (!armorStand.isAlive()) {
                    this.cancel();
                    return;
                }

                // Increment Y position by speed
                offsetY += speed;

                armorStand.setPosition(armorStand.locX, initialY + offsetY, armorStand.locZ);

                Location newLocation = armorStand.getBukkitEntity().getLocation();
                updateHologramPosition(hologramIds, newLocation.clone().add(0, 0.5, 0), 0.3);

                PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(
                        armorStand.getId(), armorStand.getDataWatcher(), true);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(metadataPacket);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // Runs every tick
    }

    private void playSound(Player player, Sound sound, Location location, float v1, float v2) {
        player.playSound(location, sound, v1, v2);
    }

    private void explosionEffect(Location location, Player player) {
        // Usando a sobrecarga com List<Player> para exibir as partículas apenas para certos jogadores
        ParticleEffect.EXPLOSION_HUGE.display(0f, -0.5f, 0f, 0.1f, 1, location, player);
    }
}

























