package com.github.Kraken3.AFKPGC;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationReader {
	public static boolean readConfig(){		
		
		//TODO: Change so that the plugin copies jar internal default config.yml into FS only when one doesn't exist there;
		AFKPGC.plugin.saveDefaultConfig();
		FileConfiguration conf = AFKPGC.plugin.getConfig();				
		
		int max_players = AFKPGC.plugin.getServer().getMaxPlayers();
		Kicker.kickThresholds = new int[max_players];	
		
		List<String> ktl =  conf.getStringList("kick_thresholds");
		int[] nums = new int[3];
		for(String s:ktl){	
			nums[0]=nums[1]=nums[2]=-1;	
			parseNaturals(s, nums);
			int min = nums[0], max = nums[1], t = nums[2];
			if(min > max || min < 1 || max < 1 || t < 0){
				AFKPGC.ErrorMessage("Configuration file error: " + s);
				return false;
			}
			
						
			for(int i = min; i <= max; i++){
				if(i <= max_players){
					Kicker.kickThresholds[i-1] = t;
				} 
			}			
		}
		
		boolean foundEmptyThreshold = false;
		for(int i = 0; i < max_players; i++){			
			if(Kicker.kickThresholds[i] == 0){
				AFKPGC.ErrorMessage("Configuration file incomplete - plugin doesn't know when to kick players when there are " + (i+1) + " players online");
				foundEmptyThreshold = true;				
			}			
		}		
		if(foundEmptyThreshold) return false;
		
		Kicker.message_on_kick = conf.getString("kick_message");	
		
		ArrayList<Warning> warnings = new ArrayList<Warning>();		
		ktl =  conf.getStringList("warnings");
		for(String s:ktl){	
			s = s.trim();
			int slen = s.length();
			StringBuilder sb = new StringBuilder();
			int n = 0;
			boolean numberPart = true;
			for(int i = 0; i < slen; i++){
				char c = s.charAt(i);
				if(numberPart && c >= '0' && c <= '9'){
					n=n*10+c-'0';
				} else {
					numberPart = false;
					sb.append(c);					
				}			
			}		
			warnings.add(new Warning(n*1000, sb.toString().trim()));			
		}
		
		int wlen = warnings.size();
		Warning[] wa = new Warning[wlen];
		for(int i = 0; i < wlen; i++) wa[i] = warnings.get(i);
		Kicker.warnings = wa;
		
		return true;
	}
	
	public static void parseNaturals(String str, int[] numbers){
		int len = str.length();
		int numberIndex = 0;
		int maxIndex = numbers.length;
		int currentNumber = 0;
		boolean numberStarted = false;		
		
		for(int i = 0; i < len; i++){
			char c = str.charAt(i);
			if(c >= '0' && c <= '9') {
				numberStarted = true;				
				currentNumber = currentNumber *10+(c-'0'); 
				numbers[numberIndex] = currentNumber;
			} else if (numberStarted) {
				numberStarted = false;
				currentNumber = 0;
				numberIndex++;
				if(numberIndex == maxIndex) return;				
			}
		}
	}	
}
