package com.github.Kraken3.AFKPGC;

import java.util.logging.Logger;
import java.util.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AFKPGC extends JavaPlugin {	
	public static Logger logger;
	public static JavaPlugin plugin;
	public static boolean enabled = true;
	
	@Override
	public void onEnable(){ 
		//setting a couple of static fields so that they are available elsewhere
		logger = getLogger();
		plugin = this;
		
		//Reads Config.yml - false as an answer indicated unrecoverable error
		AFKPGC.enabled = ConfigurationReader.readConfig();
		if(!AFKPGC.enabled) Message.error(0);
		
		getServer().getPluginManager().registerEvents(new EventHandlers(), this);				

		//Checks whether to 'garbage collect' AFKers every 20 ticks (1 seconds);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Kicker(), 0, 20L);		
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			   public void run() {		
				   LastActivity.currentTime = System.currentTimeMillis();				 
			   }
		}, 0, 1L);		
		
		Message.send(1);
	}
	 
	@Override
	public void onDisable(){ 
		Message.send(2);	 
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) player = (Player) sender;
		if(args.length == 0) return false;
	 
		if (cmd.getName().equalsIgnoreCase("afkpgc")){ 
			if(args[0].equalsIgnoreCase("times")){
				return onCommandTimes(player);						
			} else if(args[0].equalsIgnoreCase("amistillalive")){
				if(player != null) Kicker.amIStillAlivePlayer = player.getName();
				return true;
			} else if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("list") ||  args[0].equalsIgnoreCase("stop") ){
				if(player != null && !player.isOp()) { 
					player.sendMessage("Only OP can do that. ");
					return true;
				}
				
				if(args[0].equalsIgnoreCase("reload"))    return onCommandReload(player);				
				else if(args[0].equalsIgnoreCase("list")) return onCommandList(player, args);					
				else if(args[0].equalsIgnoreCase("stop")) return onCommandStop(player);	
				
				
			} else return false;
			
		} 
		return false;
	}
	
	
	private boolean onCommandTimes(Player player){
		int[] kt = Kicker.kickThresholds;
		int ktlen = kt.length;
		int starti = -1;
		int stopi = -1;
		int val = -1;
		for(int i = 0; i < ktlen; i++){
			if(val == kt[i]) {
				stopi = i;
			} else {						
				if(starti != -1) sendRangeMessage(player, starti, stopi,val);
				starti = i;
				stopi = i;					
			}
			val = kt[i];
		}				
		sendRangeMessage(player, starti, stopi,val);				
		return true;		
	}
	
	private void sendRangeMessage(Player player, int start, int stop, int val){
		if(val != 0){
			if(start == stop) Message.send(player, 3, (start+1), readableTimeSpan(val));			
			else Message.send(player, 4, (start+1), (stop+1), readableTimeSpan(val));
		}
	}
	
	
	private boolean onCommandReload(Player player){
		AFKPGC.enabled = ConfigurationReader.readConfig();
		if(AFKPGC.enabled) Message.send(player, 5);	
		else Message.send(player, 6);	
		return true;
	}
	
	
	private boolean onCommandStop(Player player){
		Message.send(player, 7);
		AFKPGC.enabled = false;
		return true;
	}
	
	
	private boolean onCommandList(Player player, String[] args){
		int p = 10;
		if(args.length == 2){		
			try{
				p = Integer.parseInt(args[1]);				
			} catch (Exception e){}
		}
		if(p < 0) p = 10;
		Message.send(player, 8, p);
		
		ArrayList<LastActivity> las = new ArrayList<LastActivity>();
		Set<String> set = LastActivity.lastActivities.keySet();
		for(String i:set) las.add(LastActivity.lastActivities.get(i));	
		int laslen = las.size();
		Collections.sort(las, new Comparator<LastActivity>(){						
			public int compare(LastActivity arg0, LastActivity arg1) {							
				return (int)(arg0.timeOfLastActivity - arg1.timeOfLastActivity);
			}					
		});
		for(int i = 0; i < laslen; i++){
			if(i == p) break; 						
			int t = (int)((LastActivity.currentTime - las.get(i).timeOfLastActivity)/1000);			
			Message.send(player, 9, las.get(i).playerName, readableTimeSpan(t));
		}
		return true;
	}

	
	public static String readableTimeSpan(int t){		
		String b = "";		
		int s = t % 60;
		t /= 60;		
		int m = t % 60;
		t /= 60;		
		int h = t % 24;
		t /= 24;		
		int d = t;		
		if(d != 0)                     b += d + "d ";
		if(d != 0 || h != 0)           b += h + "h ";
		if(d != 0 || h != 0 || m != 0) b += m + "m ";
		                               b += s + "s ";	
		return b;		
	}
	
	
	public static void removerPlayer(String name){
		if(LastActivity.lastActivities.containsKey(name))LastActivity.lastActivities.remove(name);	
	}
	
	public static void addPlayer(String name){
		if(name == null) return;
		
		LastActivity la;		
		if(LastActivity.lastActivities.containsKey(name)){
			la = LastActivity.lastActivities.get(name);			
		} else {
			la = new LastActivity();
			LastActivity.lastActivities.put(name,la);			
		}				
		
		la.timeOfLastActivity = System.currentTimeMillis();
		la.timeOflastKickerPass = System.currentTimeMillis();
		la.playerName = name;
	}
	

}

class Warning{
	public int time;	
	public String message;	
	public Warning (int time, String message){ 
		this.time = time;
		this.message = message;
	}
}



class LastActivity{
	public static Map<String, LastActivity> lastActivities = new TreeMap<String, LastActivity>();
	public static long currentTime; 	//OCD compels me to save a few System.currentTimeMillis() calls..	
	public long timeOfLastActivity;
	public long timeOflastKickerPass; //time of the last Kicker.run call, relevant for warnings
	public String playerName; //useful only in onCommandList
}