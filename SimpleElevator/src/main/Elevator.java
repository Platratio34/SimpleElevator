package main;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class Elevator {
	
	private Map<String, Floor> floors;
	@SuppressWarnings("unused")
	private Plugin plugin;
	public int[] sFloors;
	
	public Elevator(Plugin plugin) {
		floors = new HashMap<String, Floor>();
		sFloors = new int[0];
		this.plugin = plugin;
	}
	
	public Elevator(Plugin plugin, ConfigurationSection cS) {
		floors = new HashMap<String, Floor>();
		sFloors = new int[0];
		if(cS.contains("sorted")) {
			Object[] oA = cS.getIntegerList("sorted").toArray();
			sFloors = new int[oA.length];
			System.arraycopy(oA, 0, sFloors, 0, oA.length);
		}
		this.plugin = plugin;
		for(String key : cS.getKeys(false)) {
			if(!key.equals("overall")) {
				if(sFloors.length == 0) {
					
				} else {
					floors.put(key, new Floor(cS.getConfigurationSection(key)));
				}
			}
		}
	}
	
	public boolean addFloor(String floor, float z) {
		return addFloor(floor, new Floor(floor, z));
	}
		
	public boolean addFloor(String floor, Floor f) {
		if(!floors.containsKey(floor)) {
			int fl = 0;
			try {
				fl = Integer.parseInt(floor);
			} catch(NumberFormatException e) {
				return false;
			}
			floors.put(floor, f);
			if(sFloors.length == 0) {
				sFloors = new int[] {fl};
			} else {
				int[] f2 = new int[sFloors.length + 1];
				boolean o = false;
				for(int i = 0; i < sFloors.length; i++) {
					if(sFloors[i] < fl) {
						f2[i]=sFloors[i];
					} else {
						if(!o) {
							f2[i] = fl;
							o=true;
						}
						f2[i+1]=sFloors[i];
					}
				}
				if(!o) {
					f2[f2.length-1] = fl;
				}
				sFloors = f2;
			}
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
		cS.set("sorted", sFloors);
		for(String floor : floors.keySet()) {
			cS.createSection(floor);
			floors.get(floor).save(cS.getConfigurationSection(floor));
		}
		return cS;
	}
}
