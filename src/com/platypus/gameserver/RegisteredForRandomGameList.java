package com.platypus.gameserver;

import java.util.ArrayList;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


@Entity
@Cache
public class RegisteredForRandomGameList {
	/**will never need more than one of these in the datastore/memcache*/
	@Id public long id;
	public ArrayList<Long> publicIds=new ArrayList<Long>();
	public static final Key<RegisteredForRandomGameList> key=Key.create(RegisteredForRandomGameList.class, 1l);

}
