package com.platypus.gameserver;

import static com.platypus.gameserver.MyObjectifyService.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.platypus.gameserver.apiclasses.FriendData;
import com.platypus.gameserver.apiclasses.GameInfo;
import com.platypus.gameserver.apiclasses.GameUpdate;
import com.platypus.gameserver.apiclasses.PlayerNicks;
import com.platypus.gameserver.apiclasses.Settings;

@SuppressWarnings("serial")
@Entity
@Cache
public class UserProfile implements Serializable {
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());

	@Id Long publicUserId;
	public String gcmId=null;
	Settings settings=new Settings();
	private Boolean settingNotify=true;
	String userId=null;
	String userEmail=null;
	Set<Long> friends=new HashSet<Long>();
	ArrayList<Long> currentGames=new ArrayList<Long>();
	//	@Serialize private ArrayList<RunningGame> runningGames=new ArrayList<RunningGame>();
	public ArrayList<Long> finishedGames=new ArrayList<Long>();
	@Ignore
	public boolean shouldSave=false;
//	public String nick;
	public static final String[] USER_NICKS_RANDOM=new String[]{
		"Llama", "Alpaca", "Platypus", "Koala", "Panda",
		"Lion", "Tiger", "Bear", "Tapir", "Armadillo","Axolotl",
		"Hummingbird", "Hawk", "Eagle", "Wolverine", "Wolf",
		"Lynx", "Cougar", "Aardvark", "Albatross", "Alligator", "Ant",
		"Badger", "Barracuda", "Bat", "Beaver", "Bison", "Camel", "Caribou",
		"Crow", "Raven", "Magpie", "Mallard", "Lobster", "Mink", "Parrot",
		"Porpoise", "Seal", "Swan", "Squid"
	};
	@Override
	public boolean equals(final Object o){
		UserProfile other=null;
		try{
			other=(UserProfile) o;
		}
		catch(final ClassCastException e){
			return false;
		}
		if(this.publicUserId.equals(other.publicUserId)) return true;
		return false;
	}
	@Override
	public String toString(){
		if(this.publicUserId!=null){
			return "publicUID: " + Long.toString(this.publicUserId);
		}
		else{
			return "publicUID: "+ "no uid set";
		}
	}
	/**
	 * Fetches the user profile from the user parameter of an appengine cloud endpoint method. If the user is not yet saved, create a new UserProfile
	 * @param user The User argument from a cloud endpoint method
	 * @return
	 */
	public static UserProfile getExistingUserProfileFromUserObject(final User user){
		UserProfile profile = null;
		UserToUserProfileMemCacheHelper helper = null;
		helper=ofy().load().type(UserToUserProfileMemCacheHelper.class).id(user.getEmail()).now();
		if(helper!=null){
			profile = ofy().load().type(UserProfile.class).id(helper.publicUserId).now();
		}
		return profile;
	}
	public static UserProfile getUserProfileFromId(final Long publicId){
		UserProfile profile=null;
		profile = ofy().load().type(UserProfile.class).id(publicId).now();
		return profile;

	}
	/**
	 * Create a new user profile with a corresponding UserToUserProfileMemCacheHelper. Also returns it in case there is a desire to do anything else with it.
	 * @param user The user given as argument to the appengine cloud endpoint method
	 * @return
	 */
	public static UserProfile createNewUserProfile(final User user) {
		UserProfile profile=new UserProfile();
		profile.userId=user.getUserId();
		profile.userEmail=user.getEmail();
		profile.settings.setName(getRandomNick());
		ofy().save().entity(profile).now();
		log.warning("4");
		UserToUserProfileMemCacheHelper helper=new UserToUserProfileMemCacheHelper(user, profile.publicUserId);
		ofy().save().entity(helper).now();
		return profile;
	}
	public class RunningGame implements Serializable{
		private final Long gameId;
		private final Long opId;
		private final String opName;
		public RunningGame(final Long gameId, final Long opId, final String opName) {
			this.opId = opId;
			this.opName = opName;
			this.gameId=gameId;
		}
		public Long getOpId(){
			return opId;
		}
		public String getOpName(){
			return opName;
		}
		public Long getGameId() {
			return gameId;
		}
	}
	public void addRunningGame(final GameUpdate update) {
		currentGames.add(update.getGameId());
	}
	public List<GameInfo> getCurrentGames(UserProfile profile){
		return getGamesWithIds(this.currentGames);
	}
	private ArrayList<GameInfo> makeGameLists(Map<Long,GameUpdate> gamesMap) {
		Set<Long> gameIds=gamesMap.keySet();
		ArrayList<GameInfo> infos=new ArrayList<GameInfo>();
		ArrayList<Long> opIds=new ArrayList<Long>();
		for(Long gameId:gameIds){
			GameInfo info=new GameInfo();
			if(gamesMap.get(gameId)==null){
				this.finishedGames.remove(gameId);
				this.currentGames.remove(gameId);
				this.shouldSave=true;
				continue;
			}
			GameUpdateHelper helper=new GameUpdateHelper(gamesMap.get(gameId));
			info.setGameId(gameId);
			info.setGameName(helper.getUpdate().getGameName());
			info.setOpId(helper.getOtherPlayer(this.publicUserId));
			info.setGameType(helper.getUpdate().getGameType());
			info.setResult(helper.getGameResult(this.publicUserId));
			info.setReasonEnded(helper.getGameOverReason());
			opIds.add(info.getOpId());
			infos.add(info);
		}
		Map<Long,UserProfile> opProfiles=loadAll(opIds);
		for(GameInfo info:infos){
			if(opProfiles.get(info.getOpId())!=null){
				info.setOpNames(opProfiles.get(info.getOpId()).getNick());
			}
			else{
				info.setOpNames("Deleted profile");
			}
			
		}
		return infos;
	}
	public void setNick(final String nick){
		this.settings.setName(nick);
	}
	public String getNick() {
		return this.settings.getName();
	}
	public static void notifyGameExpired(final Long gameId, final Long userId) {
		ofy().load().type(UserProfile.class).id(userId).now();
		
	}
	public static final String getRandomNick(){
		Random rand=new Random(System.currentTimeMillis());
		return(USER_NICKS_RANDOM[rand.nextInt(USER_NICKS_RANDOM.length)]);
	}
	public boolean removeFromCurrentGames(Long gameId) {
		return this.currentGames.remove(gameId);
	}
	public void addToFinishedGames(Long gameId) {
		this.finishedGames.add(gameId);
	}
	public static final void saveAll(UserProfile...profiles){
		ofy().save().entities(profiles).now();
	}
	public void addFriend(Long friendId){
		this.friends.add(friendId);
	}
	public void removeFriend(Long friendId){
		this.friends.remove(friendId);
	}
	public ArrayList<FriendData> getFriends(){
		Map<Long,UserProfile> friendProfiles=loadAll(friends);
		ArrayList<FriendData> friendsData=new ArrayList<FriendData>();
		for(Long uid:friends){
			FriendData data=new FriendData(uid,friendProfiles.get(uid).settings.getName());
			data.setFriendEmail(friendProfiles.get(uid).userEmail);
			friendsData.add(data);
		}
		return friendsData;
	}

	public static final Map<Long,UserProfile> loadAll(Collection<Long> userIds){
		return  ofy().load().type(UserProfile.class).ids(userIds);
	}
	public void save() {
		ofy().save().entity(this).now();
		
	}
	public Boolean shouldUseNotifications(){
		return settingNotify;
	}
	public void useNotifications(boolean use){
		settingNotify=use;
	}
	public void updateSettings(Settings newSettings) {
		this.settings=newSettings;
	}
	public List<GameInfo> getFinishedGamesUpdate(List<GameInfo> knownList) {
		if(knownList==null || knownList.isEmpty()){
			return getGamesWithIds(this.finishedGames);
		}
		else{
			ArrayList<Long> newIds=this.finishedGames;
			for(GameInfo info:knownList){
				newIds.remove(info.getGameId());
			}
			List<GameInfo> newGamesList=getGamesWithIds(newIds);
			knownList.addAll(newGamesList);
			return knownList;
		}

	}
	private List<GameInfo> getGamesWithIds(ArrayList<Long> games) {
		Map<Long,GameUpdate> gamesMap=GameUpdateHelper.loadAll(games);
		ArrayList<GameInfo> info=makeGameLists(gamesMap);
		return info;
	}
	public Settings getSettings() {
		// TODO Auto-generated method stub
		return this.settings;
	}
	public static PlayerNicks fetchPlayerNicks(PlayerNicks ids) {
		List<Long> idsList=new ArrayList<Long>();
		idsList.add(ids.getIdOne());idsList.add(ids.getIdTwo());
		Map<Long,UserProfile> map=UserProfile.loadAll(idsList);
		ids.setNickOne(map.get(ids.getIdOne()).settings.getName());
		ids.setNickTwo(map.get(ids.getIdTwo()).settings.getName());
		return ids;
	}
}
