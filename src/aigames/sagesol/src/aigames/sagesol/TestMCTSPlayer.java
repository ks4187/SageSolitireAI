package aigames.sagesol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestMCTSPlayer {

	public static void main(String[] args) {
		GameState startState = new GameState(true); // true says generate new game 
		MCTSplayer player = new MCTSplayer();
		List<GameState> maxScorePath = player.performMCTS(startState);
		GameState prevState = startState;
		prevState.displayBoard();
		Integer points = 0;
		for(int i=0;i<maxScorePath.size();i++){
			GameState state = maxScorePath.get(i);
			state.displayBoard();
		}
		
		for(int i=0;i<maxScorePath.size();i++){
			GameState state = maxScorePath.get(i);
			HashMap<GameState, Integer> nextStateWithPoints = new HashMap<GameState, Integer>();
			List<GameState> nextStates = new ArrayList<GameState>();
			player.getNextStates(prevState, nextStates, nextStateWithPoints);
			prevState = state;
			for(GameState nState : nextStates){
				if(state.sameAs(nState)){
					state = nState;
					break;
				}
			}
			points += nextStateWithPoints.get(state);
		}
		System.out.println("$$$$ Total Score"+points);
		
	}

}
