package com.platypus.gameserver.apiclasses;

import java.io.Serializable;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Embed;
import com.googlecode.objectify.annotation.Ignore;

@Embed
public class Settings implements Serializable {
	private static final long serialVersionUID = -9092020488065107647L;
	Boolean notifyGameUpdate=true;
	Boolean notifyGameRequest=true;
	Boolean notifyGameChatUpdates=false;
	Boolean notifyFriendChatUpdates=true;
	String name;
	Float opacity=0.5f;
	@Ignore
	private Long userId;


	public Settings() {
	}

	public Boolean getNotifyGameUpdate() {
		return notifyGameUpdate;
	}

	public void setNotifyGameUpdate(Boolean notifyGameUpdate) {
		this.notifyGameUpdate = notifyGameUpdate;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Boolean getNotifyGameRequest() {
		return notifyGameRequest;
	}

	public void setNotifyGameRequest(Boolean notifyGameRequest) {
		this.notifyGameRequest = notifyGameRequest;
	}

	public Boolean getNotifyGameChatUpdates() {
		return notifyGameChatUpdates;
	}

	public void setNotifyGameChatUpdates(Boolean notifyGameChatUpdates) {
		this.notifyGameChatUpdates = notifyGameChatUpdates;
	}

	public Boolean getNotifyFriendChatUpdates() {
		return notifyFriendChatUpdates;
	}

	public void setNotifyFriendChatUpdates(Boolean notifyFriendChatUpdates) {
		this.notifyFriendChatUpdates = notifyFriendChatUpdates;
	}
	
	public Float getOpacity() {
		return opacity;
	}

	public void setOpacity(Float opacity) {
		this.opacity = opacity;
	}
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE) 	
	public boolean isChanged(Settings newSettings) {
		if(newSettings.notifyFriendChatUpdates!=null && !this.notifyFriendChatUpdates.equals(newSettings.getNotifyFriendChatUpdates())) return true;
		if(newSettings.notifyGameChatUpdates!=null && !this.notifyGameChatUpdates.equals(newSettings.getNotifyFriendChatUpdates())) return true;
		if(newSettings.notifyGameUpdate!=null && !this.notifyGameUpdate.equals(newSettings.getNotifyFriendChatUpdates())) return true;
		if(newSettings.notifyGameRequest!=null && !this.notifyGameRequest.equals(newSettings.getNotifyFriendChatUpdates())) return true;
		if(newSettings.notifyFriendChatUpdates!=null && !this.notifyFriendChatUpdates.equals(newSettings.getNotifyFriendChatUpdates())) return true;
		if(newSettings.opacity!=null && !this.opacity.equals(newSettings.getNotifyFriendChatUpdates())) return true;
		return false;
	}



}
