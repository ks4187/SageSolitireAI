package aigames.sagesol;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestMCTSPlayer {

	public static void main(String[] args) throws IOException {
		
		FileWriter writer = new FileWriter("d:\\SageTrainingData.csv");
		for(int n=1; n<=1000; n++){
			System.out.println("******GAME NO. "+n+" *******");
			writer.append("\n");
			
			GameState startState = new GameState(true); // true says generate new game 
			MCTSplayer player = new MCTSplayer();
			int maxPoints = 0;
			List<GameTreeNode> maxOfMaxScorePath = new ArrayList<GameTreeNode>();
			for(int j=0;j<10;j++){  //Perform MCTS for multiple times
				List<GameTreeNode> maxScorePath = player.performMCTS(startState);
				GameState prevState = startState;
				//prevState.displayBoard();
				int points = 0;
				
				for(int i=1;i<maxScorePath.size();i++){
					GameState state = maxScorePath.get(i).getState();
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
				if(points>maxPoints){
					maxOfMaxScorePath.clear();
					maxOfMaxScorePath.addAll(maxScorePath);
					maxPoints=points;
				}
			}
			for(int i=1;i<maxOfMaxScorePath.size();i++){
				GameState state = maxOfMaxScorePath.get(i).getState();
				//state.displayBoard();
			}
			System.out.println("$$$$ Max Total Score"+maxPoints);
		
			prepareTrainData(maxOfMaxScorePath,maxPoints,writer);
		}
		writer.flush();
		writer.close();
	}

	private static void prepareTrainData(List<GameTreeNode> maxOfMaxScorePath, int maxPoints, FileWriter writer) throws IOException {
		
		
		for(int k=0;k<maxOfMaxScorePath.size()-1;k++){
			writer.append("\n");
			GameState state = maxOfMaxScorePath.get(k).getState();
			
			HashMap<Integer,Integer> cardNoMap = new HashMap<Integer,Integer>(); 
			cardNoMap.put(1, 4);cardNoMap.put(2, 4);cardNoMap.put(3, 4);cardNoMap.put(4, 4);
			cardNoMap.put(5, 4);cardNoMap.put(6, 4);cardNoMap.put(7, 4);cardNoMap.put(8, 4);
			cardNoMap.put(9, 4);cardNoMap.put(10, 4);cardNoMap.put(11, 4);cardNoMap.put(12, 4);cardNoMap.put(13, 4);
			HashMap<Integer,Integer> suitMap = new HashMap<Integer,Integer>();
			suitMap.put(0, 13); //spade
			suitMap.put(1, 13);	//DIAMONDS
			suitMap.put(2, 13);	//CLUBS
			suitMap.put(3, 13); //HEARTS
			
			for(int i=0;i<3;i++){ //visible card suit and card number seperated
				for(int j=0;j<3;j++){
					int suit = -1;//no card
					int cardNo = 0;//no card
					if(!state.getDeckCards()[i][j].isEmpty()){
						suit = state.getDeckCards()[i][j].peek()/100;
						cardNo = state.getDeckCards()[i][j].peek()%100;
						
						suitMap.put(suit,suitMap.get(suit)-1);
						cardNoMap.put(cardNo, cardNoMap.get(cardNo)-1);
					}
					int remainNoCards = state.getDeckCards()[i][j].size();
					writer.append(Integer.toString(suit)+',');
					writer.append(Integer.toString(cardNo)+',');
					writer.append(Integer.toString(remainNoCards)+',');
				}
			}
			int bonusSuit = state.getBonusCard()/100;//bonus suit
			writer.append(Integer.toString(bonusSuit)+',');
			int trashes = state.getRemainingTrashes();
			writer.append(Integer.toString(trashes)+',');
			findRemEachCardNoBehind(state.getRemovedCards(),cardNoMap);
			findRemEachSuitBehind(state.getRemovedCards(),suitMap);
			for(int i=0;i<4;i++){//suits
				Integer remCardsInSuit = suitMap.get(i);
				writer.append(Integer.toString(remCardsInSuit)+',');
			}
			for(int i=1;i<=13;i++){//card number
				Integer remCardsWithNum = cardNoMap.get(i);
				writer.append(Integer.toString(remCardsWithNum)+',');
			}
			
			GameState nextState = maxOfMaxScorePath.get(k+1).getState();
			for(int i=0;i<3;i++){
				for(int j=0;j<3;j++){
					if(state.getDeckCards()[i][j].isEmpty()){
						writer.append("0,"); //no change will be there in next state as nothings there in the pos i j in previous state						
					}else if(nextState.getDeckCards()[i][j].isEmpty()){
						writer.append("1,"); // definite change - prev state was not empty but this state is at i j
					}else if(state.getDeckCards()[i][j].peek() == nextState.getDeckCards()[i][j].peek()){
						writer.append("0,");
					}else{
						writer.append("1,");
					}
				}
			}
		}
	}

	private static void findRemEachCardNoBehind(List<Integer> removedCards, HashMap<Integer, Integer> cardNoMap) {
		
		
		for(int i=0;i<removedCards.size();i++){
			int cardNo = removedCards.get(i)%100;
			cardNoMap.put(cardNo, cardNoMap.get(cardNo)-1);
		}
		return;
	}

	private static void findRemEachSuitBehind(List<Integer> removedCards, HashMap<Integer,Integer> suitMap) {
		
		
		for(int i=0;i<removedCards.size();i++){
			int suit = removedCards.get(i)/100;
			suitMap.put(suit, suitMap.get(suit)-1);
		}
		return;
	}

}
