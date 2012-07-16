package com.github.Kraken3.AFKPGC;

import java.util.Map;
import java.util.Set;

class Kicker implements Runnable {
	public static int[] kickThresholds;
	public static Warning[] warnings;
	public static String message_on_kick;
	
	public void run() {	
		   if(!AFKPGC.enabled) return;		
		   int numberOfPlayersOnline = LastActivity.lastActivities.size();
		   if(numberOfPlayersOnline == 0) return;
		   if(numberOfPlayersOnline > kickThresholds.length) {			   
			   AFKPGC.logger.info("Error: Inconsistency in number of players detected");
			   return;
		   }
		   
		   int warningslen = warnings.length;
		   
		   //Current threshold of AFK time after which a player gets kicked
		   int threshold = kickThresholds[numberOfPlayersOnline-1]*1000;		   
		
		   LastActivity.currentTime = System.currentTimeMillis();
		   Map<Integer, LastActivity> lastActivities = LastActivity.lastActivities;				   
		   Set<Integer> keySet = lastActivities.keySet();
		   for(Integer i:keySet){				   
			   LastActivity la = lastActivities.get(i);			  
			   long time = LastActivity.currentTime - la.timeOfLastActivity;
			   long timeOld = la.timeOflastKickerPass - la.timeOfLastActivity;
			   la.timeOflastKickerPass = LastActivity.currentTime;
						   
			   if(timeOld < 0) continue;
			   
			   for(int j = 0; j < warningslen; j++){
				   int t = warnings[j].time;			   
				  				   
				   if(time >= threshold-t && timeOld < threshold-t) {					   
					   AFKPGC.plugin.getServer().getPlayer(la.playerName).sendMessage(warnings[j].message);
				   }
			   }			   
			   
			   
			   if(time > threshold) 
				   AFKPGC.plugin.getServer().getPlayer(la.playerName).kickPlayer(message_on_kick);
			   
		   }
	   }
	
}