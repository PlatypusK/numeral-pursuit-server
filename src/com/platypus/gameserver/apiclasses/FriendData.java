package com.platypus.gameserver.apiclasses;

public class FriendData {
	Long friendId;
	String friendNick;
	String friendEmail;
	public String getFriendEmail() {
		return friendEmail;
	}
	public void setFriendEmail(String friendEmail) {
		this.friendEmail = friendEmail;
	}
	public FriendData(){}
	public FriendData(Long friendId, String friendNick) {
		super();
		this.friendId = friendId;
		this.friendNick = friendNick;
	}
	public Long getFriendId() {
		return friendId;
	}
	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}
	public String getFriendNick() {
		return friendNick;
	}
	public void setFriendNick(String friendNick) {
		this.friendNick = friendNick;
	}


}
