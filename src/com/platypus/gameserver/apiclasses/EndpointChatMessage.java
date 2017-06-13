package com.platypus.gameserver.apiclasses;

public class EndpointChatMessage {
	Long gameId;
	Long receiverId;
	String message;
	Long senderId;
	

	public EndpointChatMessage(){
		
	}
	
	public EndpointChatMessage(final Long gameId, final Long receiverId, final String message) {
		this.gameId = gameId;
		this.receiverId = receiverId;
		this.message = message;
	}
	public Long getGameId() {
		return gameId;
	}
	public void setGameId(final Long gameId) {
		this.gameId = gameId;
	}
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(final Long receiverId) {
		this.receiverId = receiverId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(final String message) {
		this.message = message;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	
}
