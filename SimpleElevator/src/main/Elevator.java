package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class Elevator {
	
	private Map<String, Floor> floors;
	private Plugin plugin;
	
	public Elevator(Plugin plugin) {
		floors = new HashMap<String, Floor>();
		this.plugin = plugin;
	}
	
	public Elevator(Plugin plugin, ConfigurationSection cS) {
		floors = new HashMap<String, Floor>();
		this.plugin = plugin;
		for(String key : cS.getKeys(false)) {
			if(!key.equals("overall")) {
				floors.put(key, new Floor(cS.getConfigurationSection(key)));
			}
		}
	}
	
	public boolean addFloor(String floor, float z) {
		if(!floors.containsKey(floor)) {
			floors.put(floor, new Floor(floor, z));
			return true;
		}
		return false;
	}
	
	public boolean setFloorName(String floor, String name) {
		if(floors.containsKey(floor)) {
			floors.get(floor).name = name;
			return true;
		}
		return false;
	}
	public boolean setFloorIcon(String floor, Material icon) {
		if(floors.containsKey(floor)) {
			floors.get(floor).icon = icon;
			return true;
		}
		return false;
	}
	public boolean setFloorY(String floor, float y) {
		if(floors.containsKey(floor)) {
			floors.get(floor).y = y;
			return true;
		}
		return false;
	}
	
	public String getFloorName(String floor) {
		if(floors.containsKey(floor)) {
			return floors.get(floor).name;
		} else {
			return "No Such Floor: '" + floor + "'";
		}
	}
	public Material getFloorIcon(String floor) {
		if(floors.containsKey(floor)) {
			return floors.get(floor).icon;
		} else {
			return Material.BARRIER;
		}
	}
	public float getFloorY(String floor) {
		if(floors.containsKey(floor)) {
			return floors.get(floor).y;
		} else {
			return 63;
		}
	}
	
	public Set<String> getFloors() {
		return floors.keySet();
	}
	
	private class Floor {
		
		public String name = "";
		public Material icon = Material.POLISHED_BLACKSTONE_BUTTON;
		public float y = 63;
		
		public Floor(String name, float y) {
			this.name = name;
			this.y = y;
		}

		public Floor(ConfigurationSection cS) {
			if(cS.contains("name")) {
				name = cS.getString("name");
			}
			if(cS.contains("icon")) {
				icon = Material.getMaterial(cS.getString("icon"));
			}
			if(cS.contains("y")) {
				y = (float)cS.getDouble("y");
			}
		}
		
		public ConfigurationSection save(ConfigurationSection cS) {
			cS.set("name", name);
			cS.set("icon", icon.toString());
			cS.set("y", y);
			return cS;
		}
		
	}

	public boolean hasFloor(String string) {
		return floors.containsKey(string);
	}

	public ConfigurationSection save(ConfigurationSection cS) {
		for(String floor : floors.keySet()) {
			cS.createSection(floor);
			floors.get(floor).save(cS.getConfigurationSection(floor));
		}
		return cS;
	}
}
