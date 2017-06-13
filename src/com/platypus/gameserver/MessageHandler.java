package com.platypus.gameserver;

import static com.platypus.gameserver.MyObjectifyService.ofy;

import java.io.IOException;
import java.util.logging.Logger;

import com.games.common.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.ObjectifyService;

public class MessageHandler {
	private static final Logger log = Logger.getLogger(GameServerEndpoint.class.getName());

	public Result getMessageForPeerAndSend(final MessageGCM carrier, final User user) {
		ObjectifyService.register(UserProfile.class);
		UserProfile profile=null;
		try{
			profile = ofy().load().type(UserProfile.class).id(carrier.publicKey).now();
		} catch(NullPointerException e){
			log.warning("could not retrieve user, nullpointexception");
		}
		if(profile==null) throw new NullPointerException("Profile of receivers id not found in appengine");
		final Message message = new Message.Builder().addData("message",carrier.message).build();
		return sendMessageByGCM(message, profile);
	}

	private Result sendMessageByGCM(final Message message, final UserProfile profile) {
		final Sender sender = new Sender(Constants.GCM.GCM_API_KEY);
		Result result=null;
		try {
			result = sender.send(message, profile.gcmId, 5);
			log.warning("gcmId: "+ profile.gcmId);
			log.warning("Result: "+ result.toString());
		} catch (final IOException e) {
			log.warning("Could not send GCM message");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
