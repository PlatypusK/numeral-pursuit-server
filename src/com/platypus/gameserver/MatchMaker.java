package com.platypus.gameserver;

import static com.platypus.gameserver.MyObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import com.platypus.gameserver.apiclasses.GameUpdate;
import com.platypus.gameserver.apiclasses.SuccessCode;


/**Contains static methods to create the various types of games and inform the participants*/
public class MatchMaker {
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());

	public static SuccessCode createRandomNontimedGame() {


		final MemCacheWorker cache=new MemCacheWorker();
		final ArrayList<UserProfile> profiles=cache.removeRandomRegisteredNoTimeList();

		if(profiles.size()<2){
			if(profiles.size()==1) cache.putRandomRegisteredNoTime(profiles.get(0));
		}
		else{
			log.warning(Long.toString(profiles.get(0).publicUserId)+Long.toString(profiles.get(1).publicUserId));
		}
		createGameNotifyPlayers(profiles);

		return new SuccessCode(SuccessCode.SUCCESS);
	}

	private static void createGameNotifyPlayers(final ArrayList<UserProfile> profiles) {
		log.warning(Integer.toString(profiles.size()));
		while(profiles.size()>=2){
			final UserProfile profile1=profiles.remove(0);
			final UserProfile profile2=profiles.remove(0);
			GameUpdate game=GameUpdateHelper.initGame(profile1, profile2,new Random(System.currentTimeMillis()).nextFloat(),GameUpdateHelper.GameTypes.RANDOM_OPPONENT_DEFAULT_TIME);
			GameUpdateHelper helper=new GameUpdateHelper(game);
			storeNewGame(helper,profile1,profile2);
			helper.notifyPlayersNewGame();
		}
	}



	private static void storeNewGame(final GameUpdateHelper helper, final UserProfile profile1, final UserProfile profile2) {
		helper.saveUpdate();
		log.warning(helper.getUpdate().getGameId().toString());
		ActiveGames.addToActiveGames(helper.getUpdate().getGameId());
		profile1.addRunningGame(helper.getUpdate());
		profile2.addRunningGame(helper.getUpdate());
		ofy().save().entities(profile1,profile2).now();
	}
}

