package com.platypus.gameserver.apiclasses;

import java.util.ArrayList;

public class GamesInfo {
	ArrayList<Long> gameIds=new ArrayList<Long>();
	ArrayList<String> gameNames=new ArrayList<String>();
	ArrayList<Long> opIds=new ArrayList<Long>();
	ArrayList<String> opNames=new ArrayList<String>();
	private ArrayList<Integer> gameType;
	public ArrayList<Integer> result; 
	public GamesInfo(){

	}


	public GamesInfo(ArrayList<Long> gameIds, ArrayList<String> gameNames,
			ArrayList<Long> opIds, ArrayList<String> opNames,
			ArrayList<Integer> gameType, ArrayList<Integer> result) {
		super();
		this.gameIds = gameIds;
		this.gameNames = gameNames;
		this.opIds = opIds;
		this.opNames = opNames;
		this.gameType = gameType;
		this.result = result;
	}


	public ArrayList<String> getGameNames() {
		return gameNames;
	}

	public void setGameNames(ArrayList<String> gameNames) {
		this.gameNames = gameNames;
	}

	public ArrayList<Long> getGameIds() {
		return gameIds;
	}

	public void setGameIds(final ArrayList<Long> gameIds) {
		this.gameIds = gameIds;
	}

	public ArrayList<Long> getOpIds() {
		return opIds;
	}

	public void setOpIds(final ArrayList<Long> opIds) {
		this.opIds = opIds;
	}

	public ArrayList<String> getOpNames() {
		return opNames;
	}

	public void setOpNames(final ArrayList<String> opNames) {
		this.opNames = opNames;
	}
	public ArrayList<Integer> getGameType() {
		return gameType;
	}
	public void setGameType(ArrayList<Integer> gameType) {
		this.gameType = gameType;
	}
	public ArrayList<Integer> getResult() {
		return result;
	}
	public void setResult(ArrayList<Integer> result) {
		this.result = result;
	}
	



}
