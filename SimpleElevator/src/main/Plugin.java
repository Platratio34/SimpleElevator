package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatColor;

public class Plugin extends JavaPlugin {
	
	public File dataFolder;
	public FileConfiguration config;
	public PluginDescriptionFile pdf;
	
	public Logger log;
	private PrintStream log2;
	
	public Map<String, Elevator> eleavtors;
	
	public ElevatorCommands com;

	@Override
	public void onEnable() {
		dataFolder = getDataFolder();
		log = super.getLogger();
		try {
			log2 = new PrintStream(new File(dataFolder + "log.log"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		pdf = this.getDescription();
		reloadConfigFile();
		if(pdf.getVersion().contains("-Dev")) {
			log.warning("This is a dev build, things may not work properly!");
		}
		
//		eleavtors = new HashMap<String, Elevator>();
		
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new ElevatorEvent(this), this);
		com = new ElevatorCommands(this);
		getCommand("elevator").setExecutor(com);
		getCommand("elevator").setTabCompleter(new ElevatorCompleter(this));
	}
	
	@Override
	public void onDisable() {
		saveElevators();
		saveConfig();
		log2.print(config);
		log2.close();
	}
	
	private void saveElevators() {
		if(config == null) {
			saveDefaultConfig();
			config = getConfig();
		}
		ConfigurationSection cS = config.createSection("elevators");
		for(String key : eleavtors.keySet()) {
			cS.createSection(key);
			eleavtors.get(key).save(cS.getConfigurationSection(key));
		}
		saveConfig();
	}

	public void log(String msg) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		log2.println(dtf.format(now) + ": " + msg);  
	}
	
	public void reloadConfigFile() {
		try {
			config = getConfig();
			if(config == null) {
				saveDefaultConfig();
				config = getConfig();
			}
			
//			try {
//				elvConfig = YamlConfiguration.loadConfiguration(new File(dataFolder, "elevatorConfig.yml"));
//			} catch (Exception e){
//				log.info("Elevator config doesn't exist, one will be created");
//				try {
//					Reader defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8");
//				    if (defConfigStream != null) {
//				        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
//				        elvConfig.setDefaults(defConfig);
//				    }
//				} catch (UnsupportedEncodingException e) {
//					log.warning("Failed to load default config");
//				}
//				elvConfig = new YamlConfiguration();
//				elvConfig.save(new File(dataFolder, "elevatorConfig.yml"));
//			}
			
//			worldConfigFile = new File(dataFolder, worldName + ".yml");
//			if(worldConfigFile.exists()) {
//				config = YamlConfiguration.loadConfiguration(worldConfigFile);
//				enableSave = true;
//			} else {
//				log.info("Config for world " + worldName + " didn't exist, if enbaled, one will be created on plugin enable");
//			}
			
			try {
				Reader defConfigStream = new InputStreamReader(this.getResource("config.yml"), "UTF8");
			    if (defConfigStream != null) {
			        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			        config.setDefaults(defConfig);
			    }
			} catch (UnsupportedEncodingException e) {
				log.warning("Failed to load default config");
			}
			saveConfig();
		} catch (Exception e) {
			File f = new File(dataFolder, "MainErrorLog.log");
			PrintStream ps;
			try {
				ps = new PrintStream(f);
				e.printStackTrace(ps);
				log.warning("Error reloading config");
				ps.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		eleavtors = new HashMap<String, Elevator>();
		if(config.contains("elevators")) {
			ConfigurationSection eCS = config.getConfigurationSection("elevators");
			for(String key : eCS.getKeys(false)) {
				eleavtors.put(key, new Elevator(this, eCS.getConfigurationSection(key)));
			}
		}
		
	}

	public void elevatorUse(Player p, String id) {
		if(!p.hasPermission("elevators.use")) {
			p.sendMessage(ChatColor.RED + "You do not have permession to use elevators");
			return;
		}
		if(eleavtors.containsKey(id)) {
			Elevator elv = eleavtors.get(id);
			InvGui gui = new InvGui(new InvGuiEvent() {
				@Override
				public void onEvent(Player p, InventoryClickEvent e, Inventory inv) {
					int slot = e.getSlot();
					String floor = inv.getItem(slot).getItemMeta().getLore().get(0);
					Location loc = p.getLocation();
					loc.setY(elv.getFloorY(floor));
					if(p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).getType().isOccluding()) {
						p.sendMessage("Floor obstructed"); return;}
					if(p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY()+1, loc.getBlockZ()).getType().isOccluding()) {
						p.sendMessage("Floor obstructed"); return;}
					if(!p.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ()).getType().isSolid()) {
						p.sendMessage("No Floor block"); return;}
					p.teleport(loc);
					p.sendMessage("Now on floor " + floor + " | " + elv.getFloorName(floor));
				}
			},"Elevator", elv.getFloors().size());
			for(int i = 0; i < elv.sFloors.length; i++) {
				String floor = elv.sFloors[i] + "";
				gui.addItem(elv.getFloorIcon(floor), elv.getFloorName(floor), floor);
			}
			PluginManager pm = Bukkit.getServer().getPluginManager();
			pm.registerEvents(gui, this);
			gui.openInventory(p);
		} else {
			log.warning("Somthing went wrong attempting to use an elevator, veiw plugin error log for details");
			File f = new File(dataFolder, "ElevatorErrorLog_" + getDT() + ".log");
			PrintStream ps;
			try {
				ps = new PrintStream(f);
				ps.println(getDT());
				ps.println("Error finding elevator");
				ps.println("Id: '" + id + "'");
				ps.println("Elevators: ");
				for(String key : eleavtors.keySet()) {
					ps.print(key + " | ");
				}
				ps.println();
				ps.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public String getDT() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
		return dtf.format(LocalDateTime.now());
	}

	public Elevator getElevator(String id) {
		if(eleavtors.containsKey(id)) {
			return eleavtors.get(id);
		}
		return null;
	}
}
