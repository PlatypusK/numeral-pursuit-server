package com.platypus.gameserver;

import java.io.IOException;
import java.util.logging.Logger;

import com.games.common.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.platypus.gameserver.apiclasses.RegIdCGM;

public class GCMServices {
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());
	public static final String MESSAGE=Constants.GCM.MESSAGE;
	public static Message getMessage(final String messageText){
		return new Message.Builder().addData(MESSAGE, messageText).build();
	}
	public static final Result sendMessage(UserProfile player, final Message mes, final String gcmId, final int repeatTimes){
		Sender sender=new Sender(Constants.GCM.GCM_API_KEY);
		if(player.gcmId==null){
			addToPollableMessages(player,mes.toString());
			return null;
		}
		log.warning(mes.toString());
		Result result=null;
		try {
			result=sender.send(mes, gcmId, repeatTimes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.warning(result.getErrorCodeName());
		return result;
	}
	/**
	 * Use this method to add strings that could not be sent to a userprofile over gson. The client application should occasionally poll appengine to fetch the latest updates. Stores messages in memcache.
	 * 
	 * @param player
	 * @param gson
	 */
	public static void addToPollableMessages(UserProfile player, String gson) {
		
	}
	public static void saveGCMIdIfNew(RegIdCGM regid, UserProfile profile) {
		if(!regid.regId.equals(profile.gcmId)){
			profile.gcmId=regid.regId;
			profile.save();
		}
	}
}
