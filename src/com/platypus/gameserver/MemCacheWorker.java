package com.platypus.gameserver;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemCacheWorker {

	private MemcacheService service;
	private static final String KEY_RANDOM_NO_TIME_REGISTERED="KEY_RANDOM_NO_TIME_REGISTERED";
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());

	public MemCacheWorker(){
		this.service = MemcacheServiceFactory.getMemcacheService();
		this.service.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	}
	private void safePutExclusiveInList(final String listKey, final Object object) {
		boolean putUnTouched=false;
		while(!putUnTouched){
			final MemcacheService.IdentifiableValue identifiable=service.getIdentifiable(listKey);
			if(identifiable!=null){
				@SuppressWarnings("unchecked")	//we are sure that the listkey always points to a list of objects as this is what it is used for
				final ArrayList<Object> list=(ArrayList<Object>) identifiable.getValue();
				log.warning(list.toString());
				if(!list.contains(object)){
					list.add(object);
					putUnTouched=service.putIfUntouched(listKey, identifiable, list);
				}
				else return;
			}
			else{
				final ArrayList<Object> list=new ArrayList<Object>();
				list.add(object);
				log.warning("putting in list");
				service.put(listKey, list);
				log.warning("after put in list");
				putUnTouched=true;
			}
		}
	}
	private ArrayList<Object> safeRemoveList(final String listKey){
		ArrayList<Object> profiles=null;
		boolean putUnTouched=false;
		while(!putUnTouched){
			final MemcacheService.IdentifiableValue identifiable=service.getIdentifiable(listKey);
			if(identifiable==null) return null;
			@SuppressWarnings("unchecked")	//we are sure that the listkey always points to a list of objects as this is what it is used for
			final ArrayList<Object> list=(ArrayList<Object>) identifiable.getValue();
			profiles=new ArrayList<Object>(list);
			list.removeAll(profiles);
			putUnTouched=service.putIfUntouched(listKey, identifiable, list);
		}
		return profiles;
	}
	private void safeRemoveItemFromList(final String listKey, final Object item){
		boolean putUnTouched=false;
		log.warning("Remove item");
		while(!putUnTouched){
			final MemcacheService.IdentifiableValue identifiable=service.getIdentifiable(listKey);
			if(identifiable==null) return;
			@SuppressWarnings("unchecked")	//we are sure that the listkey always points to a list of objects as this is what it is used for
			final ArrayList<Object> list=(ArrayList<Object>) identifiable.getValue();
			logList(list);
			list.remove(item);
			logList(list);
			putUnTouched=service.putIfUntouched(listKey, identifiable, list);
		}
	}
	private void logList(final ArrayList<Object> list){
		if(list==null) log.warning("List is null");
		else if(list.isEmpty()) log.warning("List is empty");
		else log.warning(list.toString());
	}
	public void putRandomRegisteredNoTime(final UserProfile profile){
		safePutExclusiveInList(KEY_RANDOM_NO_TIME_REGISTERED,profile);
	}

	public ArrayList<UserProfile> removeRandomRegisteredNoTimeList(){
		final ArrayList<Object> list=safeRemoveList(KEY_RANDOM_NO_TIME_REGISTERED);
		final ArrayList<UserProfile> profiles=new ArrayList<UserProfile>();
		if(list==null) return profiles;
		for(final Object object:list) profiles.add((UserProfile)object);
		return profiles;
	}
	public void removeRandomRegisteredNoTime(final UserProfile profile){
		try{
			safeRemoveItemFromList(KEY_RANDOM_NO_TIME_REGISTERED, profile);
		}
		catch(final IllegalArgumentException e){
			e.printStackTrace();
		}
	}
}
