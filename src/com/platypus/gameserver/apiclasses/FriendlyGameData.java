package com.platypus.gameserver.apiclasses;

public class FriendlyGameData {
	Long id1;
	Long id2;
	
	
	public FriendlyGameData() {
	}

	public FriendlyGameData(Long id1, Long id2) {
		super();
		this.id1 = id1;
		this.id2 = id2;
	}

	public Long getId1() {
		return id1;
	}

	public void setId1(Long id1) {
		this.id1 = id1;
	}

	public Long getId2() {
		return id2;
	}

	public void setId2(Long id2) {
		this.id2 = id2;
	}
	
}
