package dev.Fjc.minecartCrasher.var;


import org.bukkit.entity.Minecart;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public abstract class BoundBox extends Event implements Listener {
//I might use this later
    @EventHandler
    public void setBoundBox(VehicleMoveEvent event) {
        if (event.getVehicle() instanceof Minecart) {
            Minecart minecart = (Minecart) event.getVehicle();

            Vector minecartPos = minecart.getLocation().toVector();

            double width = 3;
            double length = 12;
            double height = 5;

            BoundingBox boundingBox = new BoundingBox(

                    minecartPos.getX() - width/2,
                    minecartPos.getY() + height - height,
                    minecartPos.getZ() - length/2,
                    minecartPos.getX() + width/2,
                    minecartPos.getY() + height,
                    minecartPos.getZ() + length/2
            );
        }
    }
}