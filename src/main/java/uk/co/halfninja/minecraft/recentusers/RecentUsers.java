package uk.co.halfninja.minecraft.recentusers;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.*;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
import java.text.*;
import java.util.Date;

public class RecentUsers extends JavaPlugin implements Listener {
	public static final String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	private Map<String, Date> lastConnected = new HashMap<String,Date>();
	
	private int max;
	private boolean includeSelf;

	public RecentUsers() {}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		max = getConfig().getInt("recentusers.maxitems");
		includeSelf = getConfig().getBoolean("recentusers.includeself");
		getLogger().info("Magic pants active.");
	}

	public void update(Player player) {
		lastConnected.put(player.getName(), new Date());
	}

	@EventHandler public void playerJoined(PlayerLoginEvent event) {
		update(event.getPlayer());
	}

	@EventHandler public void playerLeft(PlayerQuitEvent event) {
		update(event.getPlayer());
	}

	@EventHandler public void playerKicked(PlayerKickEvent event) {
		update(event.getPlayer());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		boolean perms = true;
		if (sender instanceof Player) {
			perms = ((Player)sender).hasPermission("recentusers.list");
		}
		if (cmd.getName().equalsIgnoreCase("recentusers") && perms) {
			String[] messages = toStrings(getRecentItems(sender.getName())).toArray(new String[0]);
			if (messages.length == 0) 
				sender.sendMessage("Nobody has been on recently.");
			else
				sender.sendMessage(messages);
			return true;
		}
		return false; 
	}

	public List<String> toStrings(List<RecentItem> items) {
		List<String> item = new ArrayList<String>();
		int count = 0;
		for (RecentItem i : items) {
			item.add(i.toString());
			count++;
			if (count == max) break;
		}
		return item;
	}

	public List<RecentItem> getRecentItems(String me) {
		List<RecentItem> item = new ArrayList<RecentItem>();
		for (String name : lastConnected.keySet()) {
			if (includeSelf || !me.equals(name)) {
				item.add(new RecentItem(name, lastConnected.get(name)));
			}
		}
		java.util.Collections.sort(item, new Comparator<RecentItem>() {
			public int compare(RecentItem a, RecentItem b) {
				return b.when.compareTo(a.when); // Sort descending
			}
		});
		return item;
	}

	public static class RecentItem {
		public SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
		public String name;
		public Date when;
		public RecentItem(String n, Date d) {
			name = n;
			when = d;
		}
		public String toString() {
			return name + ": " + format.format(when);
		}
	}

}
