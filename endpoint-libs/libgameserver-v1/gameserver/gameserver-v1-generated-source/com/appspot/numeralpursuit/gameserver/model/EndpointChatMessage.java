/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2013-12-19 23:55:21 UTC)
 * on 2014-01-25 at 01:51:37 UTC 
 * Modify at your own risk.
 */

package com.appspot.numeralpursuit.gameserver.model;

/**
 * Model definition for EndpointChatMessage.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the gameserver. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class EndpointChatMessage extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long gameId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String message;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long receiverId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long senderId;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getGameId() {
    return gameId;
  }

  /**
   * @param gameId gameId or {@code null} for none
   */
  public EndpointChatMessage setGameId(java.lang.Long gameId) {
    this.gameId = gameId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMessage() {
    return message;
  }

  /**
   * @param message message or {@code null} for none
   */
  public EndpointChatMessage setMessage(java.lang.String message) {
    this.message = message;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getReceiverId() {
    return receiverId;
  }

  /**
   * @param receiverId receiverId or {@code null} for none
   */
  public EndpointChatMessage setReceiverId(java.lang.Long receiverId) {
    this.receiverId = receiverId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getSenderId() {
    return senderId;
  }

  /**
   * @param senderId senderId or {@code null} for none
   */
  public EndpointChatMessage setSenderId(java.lang.Long senderId) {
    this.senderId = senderId;
    return this;
  }

  @Override
  public EndpointChatMessage set(String fieldName, Object value) {
    return (EndpointChatMessage) super.set(fieldName, value);
  }

  @Override
  public EndpointChatMessage clone() {
    return (EndpointChatMessage) super.clone();
  }

}
