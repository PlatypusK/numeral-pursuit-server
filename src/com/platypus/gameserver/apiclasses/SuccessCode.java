package com.platypus.gameserver.apiclasses;

import static com.games.common.Constants.SuccessCodes.FRIEND_NOT_REGISTERED;
import static com.games.common.Constants.SuccessCodes.SUCCESSCODE_SUCCESS;


/**Contains constants to specify if everything went as expected during the method. Also specifies constants to say what went wrong if anything
 * 
 * @author Ketil
 *
 */
public class SuccessCode {
	public String result;
	/**Necessary to keep compatibility with API
	 * 
	 */
	public SuccessCode(){
	}
	public SuccessCode(final String result){
		this.result=result;
	}
	public static final String SUCCESS=SUCCESSCODE_SUCCESS;
	public static final String USER_NOT_REGISTERED = "User not registered";
	public static final SuccessCode WAITING_FOR_OTHER_PLAYER = new SuccessCode("waitingForOtherPlayer");
	public String getResult() {
		return result;
	}
	public void setResult(final String result) {
		this.result = result;
	}
	public static final SuccessCode SUCCESSFULL=new SuccessCode(SUCCESS);
	public static final SuccessCode CODE_FRIEND_NOT_REGISTERED = new SuccessCode(FRIEND_NOT_REGISTERED);
}
