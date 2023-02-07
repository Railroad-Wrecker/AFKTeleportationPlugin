package me.krusk.kruskafkteleportation;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AfkTeleportation extends JavaPlugin implements Listener {
    private HashMap<UUID, Long> lastActivityTime = new HashMap<>();
    private Location afkLocation;
    private final long AFK_THRESHOLD = 30 * 1000; // 30 seconds in milliseconds
    private final String AFK_COMMAND = "/afk";
    private final String SET_POINT_COMMAND = "/afktp setpoint";
    private final String CLEAR_POINT_COMMAND = "/afktp clearpoint";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    UUID id = p.getUniqueId();
                    if (!lastActivityTime.containsKey(id)) {
                        lastActivityTime.put(id, System.currentTimeMillis());
                        continue;
                    }
                    if (System.currentTimeMillis() - lastActivityTime.get(id) >= AFK_THRESHOLD) {
                        if (afkLocation != null) {
                            Location previousLocation = p.getLocation();
                            p.teleport(afkLocation);
                            lastActivityTime.put(id, System.currentTimeMillis());
                            p.setMetadata("previousLocation", new FixedLocationMetadataValue(previousLocation, this));
                        }
                    }
                }
            }
        }, 20, 20);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        String cmd = e.getMessage();
        if (cmd.equalsIgnoreCase(AFK_COMMAND)) {
            if (afkLocation != null) {
                Location previousLocation = p.getLocation();
                p.teleport(afkLocation);
                lastActivityTime.put(id, System.currentTimeMillis());
                p.setMetadata("previousLocation", new FixedLocationMetadataValue(previousLocation, (Runnable) this));
            }
        } else if (cmd.equalsIgnoreCase(SET_POINT_COMMAND)) {
            if (p.hasPermission("afkteleportation.setpoint")) {
                afkLocation = p.getLocation();
                p.sendMessage("AFK teleportation point set to your current location.");
            } else {
                p.sendMessage("You do not have permission to use this command.");
            }
        } else if (cmd.equalsIgnoreCase(CLEAR_POINT_COMMAND)) {
            if (p.hasPermission("afkteleportation.clearpoint")) {
                afkLocation = null;
                p.sendMessage("AFK teleportation point cleared.");
            } else {
                p.sendMessage("You do not have permission to use this command.");
            }
        }
        lastActivityTime.put(id, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        lastActivityTime.put(id, System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        lastActivityTime.remove(id);
    }
}
