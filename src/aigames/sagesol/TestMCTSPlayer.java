package aigames.sagesol;

import java.util.List;

public class TestMCTSPlayer {

	public static void main(String[] args) {
		GameState startState = new GameState(true); // true says generate new game 
		MCTSplayer player = new MCTSplayer();
		List<GameState> maxScorePath = player.performMCTS(startState);
		for(GameState state : maxScorePath){
			state.displayBoard();
		}
	}

}
