package aigames.sagesol.treesearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import aigames.sagesol.general.GameState;

public class TestFullTreeSearch {

	static GameState startState = new GameState(true); // true says generate new game 
	static GameTree gameTree = new GameTree(startState);
	static GameTreeNode root = gameTree.getRoot();
	
	public static void main(String[] args) {
		
		MinGameTreeNode node = new MinGameTreeNode();
		
		
		constructTree(node);
	}

	private static void constructTree(MinGameTreeNode node) {
		
		MCTSplayer player = new MCTSplayer();
		List<GameState> nextStates = new ArrayList<GameState>();
		HashMap<GameState, Integer> nextStateWithPoints = new HashMap<GameState, Integer>();
		GameState presentState = new GameState(false);
		computeState(presentState,node,root);
		player.getNextStates(presentState, nextStates, nextStateWithPoints);
		ArrayList<MinGameTreeNode> children = new ArrayList<MinGameTreeNode>();
		for(GameState nState : nextStates){
			MinGameTreeNode child = new MinGameTreeNode();
			Integer points = nextStateWithPoints.get(nState);
			child.setRewardFromRoot(node.getRewardFromRoot()+points);
			child.setRemovedCards(nState.getRemovedCards());
			child.setRemTrashes(nState.getRemainingTrashes());
			constructTree(child);
			children.add(child);
		}
		if(nextStates.isEmpty()){
			System.out.println("Terminal State "+ node.getRewardFromRoot());
		}
		node.setChildren(children);
		System.out.println("parent "+node.getRemovedCards());
		for(MinGameTreeNode child : children){
			System.out.println("parent "+child.getRemovedCards());
		}
		System.out.println("");
	}

	private static void computeState(GameState presentState, MinGameTreeNode node, GameTreeNode root) {
		
		startState = root.getState();
		presentState.setBonusCard(startState.getBonusCard());
		presentState.setRemainingTrashes(node.getRemTrashes());
		presentState.setRemovedCards(node.getRemovedCards());
		Stack<Integer>[][] deckCards = computeDeckCards(startState, node);
		presentState.setDeckCards(deckCards);
	}

	private static Stack<Integer>[][] computeDeckCards(GameState sState, MinGameTreeNode node) {
		
		Stack<Integer>[][] deckCards = new Stack[3][3];
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				deckCards[i][j] = new Stack();
				for(int k=0;k<sState.getNoOfCards()[i][j];k++){
					deckCards[i][j].push(sState.getDeckCards()[i][j].get(k));
				}
			}
		}
		
		List<Integer> removedCards = node.getRemovedCards();
		for(Integer rCard : removedCards){
			for(int i=0;i<3;i++){
				int j=0;
				int popedCard = 0;
				for(;j<3;j++){
					if(!deckCards[i][j].isEmpty()){
						if(deckCards[i][j].peek() == rCard){
							popedCard = deckCards[i][j].pop();
							break;
						}
					}
				}
				if(popedCard == rCard){
					break;
				}
			}
		}
		return deckCards;
	}
}
