package main;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ElevatorEvent implements Listener {

	private Plugin plugin;
	public static NamespacedKey key;
	
	public ElevatorEvent(Plugin plugin) {
		this.plugin = plugin;
		key = new NamespacedKey(plugin, "elevatorSign");
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
        Player p = event.getPlayer();
//        System.out.println("event");
        if(isSign(event.getClickedBlock().getType())){
//        	System.out.println("event is sign");
            if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
//            	System.out.println("event is right click");
                Sign sign = (Sign) event.getClickedBlock().getState();
                PersistentDataContainer cont =  sign.getPersistentDataContainer();
                if(cont.has(key, PersistentDataType.STRING)) {
                	System.out.println("sign is controller");
                	String id = cont.get(key, PersistentDataType.STRING);
                	plugin.elevatorUse(p, id);
                } else {
                	System.out.println(cont.getKeys());
                }
            }
        }
	}
	
	public static boolean isSign(Material m) {
		if(m == Material.ACACIA_SIGN || m == Material.ACACIA_WALL_SIGN) {
			return true;
		} else if(m == Material.OAK_SIGN || m == Material.OAK_WALL_SIGN) {
			return true;
		} else if(m == Material.DARK_OAK_SIGN || m == Material.DARK_OAK_WALL_SIGN) {
			return true;
		} else if(m == Material.SPRUCE_SIGN || m == Material.SPRUCE_WALL_SIGN) {
			return true;
		} else if(m == Material.JUNGLE_SIGN || m == Material.JUNGLE_WALL_SIGN) {
			return true;
		} else if(m == Material.WARPED_SIGN || m == Material.WARPED_WALL_SIGN) {
			return true;
		} else if(m == Material.CRIMSON_SIGN || m == Material.CRIMSON_WALL_SIGN) {
			return true;
		} else if(m == Material.BIRCH_SIGN || m == Material.BIRCH_WALL_SIGN) {
			return true;
		}
		return false;
	}
	
}
