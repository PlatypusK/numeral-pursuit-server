package com.platypus.gameserver;


/**Wire format class that contains a message to be sent over gcm from the server and a public key given to the client that identifies the user that the message
 * is meant for. 
 * @author Ketil
 *
 */
public class MessageGCM {
	/**The message to be sent*/
	public String message;
	/**The key that identifies the user that is supposed to receive the message*/
	public Long publicKey;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Long getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(Long publicKey) {
		this.publicKey = publicKey;
	}
	public static String getPublicKeyName() {
		return PUBLIC_KEY_NAME;
	}
	public static String getMessageName() {
		return MESSAGE_NAME;
	}
	/**Necessary to keep compatibility with API
	 * 
	 */
	public MessageGCM(){
	}
	public MessageGCM(final Long publicKey, final String message){
		this.publicKey=publicKey;
		this.message=message;
	}
	public static final String PUBLIC_KEY_NAME="publicKey";
	public static final String MESSAGE_NAME="message";
}
