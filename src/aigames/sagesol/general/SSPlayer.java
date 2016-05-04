package aigames.sagesol.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class SSPlayer {
	
	private static final int THREEKIND = 30;
	private static final int THREESTRAIGHT = 20;
	private static final int PAIRPOINTS = 10;
	private static final int FOURKIND = 100;
	private static final int STRAIGHTFLUSH = 150;
	private static final int FLUSH = 90;
	private static final int FIVESTRAIGHT = 50;
	private static final int FULLHOUSE = 70;
	
	public void getNextStates(GameState presentState, List<GameState> nextStates, HashMap<GameState,Integer> nextStateWithPoints){
		
		ArrayList<GameState> nextStatesCopy = new ArrayList<GameState>();
		nextStatesCopy.addAll(nextStates);
		nextStates.removeAll(nextStatesCopy);
		nextStateWithPoints.keySet().removeAll(nextStateWithPoints.keySet());
		
		getNextWhenFiveRemoved(presentState, nextStateWithPoints,nextStates);
		
		getNextWhenFourRemoved(presentState, nextStateWithPoints,nextStates);
		
		getNextWhenThreeRemoved(presentState, nextStateWithPoints,nextStates);
		
		getNextWhenPaired(presentState, nextStateWithPoints,nextStates);
		
		getNextWhenTrashed(presentState, nextStateWithPoints,nextStates);
		
	}

	
	private void getNextWhenFiveRemoved(GameState presentState, HashMap<GameState, Integer> nextStateWithPoints, List<GameState> nextStates) {

		
		for(int v=0; v<5; v++){
			for(int w=v+1; w<6; w++){
				for(int x=w+1; x<7; x++){
					for(int y=x+1; y<8; y++){
						for(int z=y+1; z<9; z++){
							GameState nState;
							Stack<Integer>[][] cardsDeck = presentState.copyDeckCards();
							int i1 = v/3;int i2 = w/3;int i3 = x/3;int i4 = y/3;int i5 = z/3;
							int j1 = v%3;int j2 = w%3;int j3 = x%3;int j4 = y%3;int j5 = z%3;
							if (!cardsDeck[i1][j1].isEmpty() && !cardsDeck[i2][j2].isEmpty()
									&& !cardsDeck[i3][j3].isEmpty() && !cardsDeck[i4][j4].isEmpty()
									&& !cardsDeck[i5][j5].isEmpty()) {
								boolean fullHouse = false;
								boolean fiveStraight = false;
								boolean flush = false;
								boolean StraightFlush = false;
								Integer card1 = cardsDeck[i1][j1].peek();
								Integer card2 = cardsDeck[i2][j2].peek();
								Integer card3 = cardsDeck[i3][j3].peek();
								Integer card4 = cardsDeck[i4][j4].peek();
								Integer card5 = cardsDeck[i5][j5].peek();
								if(checkFullHouse(card1%100,card2%100,card3%100,card4%100,card5%100)){
									fullHouse = true;
								}else if(consecutive(card1%100,card2%100,card3%100,card4%100,card5%100)){
									fiveStraight = true;
								}
								if((card1/100 == card2/100 && card2/100 == card3/100 && card3/100 == card4/100 && card4/100 == card5/100)){
									flush = true;
								}
								if(flush && fiveStraight){
									StraightFlush = true;
								}
								if(fullHouse || fiveStraight || flush || StraightFlush){
									cardsDeck[i1][j1].pop();
									cardsDeck[i2][j2].pop();
									cardsDeck[i3][j3].pop();
									cardsDeck[i4][j4].pop();
									cardsDeck[i5][j5].pop();
									List<Integer> removedCards = new ArrayList<Integer>();
									removedCards.add(card1);
									removedCards.add(card2);
									removedCards.add(card3);
									removedCards.add(card4);
									removedCards.add(card5);
									
									nState = new GameState(false);
									nState.setDeckCards(cardsDeck);
									nState.setRemovedCards(presentState.getRemovedCards());
									nState.setBonusCard(presentState.getBonusCard());
									nState.addToRemovedCards(removedCards);
									
									int points = 0;
									if(StraightFlush){
										points = STRAIGHTFLUSH;
									}else if(flush){
										points = FLUSH;
									}else if(fiveStraight){
										points = FIVESTRAIGHT;
									}else points = FULLHOUSE;
									
									int bonusSuit = presentState.getBonusCard()/100;
									if ((bonusSuit == card1 / 100) || (bonusSuit == card2 / 100)
											|| (bonusSuit == card3 / 100) || (bonusSuit == card4 / 100)
											|| (bonusSuit == card5 / 100)) {
										points *= 2;
									}
									if(cardsDeck[i1][j1].isEmpty()){
										points+=(3-i1)*50; //points for clearing a deck
									}
									if(cardsDeck[i2][j2].isEmpty()){
										points+=(3-i2)*50; //points for clearing a deck
									}
									if(cardsDeck[i3][j3].isEmpty()){
										points+=(3-i3)*50; //points for clearing a deck
									}
									if(cardsDeck[i4][j4].isEmpty()){
										points+=(3-i4)*50; //points for clearing a deck
									}
									if(cardsDeck[i5][j5].isEmpty()){
										points+=(3-i5)*50; //points for clearing a deck
									}
									
									int remTrashes = presentState.getRemainingTrashes();
									nState.setRemainingTrashes(remTrashes);
									if(remTrashes < 2){
										nState.setRemainingTrashes(remTrashes+1);
									}
									nextStateWithPoints.put(nState, points);
									nextStates.add(nState);
								}
							}
						
						}
					}
				}
			}
		}
	}

	private boolean consecutive(Integer a, Integer b, Integer c, Integer d, Integer e) {
		int min = Math.min(e, Math.min(d, Math.min(a, Math.min(b, c))));
	    int max = Math.max(e, Math.max(d, Math.max(a, Math.max(b, c))));
	    return max - min == 4 && !(isAnyEqual(a,b,c,d,e));
	}

	private boolean isAnyEqual(Integer a, Integer b, Integer c, Integer d, Integer e) {
		int[] list = {a,b,c,d,e};
		for(int i=0; i<4; i++){
			for(int j=i+1; j<5; j++){
				if(list[i] == list[j]){
					return true;
				}
			}
		}
	return false;
	}

	private boolean checkFullHouse(Integer a, Integer b, Integer c, Integer d, Integer e) {
		int[] list = {a,b,c,d,e};
		int count = 0;
		for(int i=0; i<4; i++){
			for(int j=i+1; j<5; j++){
				if(list[i] == list[j]){
					count++;
				}
			}
		}
		if(count == 4){  //eg 6 7 7 6 6 are cards 2 + 1 + 1
			return true;
		}
		return false;
	}

	private void getNextWhenFourRemoved(GameState presentState, HashMap<GameState, Integer> nextStateWithPoints, List<GameState> nextStates) {
		
		for (int w = 0; w < 6; w++) {
			for (int x = w + 1; x < 7; x++) {
				for (int y = x + 1; y < 8; y++) {
					for (int z = y + 1; z < 9; z++) {
						GameState nState;
						Stack<Integer>[][] cardsDeck = presentState.copyDeckCards();
						int i1 = w / 3;int i2 = x / 3;int i3 = y / 3;int i4 = z / 3;
						int j1 = w % 3;int j2 = x % 3;int j3 = y % 3;int j4 = z % 3;
						if (!cardsDeck[i1][j1].isEmpty() && !cardsDeck[i2][j2].isEmpty()
								&& !cardsDeck[i3][j3].isEmpty() && !cardsDeck[i4][j4].isEmpty()) {
							Integer card1 = cardsDeck[i1][j1].peek();
							Integer card2 = cardsDeck[i2][j2].peek();
							Integer card3 = cardsDeck[i3][j3].peek();
							Integer card4 = cardsDeck[i4][j4].peek();
							if (card1 % 100 == card2 % 100 && card2 % 100 == card3 % 100 && card3 % 100 == card4 % 100) {
								//Four of a kind
								cardsDeck[i1][j1].pop();
								cardsDeck[i2][j2].pop();
								cardsDeck[i3][j3].pop();
								cardsDeck[i4][j4].pop();
								List<Integer> removedCards = new ArrayList<Integer>();
								removedCards.add(card1);
								removedCards.add(card2);
								removedCards.add(card3);
								removedCards.add(card4);

								nState = new GameState(false);
								nState.setDeckCards(cardsDeck);
								nState.setRemovedCards(presentState.getRemovedCards());
								nState.setBonusCard(presentState.getBonusCard());
								nState.addToRemovedCards(removedCards);

								int points = FOURKIND * 2; //bonus suit will be there
								
								if (cardsDeck[i1][j1].isEmpty()) {
									points += (3 - i1) * 50; // points for clearing a deck
								}
								if (cardsDeck[i2][j2].isEmpty()) {
									points += (3 - i2) * 50; // points for clearing a deck
								}
								if (cardsDeck[i3][j3].isEmpty()) {
									points += (3 - i3) * 50; // points for clearing a deck
								}
								if (cardsDeck[i4][j4].isEmpty()) {
									points += (3 - i4) * 50; // points for clearing a deck
								}
								
								int remTrashes = presentState.getRemainingTrashes();
								nState.setRemainingTrashes(remTrashes);
								if(remTrashes < 2){
									nState.setRemainingTrashes(remTrashes+1);
								}
								nextStateWithPoints.put(nState, points);
								nextStates.add(nState);
							}
						}
					}
				}
			}
		}

	}

	private void getNextWhenThreeRemoved(GameState presentState, HashMap<GameState, Integer> nextStateWithPoints, List<GameState> nextStates) {
		
		for(int x=0; x<7; x++){
			for(int y=x+1; y<8; y++){
				for(int z=y+1; z<9; z++){
					GameState nState;
					Stack<Integer>[][] cardsDeck = presentState.copyDeckCards();
					int i1 = x/3;int i2 = y/3;int i3 = z/3;
					int j1 = x%3;int j2 = y%3;int j3 = z%3;
					if(!(i1==i2 && i2==i3)){ //checking if not in the same row
						if(!cardsDeck[i1][j1].isEmpty() && !cardsDeck[i2][j2].isEmpty() && !cardsDeck[i3][j3].isEmpty()){
							boolean threeKind = false;
							boolean threeStraight = false;
							Integer card1 = cardsDeck[i1][j1].peek();
							Integer card2 = cardsDeck[i2][j2].peek();
							Integer card3 = cardsDeck[i3][j3].peek();
							if(card1%100 == card2%100 && card2%100 == card3%100){
								threeKind = true;
							}else if(consecutive(card1%100,card2%100,card3%100)){
								threeStraight = true;
							}
							if(threeKind || threeStraight){
								cardsDeck[i1][j1].pop();
								cardsDeck[i2][j2].pop();
								cardsDeck[i3][j3].pop();
								List<Integer> removedCards = new ArrayList<Integer>();
								removedCards.add(card1);
								removedCards.add(card2);
								removedCards.add(card3);
								
								nState = new GameState(false);
								nState.setDeckCards(cardsDeck);
								nState.setRemovedCards(presentState.getRemovedCards());
								nState.setBonusCard(presentState.getBonusCard());
								nState.addToRemovedCards(removedCards);
								
								int points = 0;
								if(threeKind){
									points = THREEKIND;
								}else{
									points = THREESTRAIGHT;
								}
								
								int bonusSuit = presentState.getBonusCard()/100;
								if((bonusSuit == card1/100) || (bonusSuit == card2/100) || (bonusSuit == card3/100)){
									points*=2;
								}
								if(cardsDeck[i1][j1].isEmpty()){
									points+=(3-i1)*50; //points for clearing a deck
								}
								if(cardsDeck[i2][j2].isEmpty()){
									points+=(3-i2)*50; //points for clearing a deck
								}
								if(cardsDeck[i3][j3].isEmpty()){
									points+=(3-i3)*50; //points for clearing a deck
								}
								
								int remTrashes = presentState.getRemainingTrashes();
								nState.setRemainingTrashes(remTrashes);
								if(remTrashes < 2){
									nState.setRemainingTrashes(remTrashes+1);
								}
								nextStateWithPoints.put(nState, points);
								nextStates.add(nState);
							}
						}
					}
				}
			}
		}
		
	}

	private boolean consecutive(Integer a, Integer b, Integer c) {
		int min = Math.min(a, Math.min(b, c));
	    int max = Math.max(a, Math.max(b, c));
	    return max - min == 2 && a != b && a != c && b != c;
	}

	/**
	 * removing pairs
	 * @param nextStates 
	 */
	private void getNextWhenPaired(GameState presentState, HashMap<GameState, Integer> nextStateWithPoints, List<GameState> nextStates){
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						GameState nState;
						Stack<Integer>[][] cardsDeck = presentState.copyDeckCards();
						if (x != i || y != j && x!=i) { //checking if its not same cards and not in the same row
							if (!cardsDeck[i][j].isEmpty() && !cardsDeck[x][y].isEmpty()) {
								if(cardsDeck[i][j].peek()%100 == cardsDeck[x][y].peek()%100){ //same card value different suit
									Integer removed1 = cardsDeck[i][j].pop();
									Integer removed2 = cardsDeck[x][y].pop();
									List<Integer> removedCards = new ArrayList<Integer>();
									removedCards.add(removed1);
									removedCards.add(removed2);
									
									nState = new GameState(false);
									nState.setDeckCards(cardsDeck);
									nState.setRemovedCards(presentState.getRemovedCards());
									nState.setBonusCard(presentState.getBonusCard());
									nState.addToRemovedCards(removedCards);
								
									int points = PAIRPOINTS;
									int bonusSuit = presentState.getBonusCard()/100;
									if((bonusSuit == removed1/100) || (bonusSuit == removed2/100)){
										points*=2;
									}
									if(cardsDeck[i][j].isEmpty()){
										points+=(3-i)*50; //points for clearing a deck
									}
									if(cardsDeck[x][y].isEmpty()){
										points+=(3-x)*50; //points for clearing a deck
									}
									
									int remTrashes = presentState.getRemainingTrashes();
									nState.setRemainingTrashes(remTrashes);
									if(remTrashes < 2){
										nState.setRemainingTrashes(remTrashes+1);
									}
									nextStateWithPoints.put(nState, points);
									nextStates.add(nState);
								}
							}
						}
					}
				}
			}
		}	
	}
	

	/*
	 * trashing every card at each of the 9 deck
	 * */
	private void getNextWhenTrashed(GameState presentState, HashMap<GameState, Integer> nextStateWithPoints, List<GameState> nextStates) {
		
		for(int i=0; i<3 ; i++){
			for(int j=0; j<3; j++){
				Stack<Integer>[][] cardsDeck;
				cardsDeck = presentState.copyDeckCards();
				int points = 0;
				if(!cardsDeck[i][j].isEmpty()){
					GameState nState = new GameState(false);
					List<Integer> removedCards = new ArrayList<Integer>();
					int cardToTrash = cardsDeck[i][j].pop();
					removedCards.add(cardToTrash);
					
					nState.setDeckCards(cardsDeck);
					nState.setRemovedCards(presentState.getRemovedCards());
					nState.setBonusCard(presentState.getBonusCard());
					nState.addToRemovedCards(removedCards);
					
					//if cards at a particular deck is cleared
					if(cardsDeck[i][j].isEmpty()){
						if(i == 0){
							points = 150;
						}else if(i == 1){
							points = 100;
						}else points = 50;
					}
					
					int remTrashes = presentState.getRemainingTrashes();
					if(remTrashes != 0){
						nState.setRemainingTrashes(remTrashes-1);
						nextStateWithPoints.put(nState, points);
						nextStates.add(nState);
					}
				}
			}
		}
	}
	

	
public HashMap<Integer,Integer> getActionPointsImpfState(int[] visState){
		
		ImperfectGameState state = new ImperfectGameState();
		state.setBonusSuit(visState[27]);
		state.setRemainingTrashes(visState[28]);
		int[][] deckCards;
		deckCards = computeDeckCards(visState);
		state.setDeckCards(deckCards);
		int[][] remBehind = computeRemBehind(visState);
		state.setRemBehind(remBehind);
		
		return getNextStatesImp(state);
	}

	private HashMap<Integer,Integer> getNextStatesImp(ImperfectGameState state) {
	
		HashMap<Integer,Integer> actionPoints = new HashMap<Integer,Integer>();
		getNFiveRemoved(state,actionPoints);
		getNFourRemoved(state,actionPoints);
		getNThreeRemoved(state,actionPoints);
		getNPairRemoved(state,actionPoints);
		getNTrashed(state,actionPoints);
		
		return actionPoints;
	}
	
	private void getNFiveRemoved(ImperfectGameState state, HashMap<Integer,Integer> actionPoints) {

		boolean[][] actions = new boolean[3][3];
		for(int v=0; v<5; v++){
			for(int w=v+1; w<6; w++){
				for(int x=w+1; x<7; x++){
					for(int y=x+1; y<8; y++){
						for(int z=y+1; z<9; z++){
							int[][] cardsDeck = state.getDeckCards();
							int i1 = v/3;int i2 = w/3;int i3 = x/3;int i4 = y/3;int i5 = z/3;
							int j1 = v%3;int j2 = w%3;int j3 = x%3;int j4 = y%3;int j5 = z%3;
							if (cardsDeck[i1][j1] != 0  && cardsDeck[i2][j2] !=0
									&& cardsDeck[i3][j3] !=0 && cardsDeck[i4][j4] !=0
									&& cardsDeck[i5][j5] !=0) {
								boolean fullHouse = false;
								boolean fiveStraight = false;
								boolean flush = false;
								boolean StraightFlush = false;
								Integer card1 = cardsDeck[i1][j1];
								Integer card2 = cardsDeck[i2][j2];
								Integer card3 = cardsDeck[i3][j3];
								Integer card4 = cardsDeck[i4][j4];
								Integer card5 = cardsDeck[i5][j5];
								if(checkFullHouse(card1%100,card2%100,card3%100,card4%100,card5%100)){
									fullHouse = true;
								}else if(consecutive(card1%100,card2%100,card3%100,card4%100,card5%100)){
									fiveStraight = true;
								}
								if((card1/100 == card2/100 && card2/100 == card3/100 && card3/100 == card4/100 && card4/100 == card5/100)){
									flush = true;
								}
								if(flush && fiveStraight){
									StraightFlush = true;
								}
								if(fullHouse || fiveStraight || flush || StraightFlush){
									for(int i=0;i<3;i++){
										for(int j=0;j<3;j++){
											if((i == i1 || i == i2 || i == i3 || i == i4 || i == i5) && 
													(j == j1 || j == j2 || j == j3 || j == j4 || j == j5)){
												actions[i][j] = true;
											}
										}
									}

									int points = 0;
									if(StraightFlush){
										points = STRAIGHTFLUSH;
									}else if(flush){
										points = FLUSH;
									}else if(fiveStraight){
										points = FIVESTRAIGHT;
									}else points = FULLHOUSE;
									
									int bonusSuit = state.getBonusSuit();
									if ((bonusSuit == card1 / 100) || (bonusSuit == card2 / 100)
											|| (bonusSuit == card3 / 100) || (bonusSuit == card4 / 100)
											|| (bonusSuit == card5 / 100)) {
										points *= 2;
									}
									if(state.getRemBehind()[i1][j1] == 0){
										points+=(3-i1)*50; //points for clearing a deck
									}
									if(state.getRemBehind()[i2][j2] == 0){
										points+=(3-i2)*50; //points for clearing a deck
									}
									if(state.getRemBehind()[i3][j3] == 0){
										points+=(3-i3)*50; //points for clearing a deck
									}
									if(state.getRemBehind()[i4][j4] == 0){
										points+=(3-i4)*50; //points for clearing a deck
									}
									if(state.getRemBehind()[i5][j5] == 0){
										points+=(3-i5)*50; //points for clearing a deck
									}
									int actionsDec = calcActDec(actions);
									actionPoints.put(actionsDec, points);
								}
							}
						
						}
					}
				}
			}
		}
	}

	private int calcActDec(boolean[][] actions) {
		int actions1d[] = new int[9];
		int k = 0;
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				if(actions[i][j]){
					actions1d[k] = 1;
				}
				k++;
			}
		}
		int actionsDec = 0;
		for(int i=0;i<9;i++){
			actionsDec += actions1d[8-i]*(2^(8-i));
		}
		return actionsDec;
	}

private void getNTrashed(ImperfectGameState state, HashMap<Integer, Integer> actionPoints) {
		
		boolean[][] actions = new boolean[3][3];
		for(int i=0; i<3 ; i++){
			for(int j=0; j<3; j++){
				int[][] cardsDeck = state.getDeckCards();
				int points = 0;
				if(cardsDeck[i][j] !=0){
					
					int cardToTrash = cardsDeck[i][j];
					
					actions[i][j] = true;
					
					//if cards at a particular deck is cleared
					if(state.getRemBehind()[i][j] == 0){
						if(i == 0){
							points = 150;
						}else if(i == 1){
							points = 100;
						}else points = 50;
					}
					
					int remTrashes = state.getRemainingTrashes();
					if(remTrashes != 0){
						int actionsDec = calcActDec(actions);
						actionPoints.put(actionsDec, points);
					}
				}
			}
		}
	}

	private void getNPairRemoved(ImperfectGameState state, HashMap<Integer, Integer> actionPoints) {
		
		boolean[][] actions = new boolean[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				for (int x = 0; x < 3; x++) {
					for (int y = 0; y < 3; y++) {
						GameState nState;
						int[][] cardsDeck = state.getDeckCards();
						if (x != i || y != j && x!=i) { //checking if its not same cards and not in the same row
							if (cardsDeck[i][j] != 0  && cardsDeck[x][y] !=0) {
								if(cardsDeck[i][j]%100 == cardsDeck[x][y]%100){ //same card value different suit
									Integer removed1 = cardsDeck[i][j];
									Integer removed2 = cardsDeck[x][y];
									List<Integer> removedCards = new ArrayList<Integer>();
									removedCards.add(removed1);
									removedCards.add(removed2);
									for(int m=0;m<3;m++){
										for(int n=0;n<3;n++){
											if((m == i || m == x) && 
													(n == j || n == y)){
												actions[i][j] = true;
											}
										}
									}
									
									int points = PAIRPOINTS;
									int bonusSuit = state.getBonusSuit();
									if((bonusSuit == removed1/100) || (bonusSuit == removed2/100)){
										points*=2;
									}
									if(state.getRemBehind()[i][j] == 0){
										points+=(3-i)*50; //points for clearing a deck
									}
									if(state.getRemBehind()[x][y] == 0){
										points+=(3-x)*50; //points for clearing a deck
									}
									
									int actionsDec = calcActDec(actions);
									actionPoints.put(actionsDec, points);
								}
							}
						}
					}
				}
			}
		}	
	}

	private void getNThreeRemoved(ImperfectGameState state, HashMap<Integer, Integer> actionPoints) {
		
		boolean[][] actions = new boolean[3][3];
		for(int x=0; x<7; x++){
			for(int y=x+1; y<8; y++){
				for(int z=y+1; z<9; z++){
					GameState nState;
					int[][] cardsDeck = state.getDeckCards();
					int i1 = x/3;int i2 = y/3;int i3 = z/3;
					int j1 = x%3;int j2 = y%3;int j3 = z%3;
					if(!(i1==i2 && i2==i3)){ //checking if not in the same row
						if(cardsDeck[i1][j1] != 0  && cardsDeck[i2][j2] !=0
								&& cardsDeck[i3][j3] !=0){
							boolean threeKind = false;
							boolean threeStraight = false;
							Integer card1 = cardsDeck[i1][j1];
							Integer card2 = cardsDeck[i2][j2];
							Integer card3 = cardsDeck[i3][j3];
							if(card1%100 == card2%100 && card2%100 == card3%100){
								threeKind = true;
							}else if(consecutive(card1%100,card2%100,card3%100)){
								threeStraight = true;
							}
							if(threeKind || threeStraight){
								for(int i=0;i<3;i++){
									for(int j=0;j<3;j++){
										if((i == i1 || i == i2 || i == i3) && 
												(j == j1 || j == j2 || j == j3)){
											actions[i][j] = true;
										}
									}
								}
								
								int points = 0;
								if(threeKind){
									points = THREEKIND;
								}else{
									points = THREESTRAIGHT;
								}
								
								int bonusSuit = state.getBonusSuit();
								if((bonusSuit == card1/100) || (bonusSuit == card2/100) || (bonusSuit == card3/100)){
									points*=2;
								}
								if(state.getRemBehind()[i1][j1] == 0){
									points+=(3-i1)*50; //points for clearing a deck
								}
								if(state.getRemBehind()[i2][j2] == 0){
									points+=(3-i2)*50; //points for clearing a deck
								}
								if(state.getRemBehind()[i3][j3] == 0){
									points+=(3-i3)*50; //points for clearing a deck
								}
								
								int actionsDec = calcActDec(actions);
								actionPoints.put(actionsDec, points);
							}
						}
					}
				}
			}
		}
		
	}

	private void getNFourRemoved(ImperfectGameState state, HashMap<Integer, Integer> actionPoints) {
		
		boolean[][] actions = new boolean[3][3];
		for (int w = 0; w < 6; w++) {
			for (int x = w + 1; x < 7; x++) {
				for (int y = x + 1; y < 8; y++) {
					for (int z = y + 1; z < 9; z++) {
						int[][] cardsDeck = state.getDeckCards();
						int i1 = w / 3;int i2 = x / 3;int i3 = y / 3;int i4 = z / 3;
						int j1 = w % 3;int j2 = x % 3;int j3 = y % 3;int j4 = z % 3;
						if (cardsDeck[i1][j1] != 0  && cardsDeck[i2][j2] !=0
								&& cardsDeck[i3][j3] !=0 && cardsDeck[i4][j4] !=0) {
							Integer card1 = cardsDeck[i1][j1];
							Integer card2 = cardsDeck[i2][j2];
							Integer card3 = cardsDeck[i3][j3];
							Integer card4 = cardsDeck[i4][j4];
							if (card1 % 100 == card2 % 100 && card2 % 100 == card3 % 100 && card3 % 100 == card4 % 100) {
								//Four of a kind
								for(int i=0;i<3;i++){
									for(int j=0;j<3;j++){
										if((i == i1 || i == i2 || i == i3 || i == i4) && 
												(j == j1 || j == j2 || j == j3 || j == j4)){
											actions[i][j] = true;
										}
									}
								}

								int points = FOURKIND * 2; //bonus suit will be there
								
								if (state.getRemBehind()[i1][j1] == 0) {
									points += (3 - i1) * 50; // points for clearing a deck
								}
								if (state.getRemBehind()[i2][j2] == 0) {
									points += (3 - i2) * 50; // points for clearing a deck
								}
								if (state.getRemBehind()[i3][j3] == 0) {
									points += (3 - i3) * 50; // points for clearing a deck
								}
								if (state.getRemBehind()[i4][j4] == 0) {
									points += (3 - i4) * 50; // points for clearing a deck
								}
								
								int actionsDec = calcActDec(actions);
								actionPoints.put(actionsDec, points);
							}
						}
					}
				}
			}
		}

	}

		

	


	private int[][] computeRemBehind(int[] visState) {
		
		int[][] remBehind = new int[3][3];
		int[] rem = new int[9];
		for(int i=0; i<27;){
			int suit = visState[i++];
			int cardNo = visState[i++];
			int remCards = visState[i++];
			if(suit < 0){
				rem[i-3] = -1;
			}else{
				rem[i-3] = remCards;
			}
		}
		for(int i=0;i<9;i++){
			remBehind[i/3][i%3] = rem[i];
		}
		return remBehind;
	}

	private int[][] computeDeckCards(int[] visState) {
		
		int[][] deckCards = new int[3][3];
		int[] card = new int[9];
		for(int i=0; i<27;i++){
			int suit = visState[i++];
			int cardNo = visState[i++];
			if(suit < 0){
				card[i-3] = 0;
			}else{
				card[i-3] = suit*100+cardNo;
			}
		}
		for(int i=0;i<9;i++){
			deckCards[i/3][i%3] = card[i];
		}
		return deckCards;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



}
