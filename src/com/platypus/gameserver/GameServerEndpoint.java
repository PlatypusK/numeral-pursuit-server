package com.platypus.gameserver;


import static com.games.common.Constants.AppengineAuthIds.CLIENT_ID_FOR_ANDROID_APPLICATION_DEBUG;
import static com.games.common.Constants.AppengineAuthIds.CLIENT_ID_FOR_ANDROID_APPLICATION_DEBUG_ADFREE;
import static com.games.common.Constants.AppengineAuthIds.CLIENT_ID_FOR_ANDROID_APPLICATION_PRODUCTION;
import static com.games.common.Constants.AppengineAuthIds.CLIENT_ID_FOR_ANDROID_APPLICATION_PRODUCTION_ADFREE;
import static com.games.common.Constants.AppengineAuthIds.CLIENT_ID_FOR_WEB_APPLICATION;
import static com.platypus.gameserver.MyObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.games.common.gsonmessages.ParentChatMessage;
import com.games.common.gsonmessages.ParentGameInviteMessage;
import com.google.android.gcm.server.Message;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;
import com.platypus.gameserver.apiclasses.EndpointChatMessage;
import com.platypus.gameserver.apiclasses.FriendData;
import com.platypus.gameserver.apiclasses.FriendlyGameData;
import com.platypus.gameserver.apiclasses.GameInfo;
import com.platypus.gameserver.apiclasses.GameInviteData;
import com.platypus.gameserver.apiclasses.GameUpdate;
import com.platypus.gameserver.apiclasses.GameUpdateRequest;
import com.platypus.gameserver.apiclasses.PlayerNicks;
import com.platypus.gameserver.apiclasses.PublicUId;
import com.platypus.gameserver.apiclasses.RegIdCGM;
import com.platypus.gameserver.apiclasses.Settings;
import com.platypus.gameserver.apiclasses.SuccessCode;



/**A generic gameserver for turnbased games.  The messages can be in any format, but a reasonable suggestion for the messages from client to client is json. 
 * There are a number of java libraries readily available for parsing and writing in this format. Do note that the limit on any messages using gcm is 4kB.
 * @author Ketil
 *
 */
@Api(
		name="gameserver",
		clientIds = {CLIENT_ID_FOR_WEB_APPLICATION,CLIENT_ID_FOR_ANDROID_APPLICATION_DEBUG,CLIENT_ID_FOR_ANDROID_APPLICATION_PRODUCTION,CLIENT_ID_FOR_ANDROID_APPLICATION_DEBUG_ADFREE,CLIENT_ID_FOR_ANDROID_APPLICATION_PRODUCTION_ADFREE},
		audiences={CLIENT_ID_FOR_WEB_APPLICATION}
		)
public class GameServerEndpoint {
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());
	/**Ask someone you know if they want to be your friend in game
	 * 
	 */
	public SuccessCode getNewFriend(final MessageGCM message, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return new SuccessCode(SuccessCode.SUCCESS);
	}
	@ApiMethod(
			httpMethod = HttpMethod.GET 
			)
	public PublicUId getPublicUserId(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
		if(profile==null){
			UserProfile.createNewUserProfile(user);
			profile=UserProfile.getExistingUserProfileFromUserObject(user);
		}
		return new PublicUId(profile.publicUserId);
	}
	public SuccessCode unregisterForRandomGame(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		final UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
		final MemCacheWorker cache=new MemCacheWorker(); 
		cache.removeRandomRegisteredNoTime(profile);
		return new SuccessCode(SuccessCode.SUCCESS);

	}
	public GameUpdate forfeit(final GameUpdateRequest forfeitRequest, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<GameUpdate>() {
			@Override
			public GameUpdate run() {
				Long moveMaker=forfeitRequest.getUserId();
				GameUpdate update=GameUpdateHelper.loadUpdate(forfeitRequest.getGameId());
				GameUpdateHelper helper=new GameUpdateHelper(update);
				helper.setGameForfeited(forfeitRequest.getUserId());
				helper.saveUpdate();
				helper.updateOpponentWithGCM(moveMaker);
				return update;
			}});



	}
	/**Register for a game against a random opponent. If two people are registered to start a random game,
	 * sends out a gcm message to both participants with the information about the match and who gets to play first. The message contains a public
	 * key for gcm messages between players, an id that identifies the match in the datastore/memcache
	 */
	public SuccessCode registerForRandomGame(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");

		final UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
		if(profile==null) return new SuccessCode(SuccessCode.USER_NOT_REGISTERED);
		log.warning("loaded profile");
		new GameRegistrator().registerForRandomGame(user, profile);
		log.warning("registered");
		SuccessCode code = null;
		code=MatchMaker.createRandomNontimedGame();
		log.warning("loaded game");
		log.warning(code.result);
		return code;
	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public GameUpdate updateGame(final GameUpdate update, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<GameUpdate>() {
			@Override
			public GameUpdate run() {
				Long moveMaker=update.getWhoMoved();
				GameUpdate oldUpdate=GameUpdateHelper.loadUpdate(update.getGameId());
				GameUpdateHelper helper=new GameUpdateHelper(update);
				helper.processUpdate(oldUpdate);
				if(helper.shouldSave()){
					helper.saveUpdate();
					helper.updateOpponentWithGCM(moveMaker);
				}
				if(helper.isFinished()){
					helper.setAsFinishedInProfilesAndSaveProfiles();
				}
				return helper.getUpdate();
			}
		});
	}
	public GameUpdate requestGameUpdate(final GameUpdateRequest req, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		GameUpdate returnValue= ofy().transact(new Work<GameUpdate>() {
			@Override
			public GameUpdate run() {
				GameUpdate update=GameUpdateHelper.loadUpdate(req.getGameId());
				return update;
			}});
		if(returnValue.getWinner()!=null){
			ofy().transact(new VoidWork(){
				@Override
				public void vrun() {
					UserProfile u=UserProfile.getUserProfileFromId(req.getUserId());
					if(u.removeFromCurrentGames(req.getGameId())){
						log.warning("saving finished game");
						u.addToFinishedGames(req.getGameId());
						u.save();
					}					
				}
			});

		}
		return returnValue;
	}


	public Settings login(final RegIdCGM regid,final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		UserProfile profile=ofy().transact(new Work<UserProfile>(){
			@Override
			public UserProfile run() {
				UserProfile loaded=UserProfile.getExistingUserProfileFromUserObject(user);
				if(loaded==null){
					loaded=UserProfile.createNewUserProfile(user);
				}
				return loaded;
			}
		});
		GCMServices.saveGCMIdIfNew(regid,profile);
		Settings set=profile.settings;
		set.setUserId(profile.publicUserId);
		return set;
	}


	public List<GameInfo> getRunningGamesList(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		final UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
		List<GameInfo> runningGames=profile.getCurrentGames(profile);

		ofy().transact(new VoidWork(){
			@Override
			public void vrun() {
				if(profile.shouldSave){
					profile.save();
				}				
			}

		});
		return runningGames;

	}
	public SuccessCode sendChatMessage(final EndpointChatMessage message, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile receiver=UserProfile.getUserProfileFromId(message.getReceiverId());
				UserProfile sender=UserProfile.getUserProfileFromId(message.getSenderId());
				ParentChatMessage gsonMessage=new ParentChatMessage(message.getGameId(),message.getReceiverId(),message.getMessage());
				gsonMessage.setOpName(sender.settings.getName());
				Message mes=GCMServices.getMessage(gsonMessage.toString());
				GCMServices.sendMessage(receiver,mes, receiver.gcmId, 5);
				return SuccessCode.SUCCESSFULL;
			}});
	}
	public SuccessCode addFriend(final FriendData newFriend, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile caller=UserProfile.getExistingUserProfileFromUserObject(user);
				if(newFriend.getFriendId()!=null){
					caller.addFriend(newFriend.getFriendId());
					caller.save();
				}
				else{
					Long friendId=UserToUserProfileMemCacheHelper.loadPublicUserId(newFriend.getFriendEmail());
					if(friendId==null){
						return SuccessCode.CODE_FRIEND_NOT_REGISTERED;
					}
					else{
						caller.addFriend(friendId);
						caller.save();
					}
				}
				return SuccessCode.SUCCESSFULL;
			}});
	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public SuccessCode removeFriend(final FriendData badFriend,final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile caller=UserProfile.getExistingUserProfileFromUserObject(user);
				caller.removeFriend(badFriend.getFriendId());
				caller.save();
				return SuccessCode.SUCCESSFULL;
			}});

	}
	public ArrayList<FriendData> getFriends(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<ArrayList<FriendData>>() {
			@Override
			public ArrayList<FriendData> run() {
				UserProfile caller=UserProfile.getExistingUserProfileFromUserObject(user);
				return caller.getFriends();
			}});

	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public SuccessCode inviteFriendlyGame(final GameInviteData inv, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile profile=UserProfile.getUserProfileFromId(inv.getReceiverId());
				UserProfile sender=UserProfile.getExistingUserProfileFromUserObject(user);
				ParentGameInviteMessage pgim =new ParentGameInviteMessage(sender.getNick(), sender.publicUserId, inv.getMaxTimeToAnswer(),profile.publicUserId,profile.getNick());
				Message mes=GCMServices.getMessage(pgim.toString());
				GCMServices.sendMessage(profile,mes, profile.gcmId, 5);
				return SuccessCode.SUCCESSFULL;
			}});

	}
	public SuccessCode createFriendlyGame(final FriendlyGameData data, User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		final UserProfile profile1=UserProfile.getUserProfileFromId(data.getId1());
		final UserProfile profile2=UserProfile.getUserProfileFromId(data.getId2());
		final GameUpdateHelper helper= ofy().transact(new Work<GameUpdateHelper>() {
			@Override
			public GameUpdateHelper run() {

				GameUpdate update=GameUpdateHelper.initGame(profile1, profile2, new Random(System.currentTimeMillis()).nextFloat(), GameUpdateHelper.GameTypes.STANDARD_FRIENDLY_GAME);
				GameUpdateHelper helper=new GameUpdateHelper(update);
				helper.saveUpdate();
				profile1.addRunningGame(update);
				profile2.addRunningGame(update);
				UserProfile.saveAll(profile1,profile2);
				helper.notifyPlayersNewGame();
				return helper;
			}});
		SuccessCode code=ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				ActiveGames.addToActiveGames(helper.getUpdate().getGameId());
				return SuccessCode.SUCCESSFULL;
			}});
		return code;

	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public SuccessCode removeGameFromActive(final GameUpdate update, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
				profile.removeFromCurrentGames(update.getGameId());
				profile.addToFinishedGames(update.getGameId());
				profile.save();
				return SuccessCode.SUCCESSFULL;
			}});

	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public Settings fetchSettings(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<Settings>() {
			@Override
			public Settings run() {
				UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
				return profile.getSettings();
			}});


	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public SuccessCode updateSettings(final Settings newSettings,final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
				Settings oldSettings=fetchSettings(user);
				if(oldSettings.isChanged(newSettings)){
					profile.updateSettings(newSettings);
					profile.save();
				}
				return SuccessCode.SUCCESSFULL;
			}});

	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public List<com.platypus.gameserver.apiclasses.GameInfo> fetchFinishedGames(final GameListContainer knownList,final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		final UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
		List<GameInfo> list=profile.getFinishedGamesUpdate(knownList.getList());
		ofy().transact(new VoidWork(){

			@Override
			public void vrun() {
				if(profile.shouldSave){
					profile.save();
				}				
			}

		});

		return list;
	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public SuccessCode removeAllFinishedGames(final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return ofy().transact(new Work<SuccessCode>() {
			@Override
			public SuccessCode run() {
				UserProfile profile=UserProfile.getExistingUserProfileFromUserObject(user);
				profile.finishedGames=new ArrayList<Long>();
				profile.save();
				return SuccessCode.SUCCESSFULL;
			}});

	}
	@ApiMethod(
			httpMethod = HttpMethod.POST
			)
	public PlayerNicks fetchPlayerNicks(final PlayerNicks ids, final User user){
		if(user==null) throw new IllegalArgumentException("User object can not be null, use authenticated calls only");
		return UserProfile.fetchPlayerNicks(ids);
	}
}
