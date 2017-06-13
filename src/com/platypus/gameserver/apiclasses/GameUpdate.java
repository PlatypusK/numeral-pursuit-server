package com.platypus.gameserver.apiclasses;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


@Entity
@Cache
public class GameUpdate {
	@Id Long gameId;
	List<Integer> newGrid; 
	boolean moveCorrect; 
	int place;
	int value; 
	Long whoMoved;

	List<Long> players;
	List<Integer> score;
	int moveNr;
	Long winner;
	Long forfeitedBy;
	boolean gameTimedOut=false;
	Long playerTimedOut;
	Long updateTime;
	Long maxUpdateTime;
	Long firstUpdateTime;
	Long maxGameTime;
	Integer tilesFilled=0;
	String gameName;
	Integer gameType;




	public GameUpdate(){}


	public GameUpdate(final List<Integer> newGrid,
			final boolean moveCorrect, final int place, final int value, final Long whoMoved,
			final List<Long> players, final List<Integer> score, final int moveNr, final Long winner,
			final Long forfeitedBy, final Long playerTimedOut,
			final Long updateTime, final Long maxUpdateTime, final Long firstUpdateTime,
			final Long maxGameTime, final Integer tilesFilled,String gameName, Integer gameType) {
		super();
		this.newGrid = newGrid;
		this.moveCorrect = moveCorrect;
		this.place = place;
		this.value = value;
		this.whoMoved = whoMoved;
		this.players = players;
		this.score = score;
		this.moveNr = moveNr;
		this.winner = winner;
		this.forfeitedBy = forfeitedBy;
		this.playerTimedOut = playerTimedOut;
		this.updateTime = updateTime;
		this.maxUpdateTime = maxUpdateTime;
		this.firstUpdateTime = firstUpdateTime;
		this.maxGameTime = maxGameTime;
		this.tilesFilled = tilesFilled;
		this.gameName=gameName;
		this.gameType=gameType;
	}




	@ApiResourceProperty(ignored = AnnotationBoolean.FALSE) 	
	public Long getWinner() {
		return winner;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 	
	public void setWinner(final Long winner) {
		this.winner = winner;
	}
	public Long getForfeitedBy() {
		return forfeitedBy;
	}
	public void setForfeitedBy(final Long forfeitedBy) {
		this.forfeitedBy = forfeitedBy;
	}
	public int getMoveNr() {
		return moveNr;
	}
	public void setMoveNr(final int moveNr) {
		this.moveNr = moveNr;
	}

	public Long getGameId() {
		return gameId;
	}
	public void setGameId(final Long gameId) {
		this.gameId = gameId;
	}

	public boolean getMoveCorrect() {
		return moveCorrect;
	}
	public void setMoveCorrect(final boolean moveCorrect) {
		this.moveCorrect = moveCorrect;
	}
	public int getPlace() {
		return place;
	}
	public void setPlace(final int place) {
		this.place = place;
	}
	public int getValue() {
		return value;
	}
	public void setValue(final int value) {
		this.value = value;
	}
	public List<Integer> getNewGrid() {
		return newGrid;
	}

	public void setNewGrid(final List<Integer> newGrid) {
		this.newGrid = newGrid;
	}

	public List<Long> getPlayers() {
		return players;
	}

	public void setPlayers(final List<Long> players) {
		this.players = players;
	}

	public List<Integer> getScore() {
		return score;
	}

	public void setScore(final List<Integer> score) {
		this.score = score;
	}

	public void setScore(final ArrayList<Integer> score) {
		this.score = score;
	}

	public Long getWhoMoved() {
		return whoMoved;
	}

	public void setWhoMoved(final Long whoMoved) {
		this.whoMoved = whoMoved;
	}

	public boolean getGameTimedOut() {
		return gameTimedOut;
	}

	public void setGameTimedOut(final boolean timedOut) {
		this.gameTimedOut = timedOut;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.FALSE) 	
	public Long getUpdateTime() {
		return updateTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 	
	public void setUpdateTime(final Long updateTime) {
		this.updateTime = updateTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.FALSE) 	
	public Long getMaxUpdateTime() {
		return maxUpdateTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 	
	public void setMaxUpdateTime(final Long maxUpdateTime) {
		this.maxUpdateTime = maxUpdateTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.FALSE) 	
	public Long getFirstUpdateTime() {
		return firstUpdateTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 	
	public void setFirstUpdateTime(final Long firstUpdateTime) {
		this.firstUpdateTime = firstUpdateTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.FALSE) 	
	public Long getMaxGameTime() {
		return maxGameTime;
	}
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 	
	public void setMaxGameTime(final Long maxGameTime) {
		this.maxGameTime = maxGameTime;
	}

	public Integer getTilesFilled() {
		return tilesFilled;
	}

	public void setTilesFilled(final Integer tilesFilled) {
		this.tilesFilled = tilesFilled;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getGameName(){
		return this.gameName;
	}


	public Integer getGameType() {
		return gameType;
	}


	public void setGameType(Integer gameType) {
		this.gameType = gameType;
	}


	public Long getPlayerTimedOut() {
		return playerTimedOut;
	}


	public void setPlayerTimedOut(Long playerTimedOut) {
		this.playerTimedOut = playerTimedOut;
	}



}
