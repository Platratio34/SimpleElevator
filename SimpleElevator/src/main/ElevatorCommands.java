package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;

public class ElevatorCommands implements CommandExecutor {

	private Plugin plugin;
	
	private Map<Player, Elevator> sessions;
	private Elevator consSession;
	
	public ElevatorCommands(Plugin plugin) {	
		this.plugin = plugin;
		sessions = new HashMap<Player, Elevator>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command com, String lable, String[] args) {
		Player p = null;
		boolean pMake = true;
		boolean pEdit = true;
		if(sender instanceof Player) {
			p = (Player)sender;
			pMake = p.hasPermission("elevator.make");
			pEdit = p.hasPermission("elevator.edit");
		}
		try {
			if(args.length == 0) {
				sender.sendMessage("You need an argument");
			} else {
				if((p == null && consSession != null) || sessions.containsKey(p)) {
					if(!pEdit) {
						sender.sendMessage(ChatColor.RED + "You do not have permesion to edit elevators");
						sessions.remove(p);
						return false;
					}
					Elevator e = null;
					if(p == null) {
						e = consSession;
					} else {
						e = sessions.get(p);
					}
					if(args.length < 1) {
						sender.sendMessage(ChatColor.RED + "Not enough arguments");
						return false;
					}
					if(args[0].equals("overall")) {
						// TODO implement Elevator overall options
						sender.sendMessage(ChatColor.YELLOW + "This command has not been implemted yet");
						return false;
					} else if(e.hasFloor(args[0])) {
						if(args.length >= 3) {
							if(args[1].equals("y")) {
								try {
									e.setFloorY(args[0], Float.parseFloat(args[2]));
									sender.sendMessage("Floor: " + args[0] + " y level set to " + args[2]);
									return true;
								} catch (NumberFormatException e2) {
									sender.sendMessage(ChatColor.RED + "Argmuent must be a number");
									return false;
								}
							} else if(args[1].equals("name")) {
								String name = "";
								for(int i = 2 ; i < args.length; i++) {
									name += args[i] + ((i+1<args.length)?" ":"");
								}
								e.setFloorName(args[0], name);
								sender.sendMessage("Floor: " + args[0] + " name set to " + e.getFloorName(args[0]));
								return true;
							} else if(args[1].equals("icon")) {
								Material m = Material.getMaterial(args[2]);
								if(m == null) {
									sender.sendMessage("Invalid material for icon");
									return false;
								}
								e.setFloorIcon(args[0], m);
								sender.sendMessage("Floor: " + args[0] + " icon set to " + args[2]);
								return true;
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid parameter");
								return false;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Invalid number of arguments");
							return false;
						}
					} else if(args[0].equals("exit")) {
						if(p == null) {
							consSession = null;
						} else {
							sessions.remove(p);
						}
						sender.sendMessage("Left edit session");
						return true;
					} else {
						if(args.length == 2) {
							try {
								int f = Integer.parseInt(args[0]);
							} catch (NumberFormatException n) {
								sender.sendMessage(ChatColor.RED + "Floor number must be a number");
								return false;
							}
							if(args[1].equals("add")) {
								e.addFloor(args[0], p!=null?(float)p.getLocation().getY():63);
								sender.sendMessage("Floor " + args[0] + " added at current height");
								return true;
							}
						}
						sender.sendMessage(ChatColor.RED + "Invalid floor");
						return false;
					}
				}
					if(args[0].equals("edit")) {
						if((p == null && consSession != null) || sessions.containsKey(p)) {
							if(p == null) {
								consSession = null;
							} else {
								sessions.remove(p);
							}
							sender.sendMessage("Left edit session");
							return true;
						}
						if(!pEdit) {
							sender.sendMessage(ChatColor.RED + "You do not have permesion to edit elevators");
							return false;
						}
						if(args.length == 2) {
							Elevator e = plugin.getElevator(args[1]);
							if(e == null) {
								sender.sendMessage(ChatColor.RED + "No elevator by that name. Use '/" + lable + " list' for a list of elevaotors");
								return false;
							}
							if(p != null) {
								sessions.put(p, e);
							} else {
								consSession = e;
							}
							sender.sendMessage("Started edit session for elevator " + args[1]);
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "No elevator name");
							return false;
						}
					} else if(args[0].equals("list")) {
						if(!(pEdit || pMake)) {
							sender.sendMessage(ChatColor.RED + "You do not have permesion to list elevators");
							return false;
						}
						String out = "Elevators: ";
						for(String key : plugin.eleavtors.keySet()) {
							out += key + " | ";
						}
						sender.sendMessage(out);
						return true;
					} else if(args[0].equals("make")) {
						if(pMake) {
							if(args.length != 2) {
								sender.sendMessage(ChatColor.RED + "Invalid number of arguments");
								return false;
							}
							if(plugin.eleavtors.containsKey(args[1])) {
								Block b = p.getTargetBlock(null, 5);
								if(ElevatorEvent.isSign(b.getType())) {
									Sign sign = (Sign)b.getState();
					                PersistentDataContainer cont =  sign.getPersistentDataContainer();
					                cont.set(ElevatorEvent.key, PersistentDataType.STRING, args[1]);
					                sign.update();
					                System.out.println(cont.getKeys());
					                sender.sendMessage("Sign set as elevator controller for elevator: '" + args[1] + "'");
					                return true;
								} else {
									sender.sendMessage(ChatColor.YELLOW + "You must be looking at a sign");
									return false;
								}
							} else {
								sender.sendMessage(ChatColor.RED + "Invalid elevator");
								return false;
							}
						} else {
							sender.sendMessage(ChatColor.RED + "You do not have permission to make elevator signs");
							return false;
						}
					} else if(args[0].equals("add")) {
						if(!pEdit) {
							sender.sendMessage(ChatColor.RED + "You do not have permision to make elevators");
							return false;
						}
						if(plugin.eleavtors.containsKey(args[1])) {
							sender.sendMessage(ChatColor.RED + "Elevator already exeists");
							return false;
						}
						plugin.eleavtors.put(args[1], new Elevator(plugin));
						sender.sendMessage("Added elevator " + args[1]);
						return true;
					} else if(args[0].equals("help")) {
						if(!(pEdit || pMake)) {
							sender.sendMessage(ChatColor.RED + "You do not have permesion to use elevator commands");
							return false;
						}
						sender.sendMessage(ChatColor.GREEN + "Showing help for Simple Elevators");
						if(pEdit) {
							sender.sendMessage("To make a new elevator run: " + ChatColor.DARK_BLUE + "/elev add <name>");
							sender.sendMessage("To start and edit session run: " + ChatColor.DARK_BLUE +"/elev edit <name>");
							sender.sendMessage("While in an edit session:");
							sender.sendMessage("\tTo add a new floor run: " + ChatColor.DARK_BLUE + "/elev <floor> add");
							sender.sendMessage("\tTo set the y level of a floor run: " + ChatColor.DARK_BLUE + "/elev <floor> y <level>");
							sender.sendMessage("\tTo set the name of a floor run: " + ChatColor.DARK_BLUE + "/elev <floor> name <name>");
							sender.sendMessage("\tTo set the icon for a floor run: " + ChatColor.DARK_BLUE + "/elev <floor> icon <icon>");
							sender.sendMessage("\tTo exit an edit session run: " + ChatColor.DARK_BLUE + "/elev exit");
						}
						if(pMake) {
							sender.sendMessage("To set a sign as a controller for an elevator run: " + ChatColor.DARK_BLUE + "/elev make <elevator>");
							sender.sendMessage("To unset a sign as a controller break it");
						}
						sender.sendMessage("To veiw a list of all elevators run: " + ChatColor.DARK_BLUE + "/elev list");
						return true;
					} else {
						sender.sendMessage(ChatColor.RED + "Invalid first argument");
						return false;
					}
			}
			
			
			sender.sendMessage(ChatColor.RED + "Somthing whent wrong executing the command");
			return false;
		} catch (Exception e) {
			File f = new File(plugin.dataFolder, "CommandErrorLog.log");
			PrintStream ps;
			try {
				ps = new PrintStream(f);
				ps.println("Error execuing command /" + lable + " " + printArgs(args) + "'");
				e.printStackTrace(ps);
				ps.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			sender.sendMessage(ChatColor.RED + "Somthing whent wrong executing the command. Error :" + e.getCause());
			plugin.log.warning("Somthing whent wrong executing a command. Error :" + e.getCause() + "; Command '/" + lable + " " + printArgs(args) + "'; Veiw plugin command error log for more information");
			return false;
		}
	}
	
	private String printArgs(String[] args) {
		String o = "";
		for(int i = 0; i < args.length; i++) {
			o += args[i];
			o += (i < args.length-1)?" ":"";
		}
		return o;
	}
	
	public boolean hasSession(Player p) {
		if(p == null) {
			return consSession != null;
		} else {
			return sessions.containsKey(p);
		}
	}
	public Elevator getSession(Player p) {
		if(p == null) {
			return consSession;
		} else {
			return sessions.get(p);
		}
	}
}
