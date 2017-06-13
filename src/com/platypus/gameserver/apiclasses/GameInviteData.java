package com.platypus.gameserver.apiclasses;

public class GameInviteData {
	private Long senderId;
	private Long receiverId;
	private Long maxTimeToAnswer;
	
	public GameInviteData() {
	}
	public GameInviteData(Long senderId, Long receiverId, Long maxTimeToAnswer) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.maxTimeToAnswer = maxTimeToAnswer;
	}
	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}
	public Long getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}
	public Long getMaxTimeToAnswer() {
		return maxTimeToAnswer;
	}
	public void setMaxTimeToAnswer(Long maxTimeToAnswer) {
		this.maxTimeToAnswer = maxTimeToAnswer;
	}
	
}
