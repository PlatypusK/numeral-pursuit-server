package com.platypus.gameserver;

import static com.platypus.gameserver.MyObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import com.games.common.gsonmessages.ParentGameOverMessage;
import com.games.common.gsonmessages.ParentGameUpdateMessage;
import com.games.common.gsonmessages.ParentNewGameMessage;
import com.google.android.gcm.server.Message;
import com.platypus.gameserver.apiclasses.GameUpdate;



public class GameUpdateHelper {
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());

	public static final List<Integer> EMPTY_GRID=
			Arrays.asList(new Integer[]{
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,});
	public static final Long MAX_UPDATE_TIME_DEFAULT=1000l*60l*180l;
	public static final Long MAX_GAME_TIME_DEFAULT=1000l*60l*60l*24l*14l;
	private static final Integer MAX_TILES_ON_BOARD = 81;
	private GameUpdate update;

	private boolean shouldSave=false;
	GameUpdateHelper(final GameUpdate update){
		this.update=update;
	}
	public void setUpdate(final GameUpdate update){
		this.update=update;
	}
	public GameUpdate getUpdate(){
		return this.update;
	}
	public boolean isMostRecentUpdate(final GameUpdate otherUpdate){
		return otherUpdate.getMoveNr()<=update.getMoveNr();
	}
	public boolean isFinished(){
		return update.getWinner()!=null;
	}
	public boolean isForfeited(){
		return update.getForfeitedBy()!=null;
	}
	public boolean playerTimedOut(final GameUpdate oldUpdate){
		log.warning("test if player timed out");
		Long time=System.currentTimeMillis();
		if(time-oldUpdate.getUpdateTime()>update.getMaxUpdateTime()){
			log.warning("player timed out");
			return true;
		}
		return false;
	}
	public boolean gameTimedOut(){
		Long time=System.currentTimeMillis();
		return time-update.getFirstUpdateTime()>update.getMaxGameTime();
	}
	public Long getOtherPlayer(final Long player){
		return update.getPlayers().get(0).equals(player) ? update.getPlayers().get(1):update.getPlayers().get(0);
	}
	public int getPlayerNumber(final Long player){
		return update.getPlayers().get(0).equals(player) ? 0:1;
	}
	public void updatePlayerScore(){
		final int playerNr=getPlayerNumber(update.getWhoMoved());
		List<Integer> scores=update.getScore();
		Integer score=scores.get(playerNr);
		Integer change=(update.getMoveCorrect()?1:(-1));
		score=score+change;
		scores.set(playerNr, score);
		update.setScore(scores);
	}
	private void updateTilesFilled(final GameUpdate oldUpdate) {
		int increment=(update.getMoveCorrect()?1:0);
		Integer tilesFilled=oldUpdate.getTilesFilled()+increment;
		update.setTilesFilled(tilesFilled);
	}
	public static GameUpdate loadUpdate(final Long gameId) {
		return ofy().load().type(GameUpdate.class).id(gameId).now();
	}
	public void saveUpdate() {
		ofy().save().entity(update).now();
	}
	public void addToPlayerActiveGames(){

	}
	/**Takes the stored update as an argument and incorporates any changes
	 * 
	 * @param oldUpdate
	 * @return
	 */
	public GameUpdate processUpdate(final GameUpdate oldUpdate) {
		GameUpdateHelper oldHelper=new GameUpdateHelper(oldUpdate);
		if(oldHelper.isFinished()){
			this.update=oldUpdate;
			return oldUpdate;
		}
		if(oldHelper.isMostRecentUpdate(this.update)){
			this.update=oldUpdate;

			return oldUpdate;
		}
		this.shouldSave=true;
		update.setGameType(oldUpdate.getGameType());
		update.setGameName(oldUpdate.getGameName());
		if(oldHelper.gameTimedOut()){
			oldUpdate.setWinner(0l);
			oldUpdate.setGameTimedOut(true);
			oldHelper.setGameOverAndNotify(oldUpdate.getWinner());
			this.update=oldUpdate;

			return oldUpdate;
		}
		setTimes(oldUpdate);
		if(playerTimedOut(oldUpdate)){
			log.warning("player timed out");
			setPlayerTimedOut(oldUpdate);
			this.update=oldUpdate;

			return oldUpdate;
		}
		updateTilesFilled(oldUpdate);
		updatePlayerScore();
		if(this.isBoardFull()){
			Long winner=getWinnerOnScore();
			this.setGameOverAndNotify(winner);
		}

		return update;
	}

	private void setPlayerTimedOut(GameUpdate oldUpdate) {
		oldUpdate.setWinner(getOtherPlayer(update.getWhoMoved()));
		oldUpdate.setPlayerTimedOut(update.getWhoMoved());
		new GameUpdateHelper(oldUpdate).saveUpdate();
	}
	private void setTimes(final GameUpdate oldUpdate) {
		update.setFirstUpdateTime(oldUpdate.getFirstUpdateTime());
		update.setMaxGameTime(oldUpdate.getMaxGameTime());
		update.setMaxUpdateTime(oldUpdate.getMaxUpdateTime());
		update.setUpdateTime(System.currentTimeMillis());
	}
	private Long getWinnerOnScore() {
		if(update.getScore().get(0).equals(update.getScore().get(1))){
			return Long.valueOf(0);
		}
		return update.getScore().get(0)>update.getScore().get(1)?update.getPlayers().get(0):update.getPlayers().get(1);
	}
	public void updateOpponentWithGCM(Long clientId) {
		UserProfile u=UserProfile.getUserProfileFromId(this.getOtherPlayer(clientId));
		String gsonString=new ParentGameUpdateMessage(update.getGameId(), update.getGameName(), u.publicUserId,this.isFinished()).toString();
		Message mes=GCMServices.getMessage(gsonString);
		GCMServices.sendMessage(u,mes, u.gcmId, 5);
	}
	public void notifyPlayersNewGame(){
		List<Long> players=update.getPlayers();
		UserProfile player1=UserProfile.getUserProfileFromId(players.get(0));
		UserProfile player2=UserProfile.getUserProfileFromId(players.get(1));
		Message mes1=GCMServices.getMessage(new ParentNewGameMessage(update.getGameId(),update.getGameType(),player1.publicUserId).toString());
		Message mes2=GCMServices.getMessage(new ParentNewGameMessage(update.getGameId(),update.getGameType(),player2.publicUserId).toString());
		GCMServices.sendMessage(player1,mes1, player1.gcmId, 5);
		GCMServices.sendMessage(player2,mes2, player2.gcmId, 5);
	}
	public static GameUpdate initGame(final UserProfile profile1, final UserProfile profile2, final float whoStarts, int gameType) {
		UserProfile player1=0.5<whoStarts?profile1:profile2;
		UserProfile player2=0.5>=whoStarts?profile1:profile2;
		Long time=System.currentTimeMillis();
		return new GameUpdate(EMPTY_GRID, false, -1, -1, player2.publicUserId, Arrays.asList(new Long[]{player1.publicUserId,player2.publicUserId}), Arrays.asList(new Integer[]{0,0}), 0, null, null, null, time, MAX_UPDATE_TIME_DEFAULT, time, MAX_GAME_TIME_DEFAULT, 0,getRandomGameName(),gameType);
	}
	public void setGameForfeited(final Long forfeiter) {
		update.setForfeitedBy(forfeiter);
		setGameOverAndNotify(getOtherPlayer(forfeiter));
	}
	private void setGameOverAndNotify(Long winner) {
		update.setWinner(winner);
		saveUpdate();
		setAsFinishedInProfilesAndSaveProfiles();
		notifyPlayersGameOver();

	}
	public static String getRandomGameName(){
		Random rand=new Random(System.currentTimeMillis());
		return GAME_NAMES_RANDOM[rand.nextInt(GAME_NAMES_RANDOM.length)];
	}
	public static final String[] GAME_NAMES_RANDOM= new String[]{
		"Everest", "Cerro Aconcagua", "Denali",
		"Kilimanjaro","Cristobal Colon","Mount Logan",
		"Citlaltepetl", "Mount Vinson", "Puncak Jaya",
		"Gora Elbrus", "Mount Blanc", "Damavand",
		"Klyuchevska", "Nanga Perbat",
		"Pacific","Atlantic", "Mediterranean",
		"Sahara", "Gobi", "Kara Kum"

	};
	public void setAsFinishedInProfilesAndSaveProfiles() {
		List<Long> players=update.getPlayers();
		UserProfile player1=UserProfile.getUserProfileFromId(players.get(0));
		UserProfile player2=UserProfile.getUserProfileFromId(players.get(1));
		player1.removeFromCurrentGames(update.getGameId());
		player2.removeFromCurrentGames(update.getGameId());
		player1.addToFinishedGames(update.getGameId());
		player2.addToFinishedGames(update.getGameId());
		UserProfile.saveAll(player1,player2);
	}
	public void notifyPlayersGameOver(){
		List<Long> players=update.getPlayers();
		UserProfile player1=UserProfile.getUserProfileFromId(players.get(0));
		UserProfile player2=UserProfile.getUserProfileFromId(players.get(1));
		String gson1=new ParentGameOverMessage(update.getGameId(), update.getGameName(), player1.publicUserId, 
				player1.getNick(), player2.getNick(), getGameOverReason(), getGameResult(player1.publicUserId)).toString();
		String gson2=new ParentGameOverMessage(update.getGameId(), update.getGameName(), player2.publicUserId, 
				player2.getNick(), player1.getNick(), getGameOverReason(),getGameResult(player2.publicUserId) ).toString();
		Message mes1=GCMServices.getMessage(gson1);
		Message mes2=GCMServices.getMessage(gson2);
		GCMServices.sendMessage(player1,mes1, player1.gcmId, 5);
		GCMServices.sendMessage(player2,mes2, player2.gcmId, 5);
	}
	public int getGameResult(Long playerId) {
		if(!this.isFinished()) return ParentGameOverMessage.WinnerValues.NOT_FINISHED;
		if(this.isTied()) return ParentGameOverMessage.WinnerValues.TIE;
		if(this.isWinner(playerId)) return ParentGameOverMessage.WinnerValues.THIS_PLAYER_WON;
		else return ParentGameOverMessage.WinnerValues.OPPONENT_WON;
	}
	private boolean isWinner(Long playerId) {
		return this.update.getWinner().equals(playerId);
	}
	private boolean isTied() {
		return update.getWinner().equals(0l);
	}
	public int getGameOverReason() {
		if(!isFinished()) return ParentGameOverMessage.GameOverReason.NOT_FINISHED;
		if(update.getGameTimedOut()) return ParentGameOverMessage.GameOverReason.GAME_TIMED_OUT;
		if(this.isForfeited()) return ParentGameOverMessage.GameOverReason.GAME_FORFEITED;
		if(update.getPlayerTimedOut()!=null) return ParentGameOverMessage.GameOverReason.PLAYER_TIMED_OUT;
		if(this.isBoardFull()) return ParentGameOverMessage.GameOverReason.BOARD_FILLED;
		throw new IllegalArgumentException("Could not parse result. Error in GameUpdate object or GameUpdateHelper");
	}
	private boolean isBoardFull() {
		List<Integer> grid=update.getNewGrid();
		
		return this.update.getTilesFilled().equals(MAX_TILES_ON_BOARD);
	}
	public static Map<Long, GameUpdate> loadAll(ArrayList<Long> currentGames) {
		Map<Long, GameUpdate> games = ofy().load().type(GameUpdate.class).ids(currentGames);
		return games;
	}
	public interface GameTypes{
		public static final int RANDOM_OPPONENT_DEFAULT_TIME=1;
		public static final int STANDARD_FRIENDLY_GAME=2;

	}
	public boolean isPlayerTimeExpired(){
		return System.currentTimeMillis()-update.getUpdateTime()>update.getMaxUpdateTime();
	}
	public boolean shouldSave(){
		return shouldSave;
	}
}
