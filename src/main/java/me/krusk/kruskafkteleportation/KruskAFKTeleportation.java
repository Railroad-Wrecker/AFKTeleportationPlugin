package me.krusk.kruskafkteleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KruskAFKTeleportation extends JavaPlugin implements Listener {
    private Location afkLocation;
    private Map<UUID, Location> previousLocations = new HashMap<>();
    private Map<UUID, Long> lastMoveTimes = new HashMap<>();
    private final long AFK_TIMEOUT = 300000; // 5 minutes in milliseconds

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerId = player.getUniqueId();
                    Location previousLocation = previousLocations.get(playerId);

                    if (previousLocation == null) {
                        previousLocations.put(playerId, player.getLocation());
                        lastMoveTimes.put(playerId, System.currentTimeMillis());
                        continue;
                    }

                    if (!previousLocation.equals(player.getLocation())) {
                        previousLocations.put(playerId, player.getLocation());
                        lastMoveTimes.put(playerId, System.currentTimeMillis());
                        continue;
                    }

                    long lastMoveTime = lastMoveTimes.get(playerId);

                    if (System.currentTimeMillis() - lastMoveTime >= AFK_TIMEOUT) {
                        if (afkLocation != null) {
                            player.teleport(afkLocation);
                            player.sendMessage("You have been teleported because you are AFK.");
                        }
                    }
                }
            }
        }, 20L, 20L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("afkteleport.admin")) {
            player.sendMessage("You do not have permission to execute this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("Usage: /afktp setpoint or /afktp clearpoint");
            return true;
        }
        if (args[0].equalsIgnoreCase("setpoint")) {
            afkLocation = player.getLocation();
            player.sendMessage("AFK teleport point set to your current location.");
        } else if (args[0].equalsIgnoreCase("clearpoint")) {
            afkLocation = null;
            player.sendMessage("AFK teleport point cleared.");
        } else {
            player.sendMessage("Usage: /afktp setpoint or /afktp clearpoint");
        }

        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        previousLocations.put(playerId, player.getLocation());
        lastMoveTimes.put(playerId, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        previousLocations.remove(playerId);
        lastMoveTimes.remove(playerId);
    }
}
