package aigames.sagesol;

import java.util.ArrayList;
import java.util.List;

class MinGameTreeNode {
	
	List<Integer> removedCards;
	
	public MinGameTreeNode() {
		removedCards = new ArrayList<Integer>();
	}
	
	public List<Integer> getRemovedCards() {
		return removedCards;
	}
	public void setRemovedCards(List<Integer> removedCards) {
		this.removedCards = removedCards;
	}
	int remTrashes;
	public int getRemTrashes() {
		return remTrashes;
	}
	public void setRemTrashes(int remTrashes) {
		this.remTrashes = remTrashes;
	}
	int rewardFromRoot;
	
	public int getRewardFromRoot() {
		return rewardFromRoot;
	}
	public void setRewardFromRoot(int rewardFromRoot) {
		this.rewardFromRoot = rewardFromRoot;
	}
	public List<MinGameTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<MinGameTreeNode> children) {
		this.children = children;
	}
	
	List<MinGameTreeNode> children;
}


class GameTreeNode {
	GameState state;
	float backMaxReward;
	int visits;
	boolean inTPolicyPath;
	float backAvgReward;
	int rewardFromRoot;
	int oneStepReward;
	

	public int getOneStepReward() {
		return oneStepReward;
	}

	public void setOneStepReward(int oneStepReward) {
		this.oneStepReward = oneStepReward;
	}

	public int getRewardFromRoot() {
		return rewardFromRoot;
	}

	public void setRewardFromRoot(int rewardFromRoot) {
		this.rewardFromRoot = rewardFromRoot;
	}

	public float getBackAvgReward() {
		return backAvgReward;
	}

	public void setBackAvgReward(float backAvgReward) {
		this.backAvgReward = backAvgReward;
	}

	public boolean isInTPolicyPath() {
		return inTPolicyPath;
	}

	public void setInTPolicyPath(boolean inTPolicyPath) {
		this.inTPolicyPath = inTPolicyPath;
	}

	List<GameTreeNode> children;

	GameTreeNode(GameState s) {
		state = s;
		children = new ArrayList<GameTreeNode>();
		visits = 0;
	}

	public float getBackMaxReward() {
		return backMaxReward;
	}

	public void setBackMaxReward(float backMaxReward) {
		this.backMaxReward = backMaxReward;
	}

	public int getVisits() {
		return visits;
	}

	public void setVisits(int visits) {
		this.visits = visits;
	}

	public List<GameTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<GameTreeNode> children) {
		this.children = children;
	}
	
	public void addToChildren(GameTreeNode child) {
		this.children.add(child);
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public List<GameState> getAllChildrenStates() {
		List<GameState> childrenStates = new ArrayList<GameState>();
		if(!getChildren().isEmpty() && getChildren() != null){
			for(GameTreeNode child : getChildren()){
				if(child != null){
					childrenStates.add(child.getState());
				}
			}
		}
		return childrenStates;
	}

	public void display() {
		GameState s = this.getState();
		s.displayBoard();
		
	}
	
	
	
	
}

public class GameTree {

	GameTreeNode root;
	
	GameTree(GameTreeNode node){
		root = node;
	}
	
	GameTree(GameState s){
		root = new GameTreeNode(s);
	}

	public GameTreeNode getRoot() {
		return root;
	}
	
}
