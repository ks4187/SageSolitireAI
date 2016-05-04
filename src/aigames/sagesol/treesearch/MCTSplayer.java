package aigames.sagesol.treesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import aigames.sagesol.general.GameState;
import aigames.sagesol.general.ImperfectGameState;
import aigames.sagesol.general.SSPlayer;

public class MCTSplayer extends SSPlayer{
	
	private static final int MCTSITERATIONS = 4000;
	private static final double Cp = 1/(Math.sqrt(2)); //;
	private static final double MIXMAX = 0.4;
	//private static final float MAXHIGHSCORE = 3330;//Maximum high score possible
	private static final double MAXSCOREONEMOVE = 1;//averagely maximum
	//private static double maxExploit = 0;
	
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
					//System.out.print("maxReward "+maxReward);System.out.println(" Iteration"+count);
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
				//System.out.println("backreward "+child.getBackReward());
				 if(child.getBackAvgReward() >= maxBackReward){
					 goodChild  = child; 
					 maxBackReward = child.getBackAvgReward();
				 }
			}
			//System.out.println("removed "+goodChild.getState().getRemovedCards());
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
			//System.out.println("node "+node.getState().getRemovedCards());
			//System.out.println("endNode "+endNode.getState().getRemovedCards());
			for(int i=0; i<children.size() ;i++){
				node = children.get(i);
				if(node.isInTPolicyPath()){
					//System.out.println("reward  "+reward);
					//System.out.println("visits  "+node.getVisits());
					//System.out.println("reward assigned "+ (node.getBackReward()*(node.getVisits()-1)+reward)/node.getVisits());
					//System.out.print("state  "+node.getState().getRemovedCards());
					node.setBackAvgReward((float) ((node.getBackAvgReward()*(node.getVisits()-1)+reward/MAXSCOREONEMOVE)/node.getVisits()));//reward/MAXSCOREONEMOVE normalizing
					if(reward >= node.getBackMaxReward()){
						node.setBackMaxReward(reward);
					}
					//System.out.println("set false"+node.getState().getRemovedCards());
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
			//System.out.println("before tree policy.. "+node.getState().getRemovedCards());
			node = treePolicy(node); //returns a good child from a node(This child is already explored one) 
			path.add(node);
			parent = node; //parent is required to add the child that we got into the tree which is unexplored 
						   //after next few lines there will be a while check to find if there are any unexplored next states if
			               //there are any then that is added to this parent after out of the while loop 
			//System.out.println("set true tree policy"+node.getState().getRemovedCards());
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
		GameState state = null;
		if(!(nextStatesPoints.isEmpty())){
			state = nextStates.get(0);//randomly select one of the action which is not explored yet
			//there are a few states which are unexplored till now which are in nextStatesPoints
			//System.out.println("unexplored.. "+state.getRemovedCards());
			pntsThisIter += nextStatesPoints.get(state);
			gameNode = new GameTreeNode(state);
		}
		if(gameNode == null){//tree policy reached terminal state
			GameTreeNode root = gameTree.getRoot();
			GameTreeNode n = root;
			for(int i=0;i<path.size();i++){
				List<GameTreeNode> children = n.getChildren();
				for(GameTreeNode child : children){
					if(child.isInTPolicyPath()){
						child.setInTPolicyPath(false);  //remove all in T paths as we are encountered the terminal state during tree policy
						n = child;
						break;
					}
				}
			}
			return null;
		}
		gameNode.setOneStepReward(nextStatesPoints.get(state));
		gameNode.setVisits(1);
		//System.out.println("parent   "+parent.getState().getRemovedCards());
		//System.out.println("");
		//System.out.println("child   "+gameNode.getState().getRemovedCards());
		parent.addToChildren(gameNode);
		path.add(gameNode);
		//System.out.println("set true"+gameNode.getState().getRemovedCards());
		gameNode.setInTPolicyPath(true);
		return gameNode;
	}

	private GameTreeNode treePolicy(GameTreeNode node) {
		float maxUct = 0;
		GameTreeNode selChild = null;
		for(GameTreeNode child : node.getChildren()){
			//float uct = (float) (child.getBackAvgReward() + 2 * Cp * Math.sqrt(2 * Math.log(node.getVisits())/child.getVisits()));
			double exploit = MIXMAX*child.getOneStepReward()/MAXSCOREONEMOVE+(1-MIXMAX)*child.getBackAvgReward();
			/*if(exploit >= maxExploit){
				maxExploit = exploit;
				//System.out.println("Max Exploit "+ maxExploit);
			}*/
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
		
		//System.out.println("Inside anyNextUnexploredStates() "+node.getState().getRemovedCards());
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
		GameTreeNode node;
		HashMap<GameState, Integer> nStatesPoints = new HashMap<GameState, Integer>();
		getNextStates(gameNode.getState(), nextStates, nStatesPoints);
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


}
