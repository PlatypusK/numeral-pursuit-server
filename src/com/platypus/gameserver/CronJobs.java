package com.platypus.gameserver;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class CronJobs extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5660671868431805017L;

	@Override
	public void doGet(final HttpServletRequest req, final HttpServletResponse resp){
		testActiveGames();

		MatchMaker.createRandomNontimedGame();
	}
	private void testActiveGames() {
		ActiveGames games=ActiveGames.loadActiveGamesClass();
		games.testAllActiveGames();
		games.saveIfDirty();		
	}
	@Override
	public void doPost(final HttpServletRequest req, final HttpServletResponse resp)
	throws ServletException, IOException {
	}
}
