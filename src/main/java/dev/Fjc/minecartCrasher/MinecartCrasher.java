package dev.Fjc.minecartCrasher;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Random;

public final class MinecartCrasher extends JavaPlugin implements Listener {

    private final double x = 8;
    private final double y = 17;
    private final double xy = x * y;
    private static final Random random = new Random();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.AQUA + "Debug: Plugin startup successful.");
        this.saveDefaultConfig();
        this.getConfig().set("CrashTerminal.Velocity", 8);
        getConfig().options().copyDefaults(true);
        saveConfig();
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
        getLogger().info("Shutdown successful.");
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMinecartBlockCollision(VehicleBlockCollisionEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();
            World world = minecart.getWorld();
            Vector velocity = minecart.getVelocity();
            Player player = (Player) minecart.getPassengers();

            if (minecart.getVelocity().equals(this.getConfig().get("CrashTerminal.Velocity"))) {
                world.createExplosion(minecart.getLocation(), 20F, true, false);
                spawnExplosionRandomLocation(minecart.getLocation(), world, 4, 6);
                player.sendMessage(ChatColor.DARK_RED + "Your train terminated too fast and has exploded! Do better next time!");
            }


        }
    }

    @EventHandler
    public void onMinecartEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            RideableMinecart minecart = (RideableMinecart) event.getVehicle();
            World world = minecart.getWorld();
            Player player = (Player) minecart.getPassengers();

            if (minecart.getPassengers().contains(player)) {
                world.createExplosion(minecart.getLocation(), 12F, false, false);
                player.sendMessage(ChatColor.DARK_RED + "Your train collided with some other entity! Enjoy the chaos.");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMinecartDestoroy(VehicleDestroyEvent event, Player attacker) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();
            World world = minecart.getWorld();
            Player player = (Player) attacker;

            if (event.getVehicle() != null && event.getAttacker().equals(player)) {
                world.createExplosion(minecart.getLocation(), 16F, false, false);
                player.sendMessage(ChatColor.DARK_RED + "Damn, your train exploded on impact.");
            }
        }
    }

    private void spawnExplosionRandomLocation(Location location, World world, int amount, int r) {

        //r = radius, possible range factor of spawned explosion from initial blast
        for (int i = 0; i < amount; i++) {

            double offsetX = (random.nextDouble() * 2 - 1) * r;
            double offsetY = (random.nextDouble() * 2 - 1) * r;
            double offsetZ = (random.nextDouble() * 2 - 1) * r;

            Location randomLocation = location.clone().add(offsetX, offsetY, offsetZ);
            world.createExplosion(randomLocation, 15F, false, false);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("mcrasherreload")) {
            Player player = (Player) sender;
            if (!player.hasPermission("fjc.mcrasher.reload")) {
                player.sendMessage(ChatColor.DARK_PURPLE + "No permissions. Fool!");
                return true;
            } else {
                reloadConfig();
                player.sendMessage(ChatColor.DARK_GREEN + "Configuration reloaded");
                player.sendMessage(ChatColor.GREEN + "Crash velocity is now " + this.getConfig().get("CrashTerminal.Velocity"));
            }
        }
        return false;
    }
}
