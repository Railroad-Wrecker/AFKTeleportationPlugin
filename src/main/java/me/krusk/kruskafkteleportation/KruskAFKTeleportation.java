package me.krusk.kruskafkteleportation;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KruskAFKTeleportation extends JavaPlugin implements Listener {

    private Location afkLocation;
    private Map<Player, Location> previousLocations = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can execute this command");
            return false;
        }
        if (args.length != 1) {
            sender.sendMessage("Usage: /afkTP setpoint or /afkTP clearpoint");
            return false;
        }
        if (args[0].equalsIgnoreCase("setpoint")) {
            afkLocation = ((Player) sender).getLocation();
            sender.sendMessage("AFK teleport location set to: " + afkLocation);
        } else if (args[0].equalsIgnoreCase("clearpoint")) {
            afkLocation = null;
            sender.sendMessage("AFK teleport location cleared.");
        } else {
            sender.sendMessage("Usage: /afkTP setpoint or /afkTP clearpoint");
        }
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!previousLocations.containsKey(player)) {
            previousLocations.put(player, player.getLocation());
        }
        if (afkLocation != null) {
            double distance = event.getFrom().distance(event.getTo());
            if (distance > 0.1) {
                player.teleport(previousLocations.get(player));
                previousLocations.remove(player);
            } else {
                player.teleport(afkLocation);
            }
        }
    }
}