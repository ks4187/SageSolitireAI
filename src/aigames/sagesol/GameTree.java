package aigames.sagesol;

import java.util.ArrayList;
import java.util.List;

class GameTreeNode {
	GameState state;
	float backReward;
	int visits;
	boolean inTPolicyPath;
	
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
		backReward = 0;
		visits = 0;
	}

	public float getBackReward() {
		return backReward;
	}

	public void setBackReward(float backReward) {
		this.backReward = backReward;
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
