package me.krusk.kruskafkteleportation;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class KruskAFKTeleportation extends JavaPlugin implements Listener {
    private Location afkLocation;
    private Map<Player, Location> playerLocations = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (afkLocation != null && (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
            playerLocations.put(player, event.getFrom());
            player.teleport(afkLocation);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("afkTP.admin")) {
            player.sendMessage("You do not have permission to use this command.");
            return false;
        }

        if (args.length != 1 || (!args[0].equals("setpoint") && !args[0].equals("clearpoint"))) {
            player.sendMessage("Usage: /afkTP setpoint or /afkTP clearpoint");
            return false;
        }

        if (args[0].equals("setpoint")) {
            afkLocation = player.getLocation();
            player.sendMessage("AFK location set to your current location.");
        } else {
            afkLocation = null;
            player.sendMessage("AFK location cleared.");
        }

        return true;
    }

    public void teleportBack(Player player) {
        Location previousLocation = playerLocations.get(player);
        if (previousLocation != null) {
            player.teleport(previousLocation);
            playerLocations.remove(player);
        }
    }
}