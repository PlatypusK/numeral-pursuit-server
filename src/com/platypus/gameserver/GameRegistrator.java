package com.platypus.gameserver;


import com.google.appengine.api.users.User;


public class GameRegistrator {

	public void registerForRandomGame(final User user, final UserProfile profile) {

		final MemCacheWorker cache=new MemCacheWorker();
		cache.putRandomRegisteredNoTime(profile);
	}
}
