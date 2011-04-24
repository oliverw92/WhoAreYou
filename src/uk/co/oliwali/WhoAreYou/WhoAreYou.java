package uk.co.oliwali.WhoAreYou;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WhoAreYou extends JavaPlugin {
	
	public static String name;
	public static String version;
	private Permission permissions;
	private Config config;

	public void onDisable() {
		Util.sendMessage("info", "Version " + version + " disabled!");
	}

	public void onEnable() {
		name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        config = new Config(this);
        permissions = new Permission(this);
        Util.sendMessage("info", "Version " + version + " enabled!");
	}
	
	private void sendPlayerList(Player sender, String message, List<Player> players) {
		for (Player player : players.toArray(new Player[0]))
			message = message + " " + permissions.getPrefix(player) + player.getName();
		Util.sendMessage(sender, message);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		
		String prefix = cmd.getName();
		Player player = (Player) sender;
		
		if (prefix.equalsIgnoreCase("who")) {
			if (args.length > 0) {
				String name = args[0];
				if (permissions.world(player)) {
					for (World world : getServer().getWorlds().toArray(new World[0])) {
						if (name.equalsIgnoreCase(config.getAliasFromWorld(world))) {
							sendPlayerList(player, "&aPlayer list for &c" + world.getName() + " &7(" + world.getPlayers().size() + "/" + getServer().getMaxPlayers() + ")&a:&f", world.getPlayers());
							return true;
						}
					}
				}
				if (permissions.player(player)) {
					for (World world : getServer().getWorlds().toArray(new World[0])) {
						for (Player playerInfo : world.getPlayers().toArray(new Player[0])) {
							if (playerInfo.getName().equalsIgnoreCase(name)) {
								Location loc = playerInfo.getLocation();
								Util.sendMessage(player, "&aPlayer: &f" + playerInfo.getName());
								Util.sendMessage(player, "&aIP: &f" + playerInfo.getAddress().getAddress().getHostAddress().toString());
								Util.sendMessage(player, "&aLocation: &f" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
								Util.sendMessage(player, "&aWorld: &f" + config.getAliasFromWorld(playerInfo.getWorld()));
								Util.sendMessage(player, "&aHealth: &f" + playerInfo.getHealth() + "/20");
								Util.sendMessage(player, "&aOp: &f" + (player.isOp()?"yes":"no"));
								return true;
							}
						}
					}
				}
			}
			else if (permissions.list(player)) {
				List<Player> players = new ArrayList<Player>();
				for (World world : getServer().getWorlds().toArray(new World[0]))
					players.addAll(world.getPlayers());
				sendPlayerList(player, "&aServer player list &7(" + players.size() + "/" + getServer().getMaxPlayers() + ")&a:&f", players);
				return true;
			}
		}
		return false;
	}

}