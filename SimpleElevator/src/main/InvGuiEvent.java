package main;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InvGuiEvent {
	public void onEvent(Player p, InventoryClickEvent e, Inventory inv) {
        p.sendMessage("You clicked at slot " + e.getRawSlot());
	}
}
