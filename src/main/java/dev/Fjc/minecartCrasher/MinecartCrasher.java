package dev.Fjc.minecartCrasher;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
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
        this.getConfig().set("CrashTerminal.Velocity", 0.1);
        this.getConfig().set("CrashTerminal.RandomRadius", 6);
        this.getConfig().set("CrashTerminal.RandomStrength", 12F);
        this.getConfig().set("CrashTerminal.RandomAmount", 4);
        this.getConfig().set("initial-break-blocks", false);
        this.getConfig().set("random-break-blocks", true);
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable() {
        saveDefaultConfig();
        getLogger().info("Shutdown successful.");
    }

    //Main handler for block collision explosion
    @EventHandler(priority = EventPriority.NORMAL)
    public void onMinecartBlockCollision(VehicleBlockCollisionEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();
            World world = minecart.getWorld();

            //Maps the value of this velocity var to config.yml
            Vector velocityRand = (Vector) this.getConfig().get("CrashTerminal.Velocity");


            Player player = null;
            for (Entity passenger : minecart.getPassengers()) {
                if (passenger instanceof Player) {
                    player = (Player) passenger;
                    break;
                }
            }

            if (minecart.getVelocity() == velocityRand) {
                //Checks if the velocity of the vehicle is at a certain point defined by var velocityRand
                world.createExplosion(minecart.getLocation(), 20F, true, false);
                spawnExplosionRandomLocation(minecart.getLocation(), world, (Integer) this.getConfig().get("CrashTerminal.RandomAmount"), (Integer) this.getConfig().get("CrashTerminal.RandomRadius"));
                assert player != null;
                player.sendMessage(ChatColor.DARK_RED + "Your train terminated too fast and has exploded! Do better next time!");
                getLogger().info(ChatColor.YELLOW + "Debug: There was a block collision at " + player.getLocation());

            }


        }
    }

    @EventHandler
    public void onMinecartEntityCollision(VehicleEntityCollisionEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            RideableMinecart minecart = (RideableMinecart) event.getVehicle();
            World world = minecart.getWorld();

            Player player = null;
            for (Entity passenger : minecart.getPassengers()) {
                if (passenger instanceof Player) {
                    player = (Player) passenger;
                    break;
                }
            }

            if (player != null) {
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

            if (event.getAttacker() instanceof Player) {
                Player player = (Player) event.getAttacker();
                world.createExplosion(minecart.getLocation(), 16F, false, (Boolean) this.getConfig().get("initial-break-blocks"));
                player.sendMessage(ChatColor.DARK_RED + "Damn, your train exploded on impact.");
            }
        }
    }

    //This method handles the random explosion logic
    private void spawnExplosionRandomLocation(Location location, World world, int amount, int r) {

        //r = radius, possible range factor of spawned explosion from initial blast
        for (int i = 0; i < amount; i++) {

            double offsetX = (random.nextDouble() * 2 - 1) * r;
            double offsetY = (random.nextDouble() * 2 - 1) * r;
            double offsetZ = (random.nextDouble() * 2 - 1) * r;

            Location randomLocation = location.clone().add(offsetX, offsetY, offsetZ);
            world.createExplosion(randomLocation, 15F, false, (Boolean) this.getConfig().get("random-break-blocks"));
        }
    }

    //Reload command logic
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
