package com.platypus.gameserver;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.platypus.gameserver.apiclasses.GameUpdate;

public class MyObjectifyService {
    static {
        factory().register(UserProfile.class);
//        factory().register(Game.class);
        factory().register(UserToUserProfileMemCacheHelper.class);
//        factory().register(RunningGames.class);
        factory().register(GameUpdate.class);
        factory().register(ActiveGames.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
