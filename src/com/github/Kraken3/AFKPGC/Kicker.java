package com.github.Kraken3.AFKPGC;

import java.util.Map;

import org.bukkit.entity.Player;


class Kicker implements Runnable {
	public static int[] kickThresholds;
	public static Warning[] warnings;
	public static String message_on_kick;
	public static String amIStillAlivePlayer;  // sends a message to this player
	
	public void run() {				
			if(amIStillAlivePlayer != null){			
					Player p;
					if((p = AFKPGC.plugin.getServer().getPlayer(amIStillAlivePlayer)) != null){
						p.sendMessage("AFKPGC plugin is still alive");						
					}	
					amIStillAlivePlayer = null;
			}		   
		
		   if(!AFKPGC.enabled) return;			   
		   
		   int numberOfPlayersOnline = LastActivity.lastActivities.size();
		   if(numberOfPlayersOnline == 0) return;
		   if(numberOfPlayersOnline > kickThresholds.length) {			   
			   Message.send(13);
			   return;
		   }
		   
		   int warningslen = warnings.length;
		   
		   //Current threshold of AFK time after which a player gets kicked
		   int threshold = kickThresholds[numberOfPlayersOnline-1]*1000;
		   if(threshold == 0) return;
		
		   LastActivity.currentTime = System.currentTimeMillis();
		   Map<String, LastActivity> lastActivities = LastActivity.lastActivities;				   
		   String[] keySet = lastActivities.keySet().toArray(new String[0]);		   
		   for(String i:keySet){
			   if(!lastActivities.containsKey(i)) continue;
			   LastActivity la = lastActivities.get(i);			  
			   long time = LastActivity.currentTime - la.timeOfLastActivity;
			   long timeOld = la.timeOflastKickerPass - la.timeOfLastActivity;
			   la.timeOflastKickerPass = LastActivity.currentTime;
						   
			   if(timeOld < 0) continue;
			   
			   for(int j = 0; j < warningslen; j++){
				   int t = warnings[j].time;			   
				  				   
				   if(time >= threshold-t && timeOld < threshold-t) {	
					   Player p;
					   if((p = AFKPGC.plugin.getServer().getPlayer(i)) != null)	 p.sendMessage(warnings[j].message);
				   }
			   }	
			   
			   if(time > threshold){ 
				   Player p;
				   if((p = AFKPGC.plugin.getServer().getPlayer(i)) != null){
					   //kicking a player
					   p.kickPlayer(message_on_kick);					   
					   AFKPGC.removerPlayer(i);						   
					   int t = (int)((LastActivity.currentTime - la.timeOfLastActivity)/1000);
					   Message.send(14, i, AFKPGC.readableTimeSpan(t)); 
				   }	
			   }
			   
		   }
	   }
	
}