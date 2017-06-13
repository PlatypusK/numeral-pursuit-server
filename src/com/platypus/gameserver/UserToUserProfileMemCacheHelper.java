package com.platypus.gameserver;

import static com.platypus.gameserver.MyObjectifyService.ofy;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


/**
 * This is a class to save datastore accesses when finding the public user id from an appengine user object. Having this class with the 
 * userId as an objectify Id field makes it possible to fetch the id from memcache if the memcache is hot without going through the datastore. If this was not here you would have
 * to do a query in userprofile to match the userId with the publicUserId, which is not possible to do without accessing datastore. userId and publicUserId has a one to one relationship and
 * both are unique. At the time of writing this, the class uses the email as an id field due to a bug in the appengine-cloud-endpoint api which causes User.getUserId() to return null
 * for any user object. It would be advisable to fix this when the bug is corrected.
 * @author Ketil
 *
 */
@Entity
@Cache
public class UserToUserProfileMemCacheHelper {
	@Id String email;
	String userId;
	Long publicUserId;
	/**
	 * Keep compatibility with Objectify with this constructor
	 */
	public UserToUserProfileMemCacheHelper(){
	}
	public UserToUserProfileMemCacheHelper(User user, Long publicUserId) {
		this.email=user.getEmail();
		this.userId=user.getUserId();
		this.publicUserId=publicUserId;
	}
	public static final Long loadPublicUserId(String email){
		UserToUserProfileMemCacheHelper helper;
		helper=ofy().load().type(UserToUserProfileMemCacheHelper.class).id(email).now();
		return helper==null?null:helper.publicUserId;
	}
}
