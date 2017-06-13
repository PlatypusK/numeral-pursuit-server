package com.platypus.gameserver.apiclasses;

public class GameForfeiter {
	public Long gameId;
	public Long userId;
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public GameForfeiter(){
		
	}
}
