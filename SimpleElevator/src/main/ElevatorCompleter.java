package main;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ElevatorCompleter implements org.bukkit.command.TabCompleter {
	
private Plugin plugin;
	
	public ElevatorCompleter(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command com, String lable, String[] args) {
		List<String> l = new ArrayList<String>();
		Player p = null;
		boolean pMake = true;
		boolean pEdit = true;
		if(sender instanceof Player) {
			p = (Player)sender;
			pMake = p.hasPermission("elevator.make");
			pEdit = p.hasPermission("elevator.edit");
		}
		if(!(pMake || pEdit)) {
			return l;
		}
		boolean s = plugin.com.hasSession(p);
		if(pEdit) {
			if(s) {
				Elevator e = plugin.com.getSession(p);
				if(args.length == 1) {
					l.add("overall");
					l.add("exit");
					for(String key : e.getFloors()) {
						l.add(key);
					}
				} else if(args.length == 2) {
					if(args[0].equals("overall")) {
						l.add("");
					} else if(e.hasFloor(args[0])) {
						l.add("y");
						l.add("name");
						l.add("icon");
					} else {
						l.add("add");
					}
				} else if(args.length == 3) {
					if(args[1].equals("icon")) {
						for(Material m : Material.values()) {
							l.add(""+m);
						}
					}
				}
			} else {
				if(args.length == 1) {
					l.add("edit");
					l.add("add");
					l.add("list");
					l.add("help");
				} else if(args.length == 2) {
					if(args[0].equals("edit")) {
						for(String key : plugin.eleavtors.keySet()) {
							l.add(key);
						}
					}
				}
			}
		}
		if(!s && pMake) {
			if(args.length == 1) {
				l.add("make");
				if(!pEdit) {
					l.add("list");
					l.add("help");
				}
			} else if(args.length == 2) {
				if(args[0].equals("make")) {
					for(String key : plugin.eleavtors.keySet()) {
						l.add(key);
					}
				}
			}
		}
		return fix(l, args[args.length-1]);
	}
	
	private static List<String> fix(List<String> in, String str) {
		List<String> l = new ArrayList<String>();
		if(in.size() > 0) {
			for(int i = 0; i < in.size(); i++) {
				if(in.get(i) != null) {
					if(in.get(i).substring(0,Math.min(str.length(), in.get(i).length())).equals(str)) {
						l.add(in.get(i));
					}
				}
			}
		} else {
			return l;
		}
		if(l.size()==0) {
			return l;
		}
		return l;
	}
}
