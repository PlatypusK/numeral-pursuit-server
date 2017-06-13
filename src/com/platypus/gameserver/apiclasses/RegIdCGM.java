package com.platypus.gameserver.apiclasses;

/**Wire format class to transfer the gcm registration id from the user to the server
 * 
 * @author Ketil
 *
 */
public class RegIdCGM {
	public String regId;
	public RegIdCGM(){
	}
	public RegIdCGM(final String regId){
		this.regId=regId;
	}
	
}
