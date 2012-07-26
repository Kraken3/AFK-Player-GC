package com.github.Kraken3.AFKPGC;

import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;

import org.bukkit.entity.Player;

public class Message {
	//Send a message to player or server console if player is null
	public static void send(Player player, String str, Object... args){
		StringBuilder sb = new StringBuilder();	
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(str, args);		
		if(player != null) player.sendMessage(sb.toString());
		else AFKPGC.logger.info(sb.toString());
		formatter.close();
	}
	
	public static void send(String str, Object... args){
		StringBuilder sb = new StringBuilder();	
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(str, args);
		AFKPGC.logger.info(sb.toString());
		formatter.close();
	}
	
	public static void warning(String str, Object... args){
			StringBuilder sb = new StringBuilder();	
			Formatter formatter = new Formatter(sb, Locale.US);
			formatter.format(str, args);
			AFKPGC.logger.log(Level.WARNING, sb.toString());	
			formatter.close();
	}
	
	public static void error(String str, Object... args){
		StringBuilder sb = new StringBuilder();	
		Formatter formatter = new Formatter(sb, Locale.US);
		formatter.format(str, args);
		AFKPGC.logger.log(Level.SEVERE, sb.toString());	
		formatter.close();
	}
	
	public static void send(Player player, int str, Object... args){
		send(player, stringResource(str), args);
	}
	
	public static void send(int str, Object... args){
		send(stringResource(str), args);
	}
	
	public static void warning(int str, Object... args){
		warning(stringResource(str), args);	
	}
	
	public static void error(int str, Object... args){
		error(stringResource(str), args);
	}
	
	private static String stringResource(int i){
		return AFKPGC.plugin.getConfig().getString("Str" + i);
	}
	

}
