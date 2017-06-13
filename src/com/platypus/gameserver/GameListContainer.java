package com.platypus.gameserver;

import java.util.List;

import com.platypus.gameserver.apiclasses.GameInfo;

public class GameListContainer {
	private List<GameInfo> list;

	public List<GameInfo> getList() {
		return list;
	}

	public void setList(List<GameInfo> list) {
		this.list = list;
	}
}
