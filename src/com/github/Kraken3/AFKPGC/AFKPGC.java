package com.github.Kraken3.AFKPGC;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
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
		if(!ConfigurationReader.readConfig()) return;
		
		getServer().getPluginManager().registerEvents(new EventHandlers(), this);				

		//Checks whether to 'garbage collect' AFKers every 20 ticks (1 seconds);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Kicker(), 0, 20L);		
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			   public void run() {		
				   LastActivity.currentTime = System.currentTimeMillis();				 
			   }
		}, 0, 1L);
		
		
		logger.info("AFK Player Garbage Collector has been enabled.");
	}
	 
	@Override
	public void onDisable(){ 
		getLogger().info("AFK Player Garbage Collector has been disabled.");	 
	}
	
	public static void ErrorMessage(String str){
		logger.log(Level.SEVERE, str);
		plugin.getServer().getPluginManager().disablePlugin(plugin);
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
	public static Map<Integer, LastActivity> lastActivities = new HashMap<Integer, LastActivity>();
	public static long currentTime; 	//OCD compels me to save a few System.currentTimeMillis() calls..
	public String playerName;	
	public long timeOfLastActivity;
	public long timeOflastKickerPass; //time of the last Kicker.run call, relevant for warnings
}