package com.platypus.gameserver.apiclasses;


public class GameInfo {
	private Long gameId;
	private String gameName;
	private Long opId;
	private String opNames;
	private Integer gameType;
	private Integer result;
	private Integer reasonEnded;
	public GameInfo() {
		super();
	}
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public Long getOpId() {
		return opId;
	}
	public void setOpId(Long opIds) {
		this.opId = opIds;
	}
	public String getOpNames() {
		return opNames;
	}
	public void setOpNames(String opNames) {
		this.opNames = opNames;
	}
	public Integer getGameType() {
		return gameType;
	}
	public void setGameType(Integer gameType) {
		this.gameType = gameType;
	}
	public Integer getResult() {
		return result;
	}
	public void setResult(Integer result) {
		this.result = result;
	}
	public Integer getReasonEnded() {
		return reasonEnded;
	}
	public void setReasonEnded(Integer reasonEnded) {
		this.reasonEnded = reasonEnded;
	}
	

}
