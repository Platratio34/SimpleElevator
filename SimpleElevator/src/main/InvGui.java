package main;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvGui implements Listener {
	
    private final Inventory inv;
    
    public InvGuiEvent event;
    public boolean stopPickup = true;
    public boolean exitOnEvent = true;
    
    public InvGui(InvGuiEvent event, String name, int size) {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        System.out.println(size);
    	size += size%9;
        if(size < 9) {
        	size = 9;
        }
    	inv = Bukkit.createInventory(null, size, name);
        this.event = event;
        // Put the items into the inventory
//        initializeItems();
    }
    
    public void addItem(Material m, String name) {
    	addItem(m,name,"");
    }
    public void addItem(Material m, String name, String... lore) {
    	inv.addItem(createGuiItem(m,name,lore));
    }
    // You can call this whenever you want to put the items in
//    public void initializeItems() {
//        inv.addItem(createGuiItem(Material.DIAMOND_SWORD, "Example Sword", "§aFirst line of the lore", "§bSecond line of the lore"));
//        inv.addItem(createGuiItem(Material.IRON_HELMET, "§bExample Helmet", "§aFirst line of the lore", "§bSecond line of the lore"));
//    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;
        
        if(stopPickup) {
        	e.setCancelled(true);
        }

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        event.onEvent(p, e, inv);
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
          e.setCancelled(true);
        }
    }
}