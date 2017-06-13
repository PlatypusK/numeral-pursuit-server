package com.platypus.gameserver.apiclasses;

public class GameUpdateRequest {
	private Long gameId;
	private Long userId;
	public GameUpdateRequest(){};
	public GameUpdateRequest(Long gameId, Long userId) {
		this.gameId = gameId;
		this.userId = userId;
	}

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
}
