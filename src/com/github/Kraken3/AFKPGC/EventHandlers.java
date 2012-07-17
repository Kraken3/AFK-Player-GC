package com.github.Kraken3.AFKPGC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

class EventHandlers implements Listener {
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		LastActivity.lastActivities.remove(event.getPlayer().getEntityId());	
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {				
		LastActivity la = new LastActivity();
		la.playerName = event.getPlayer().getName();
		la.timeOfLastActivity = System.currentTimeMillis();		
		LastActivity.lastActivities.put(event.getPlayer().getEntityId(),la);		
	}	
	
	
	public void registerActivity(int id){
		LastActivity.lastActivities.get(id).timeOfLastActivity = LastActivity.currentTime;
	}
	
	
	//EVENTS THAT REGISTER PLAYER ACTIVITY
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {			
		registerActivity(event.getPlayer().getEntityId());
	}	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChatEvent(PlayerChatEvent event) {
		registerActivity(event.getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		registerActivity(event.getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {	
		registerActivity(event.getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
		registerActivity(event.getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
		registerActivity(event.getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {	
		registerActivity(event.getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEnchantItemEvent(EnchantItemEvent event) {		
		registerActivity(event.getEnchanter().getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {	
		registerActivity(event.getEnchanter().getPlayer().getEntityId());
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClickEvent(InventoryClickEvent event) {	
		registerActivity(event.getWhoClicked().getEntityId());
	}

}
