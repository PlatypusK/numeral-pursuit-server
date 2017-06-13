package com.platypus.gameserver;

import static com.platypus.gameserver.MyObjectifyService.ofy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.platypus.gameserver.apiclasses.GameUpdate;

@Entity
@Cache
public class ActiveGames {
	@Id
	Long id=1l;
	private Set<Long> activeGames=new HashSet<Long>();
	@Ignore
	boolean shouldSave=false;
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());


	private Map<Long,GameUpdate> loadAllActive(){
		return ofy().load().type(GameUpdate.class).ids(this.activeGames);
	}
	public static ActiveGames loadActiveGamesClass(){
		ActiveGames games= ofy().load().type(ActiveGames.class).id(1l).now();
		return games==null?new ActiveGames():games;
	}
	private void saveActiveGamesClass(){
		ofy().save().entity(this).now();
	}
	public static void addToActiveGames(Long gameId){
		ActiveGames games=loadActiveGamesClass();
		games.activeGames.add(gameId);
		games.saveActiveGamesClass();
	}
	public void testAllActiveGames(){
		Map<Long,GameUpdate> games=loadAllActive();
		for(Long gid:games.keySet()){
			GameUpdateHelper helper=new GameUpdateHelper(games.get(gid));
			if(helper.isFinished()){
				activeGames.remove(gid);
				shouldSave=true;
				continue;
			}
			if(helper.isPlayerTimeExpired()){
				onPlayerTimedOut(gid, helper);
			}
			if(helper.gameTimedOut()){
				onGameTimedOut(gid, helper);
			}
		}
	}
	private void onGameTimedOut(final Long gid, final GameUpdateHelper helper) {
		ofy().transact(new VoidWork(){
			@Override
			public void vrun() {
				log.warning("game over");
				helper.getUpdate().setWinner(0l);
				helper.getUpdate().setGameTimedOut(true);
				helper.saveUpdate();
				activeGames.remove(gid);
				shouldSave=true;
			}
		});
		ofy().transact(new VoidWork(){
			@Override
			public void vrun() {
				helper.setAsFinishedInProfilesAndSaveProfiles();				
			}
		});		
	}
	private void onPlayerTimedOut(final Long gid, final GameUpdateHelper helper) {
		ofy().transact(new VoidWork(){
			@Override
			public void vrun() {
				log.warning("game over");
				helper.getUpdate().setWinner(helper.getUpdate().getWhoMoved());
				helper.getUpdate().setPlayerTimedOut(helper.getOtherPlayer(helper.getUpdate().getWinner()));
				helper.saveUpdate();
				activeGames.remove(gid);
				shouldSave=true;
			}
		});
		ofy().transact(new VoidWork(){
			@Override
			public void vrun() {
				helper.setAsFinishedInProfilesAndSaveProfiles();
				helper.updateOpponentWithGCM(helper.getUpdate().getWinner());
				helper.updateOpponentWithGCM(helper.getUpdate().getPlayerTimedOut());
			}
		});
		

	}
	public boolean saveIfDirty(){
		return ofy().transact(new Work<Boolean>(){
			@Override
			public Boolean run() {
				if(shouldSave){
					saveActiveGamesClass();
					return true;
				}
				return false;
			}
		});

	}
}
