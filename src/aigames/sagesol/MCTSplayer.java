package aigames.sagesol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class MCTSplayer {
	
	private static final int THREEKIND = 30;
	private static final int THREESTRAIGHT = 20;
	private static final int PAIRPOINTS = 10;
	private static final int FOURKIND = 100;
	private static final int STRAIGHTFLUSH = 150;
	private static final int FLUSH = 90;
	private static final int FIVESTRAIGHT = 50;
	private static final int FULLHOUSE = 70;
	
	private static final int MCTSITERATIONS = 2000;
	private static final double Cp = 1/(Math.sqrt(2)); //;
	
	public List<GameTreeNode> performMCTS(GameState startState){
		
		ArrayList<GameTreeNode> maxScorePath = new ArrayList<GameTreeNode>();
		//maxScorePath.add(startState);
		int count = 0;
		int maxReward = 0;
		GameTree gameTree = new GameTree(startState);
		while(count < MCTSITERATIONS){
			GameTreeNode root = gameTree.getRoot();
			//GameTreeNode parent = gameTree.getRoot();
			int pntsThisIter = 0;
			ArrayList<GameTreeNode> path = new ArrayList<GameTreeNode>();
			GameTreeNode gameNode = selectNExpand(gameTree, pntsThisIter, path);
			if(gameNode != null){
				//gameNode.display();
				//parent.addToChildren(gameNode);
				//gameNode.setVisits(gameNode.getVisits()+1);
				int reward = defaultRun(gameNode,pntsThisIter,path);
				if(reward >= maxReward){
					maxReward = reward;
					maxScorePath.clear();
					maxScorePath.addAll(path);
					System.out.print("maxReward "+maxReward);System.out.println(" Iteration"+count);
				}
				//int reward = defaultRunAstar(gameNode, pntsThisIter);
				//System.out.println("reward  "+reward+"\n");
				setBackRewardsToAllInPath(gameTree,gameNode,reward);
			}
			count++;
		}
		GameTreeNode node = gameTree.getRoot();
		List<GameTreeNode> children = node.getChildren();
		ArrayList<GameState> maxScorePath2 = new ArrayList<GameState>();
		
		while(!children.isEmpty()){
			float maxBackReward = 0;
			GameTreeNode goodChild = null;
			for(GameTreeNode child : children){
				//System.out.println("removed "+child.getState().getRemovedCards());
				//System.out.println("backreward "+child.getBackReward());
				 if(child.getBackMaxReward() >= maxBackReward){
					 goodChild  = child; 
					 maxBackReward = child.getBackMaxReward();
				 }
			}
			//System.out.println("child selected from backreward");
			//goodChild.display();
			//System.out.println("max backreward "+goodChild.getBackReward());
			maxScorePath2.add(goodChild.getState());
			node = goodChild;
			children = node.getChildren();
		}
		GameState prevState = gameTree.getRoot().getState();
		int points = 0;
		for(int i=0;i<maxScorePath2.size();i++){
			GameState state = maxScorePath2.get(i);
			HashMap<GameState, Integer> nextStateWithPoints = new HashMap<GameState, Integer>();
			List<GameState> nextStates = new ArrayList<GameState>();
			
			getNextStates(prevState, nextStates, nextStateWithPoints);
			prevState = state;
			for(GameState nState : nextStates){
				if(state.sameAs(nState)){
					state = nState;
					break;
				}
			}
			points += nextStateWithPoints.get(state);
		}
		//System.out.println("MCTS Total Score"+points+"\n\n\n\n");
		
		/*node = gameTree.getRoot();
		children = node.getChildren();
		
		while(!children.isEmpty()){
			int maxVisits = 0;
			GameTreeNode goodChild = null;
			for(GameTreeNode child : children){
				 if(child.getVisits() > maxVisits){
					 goodChild  = child; 
					 maxVisits = child.getVisits();
				 }
			}
			maxScorePath.add(goodChild.getState());
			node = goodChild;
			children = node.getChildren();
		}*/
		
		return maxScorePath;
		
	}
	
	/*private GameTreeNode selection(GameTree gameTree, GameTreeNode parent, int pntsThisIter) {

		
		List<GameState> nextStates = new ArrayList<GameState>();
		GameTreeNode node = gameTree.getRoot();
		//HashMap<GameState,Integer> notFoundStates;
		GameState currState = node.getState();
		HashMap<GameState, Integer> nextStatesPoints = getNextStates(currState, nextStates);
		
		while((anyNotFoundNextInTree(node, nextStates, nextStatesPoints))){// no not yet explored game states
			node = treePolicy(node);
			HashMap<GameState, Integer> nextStatePoints = getNextStates(node.getState(), nextStates);
			for(GameState nState : nextStates){
				if(node.getState().sameAs(nState)){
					pntsThisIter += nextStatePoints.get(nState);
				}
			}
			
		}
		parent = node;
		Set<GameState> statesSet = (Set<GameState>) nextStatesPoints.keySet();
		int i = 0;
		GameState[] states = new GameState[statesSet.size()];
		for(GameState s : statesSet){
			states[i++] = s;
		}
		GameState state = states[0];//randomly select one of the action which is not explored yet
		pntsThisIter += nextStatesPoints.get(state);
		GameTreeNode gameNode = new GameTreeNode(state);
		return gameNode;
	}*/

	private void setBackRewardsToAllInPath(GameTree gameTree, GameTreeNode endNode, int reward) {
		
		GameTreeNode root = gameTree.getRoot();
		List<GameTreeNode> children = root.getChildren();
		GameTreeNode node = root;
		while(node != endNode){
			for(int i=0; i<children.size() ;i++){
				node = children.get(i);
				if(node.isInTPolicyPath()){
					//System.out.println("reward  "+reward);
					//System.out.println("visits  "+node.getVisits());
					//System.out.println("reward assigned "+ (node.getBackReward()*(node.getVisits()-1)+reward)/node.getVisits());
					//System.out.print("state  "+node.getState().getRemovedCards());
					node.setBackAvgReward((node.getBackAvgReward()*(node.getVisits()-1)+reward)/node.getVisits());
					if(reward >= node.getBackMaxReward()){
						node.setBackMaxReward(reward);
					}
					node.setInTPolicyPath(false);
					children = node.getChildren();
					break;
				}
			}
		}
	}

	private GameTreeNode selectNExpand(GameTree gameTree, int pntsThisIter, List<GameTreeNode> path) {

		List<GameState> nextStates = new ArrayList<GameState>();
		HashMap<GameState, Integer> nextStatesPoints = new HashMap<GameState, Integer>();
		getNextStates(gameTree.getRoot().getState(), nextStates, nextStatesPoints); 
		GameTreeNode gameNode = null;
		GameTreeNode parent = gameTree.getRoot();
		GameTreeNode node = gameTree.getRoot();
		node.setVisits(node.getVisits()+1); //incrementing root's visits
		path.add(node);
		
		while(anyNextUnexploredStates(node, nextStates, nextStatesPoints)){// all state explored, time for tree policy
			
			getNextStates(node.getState(), nextStates, nextStatesPoints);// find next states of this child
			
			if(nextStates.isEmpty()){ //when the node's state is the terminal state
				node.setVisits(node.getVisits()-1);
				return null;
			}
			
			node = treePolicy(node); //returns a good child from a node(This child is already explored one) 
			path.add(node);
			parent = node; //parent is required to add the child that we got into the tree which is unexplored 
						   //after next few lines there will be a while check to find if there are any unexplored next states if
			               //there are any then that is added to this parent after out of the while loop 
			node.setInTPolicyPath(true);
			node.setVisits(node.getVisits()+1);
			//System.out.println("tree policy.. "+node.getState().getRemovedCards());
			
			for(GameState nState : nextStates){
				if(node.getState().sameAs(nState)){ // Among all the states in nextStates check which was the one 
													//chosen by treepolicy(node's state) 
					pntsThisIter += nextStatesPoints.get(nState);//As we are travelling until we find an unexplored 
					break;										//node : we keep track of the points
				}
			}
			
		}
		if(!(nextStatesPoints.isEmpty())){
			//there are a few states which are unexplored till now which are in nextStatesPoints
			GameState state = nextStates.get(0);//randomly select one of the action which is not explored yet
			//System.out.println("unexplored.. "+state.getRemovedCards());
			pntsThisIter += nextStatesPoints.get(state);
			gameNode = new GameTreeNode(state);
		}
		if(gameNode == null){//tree policy reached terminal state
			return null;
		}
		gameNode.setVisits(1);
		//System.out.println("parent   "+parent.getState().getRemovedCards());
		//System.out.println("");
		//System.out.println("child   "+gameNode.getState().getRemovedCards());
		parent.addToChildren(gameNode);
		path.add(gameNode);
		gameNode.setInTPolicyPath(true);
		return gameNode;
	}

	private GameTreeNode treePolicy(GameTreeNode node) {
		float maxUct = 0;
		GameTreeNode selChild = null;
		for(GameTreeNode child : node.getChildren()){
			//float uct = (float) (child.getBackAvgReward() + 2 * Cp * Math.sqrt(2 * Math.log(node.getVisits())/child.getVisits()));
			double exploit = 0*child.getBackMaxReward()+(1-0)*child.getBackAvgReward();
			float uct = (float) (exploit + 2 * Cp * Math.sqrt(2 * Math.log(node.getVisits())/child.getVisits()));
			if(uct >= maxUct){
				selChild = child;
				maxUct = uct;
			}
		}
		//System.out.println("Max UCT"+ maxUct);
		return selChild;
	}

	//returns not yet explored game states
	private boolean anyNextUnexploredStates(GameTreeNode node, List<GameState> nextStates, HashMap<GameState,Integer> nextStatesPoints) {
		
		
		List<GameState> nextStatesInTree = new ArrayList<GameState>();
		nextStatesInTree = node.getAllChildrenStates();
		getNextStates(node.getState(), nextStates, nextStatesPoints);
		if(nextStatesInTree.isEmpty()){
			return false;   // there are unexplored states
		}
		findNextNotInTree(nextStates, nextStatesInTree);//could have done nextStates.removeAll(nextStatesInTree) but the objects in the tree are different from that in the nextStates even if they are identical interms of cards and every other thing
		if(nextStates.isEmpty()){  //all states explored
			return true;  // no unexplored
		}
		/*for(GameState nState : nextStates){
			nextStatesPoints.remove(nState);
		}*/
		return false;    // there are unexplored states
	}

	private void findNextNotInTree(List<GameState> nextStates, List<GameState> nextStatesInTree) {
		List<GameState> toBeRemoved = new ArrayList<GameState>();
		for(GameState i : nextStates){
			for(GameState j : nextStatesInTree){ //if(nextStatesInTree.contains(i))
				if(j.sameAs(i)){
					toBeRemoved.add(i);
					break;
				}
			}	
		}
		nextStates.removeAll(toBeRemoved);
	}

	private int defaultRun(GameTreeNode gameNode, int pntsThisIter, List<GameTreeNode> path) {
		List<GameState> nextStates = new ArrayList<GameState>();
		GameTreeNode node = gameNode;
		HashMap<GameState, Integer> nStatesPoints = new HashMap<GameState, Integer>();
		getNextStates(node.getState(), nextStates, nStatesPoints);
		while(!nStatesPoints.isEmpty()){
			Set<GameState> gameStateSet = nStatesPoints.keySet();
			GameState selState = null;
			int randomNum = (int) (Math.random()*(gameStateSet.size()-1));
			int i = 0;
			for(GameState state : gameStateSet){
				if(i == randomNum){
					selState = state;
					break;
				}
				i++;
			}
			node = new GameTreeNode(selState);//node is incremented to one of the randomly selected node
			path.add(node);
			pntsThisIter += nStatesPoints.get(selState);//points are calculated corresponding to that random selected node
			
			getNextStates(node.getState(), nextStates, nStatesPoints);//next state points are calculated for the next iteration
		}
		return pntsThisIter;
	}
	
	private int defaultRunAstar(GameTreeNode gameNode, int pntsThisIter) {
		List<GameState> nextStates = new ArrayList<GameState>();
		GameTreeNode node = gameNode;
		HashMap<GameState, Integer> nStatesPoints = new HashMap<GameState, Integer>();
		getNextStates(node.getState(), nextStates, nStatesPoints);
		while(!nStatesPoints.isEmpty()){
			Set<GameState> gameStateSet = nStatesPoints.keySet();
			GameState selState = null;
			int maxPoints = 0;
			for(GameState state : gameStateSet){
				Integer points = nStatesPoints.get(state);
				if(points >= maxPoints){
					selState = state;
					maxPoints = points;
				}
			}
			node = new GameTreeNode(selState);//node is incremented to one of the randomly selected node
			pntsThisIter += nStatesPoints.get(selState);//points are calculated corresponding to that random selected node
			
			getNextStates(node.getState(), nextStates, nStatesPoints);//next state points are calculated for the next iteration
		}
		return pntsThisIter;
	}

	public void getNextStates(GameState presentState, List<GameState> nextStates, HashMap<GameState,Integer> nextStateWithPoints){
		
		ArrayList<GameState> nextStatesCopy = new ArrayList<GameState>();
		nextStatesCopy.addAll(nextStates);
		nextStates.removeAll(nextStatesCopy);
		nextStateWithPoints.keySet().removeAll(nextStateWithPoints.keySet());
		
		//trashing every card at each of the 9 deck
		getNextWhenTrashed(presentState, nextStateWithPoints,nextStates);
		
		//System.out.println("trash : No. of next states :"+nextStates.size());
		
		getNextWhenPaired(presentState, nextStateWithPoints,nextStates);
		
		//System.out.println("pair : No. of next states :"+nextStates.size());
		
		getNextWhenThreeRemoved(presentState, nextStateWithPoints,nextStates);
		
		//System.out.println("three : No. of next states :"+nextStates.size());
		
		getNextWhenFourRemoved(presentState, nextStateWithPoints,nextStates);
		
		//System.out.println("four : No. of next states :"+nextStates.size());
		
		getNextWhenFiveRemoved(presentState, nextStateWithPoints,nextStates);
		
		//System.out.println("five : No. of next states :"+nextStates.size());
		/*for(GameState nState : nextStates){
			
			nState.displayBoard();
			System.out.println("POINTS : "+nextStateWithPoints.get(nState));
		}*/
		
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
}
