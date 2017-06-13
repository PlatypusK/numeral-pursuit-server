package com.platypus.gameserver.apiclasses;

public class PublicUId {
	public Long uid;
	/**Necessary to keep compatibility with API
	 * 
	 */
	public PublicUId(){
	}
	public PublicUId(final Long uid){
		this.uid=uid;
	}
}
