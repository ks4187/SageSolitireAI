package aigames.sagesol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by Karan Shah on 4/11/2016.
 */
public class GameState {

    int bonusCard;
    List<Integer> removedCards;
    Stack<Integer>[][] deckCards;
    int remainingTrashes;
    
    private static final boolean SINGLESAMEDECK = false;
    

    private int[] playingCards;
    public int[] getPlayingCards() {
		return playingCards;
	}

	public void setPlayingCards(int[] playingCards) {
		this.playingCards = playingCards;
	}

	private int[][] noOfCards = {
            {8, 8, 8,},
            {7, 6, 5,},
            {4, 3, 2}
    };

    //public Stack<Integer>[] boardCards = new Stack[9];

    public int[][] getNoOfCards() {
		return noOfCards;
	}

	public void setNoOfCards(int[][] noOfCards) {
		this.noOfCards = noOfCards;
	}

	private List<Integer> shuffledCards = new ArrayList<>();
    
    
    public List<Integer> getShuffledCards() {
		return shuffledCards;
	}

	public void setShuffledCards(List<Integer> shuffledCards) {
		this.shuffledCards = shuffledCards;
	}

	//private int[] fixedCards = {202, 13, 112, 105, 211, 207, 312, 201, 5, 101, 4, 104, 204, 106, 12, 313, 102, 311, 310, 305, 307, 308, 109, 203, 7, 304, 303, 205, 11, 107, 9, 110, 212, 111, 306, 309, 6, 10, 213, 113, 206, 209, 8, 3, 208, 301, 103, 1, 302, 2, 108, 210};
	//private int[] fixedCards = {210, 302, 105, 108, 112, 313, 201, 205, 102, 11, 309, 303, 208, 7, 3, 5, 212, 1, 4, 304, 111, 306, 206, 101, 312, 106, 204, 209, 207, 113, 109, 308, 307, 305, 203, 202, 103, 13, 211, 311, 310, 2, 10, 213, 8, 110, 6, 301, 9, 12, 107, 104};
	//max fixed cards below
	private int[] fixedCards = {13, 1, 2, 3, 4, 5, 6, 7, 8, 101, 102, 103, 104, 105, 106, 107, 108, 201, 202, 203, 204, 205, 206, 207, 208, 301, 302, 303, 304, 305, 306, 307, 213, 12, 11, 10, 9, 308, 113, 112, 111, 110, 109, 212, 211, 210, 209, 312, 311, 310, 313, 309};

	
    public GameState(boolean start) {

        if (start) {
            generateCards();
            shuffleDeck();
            if(SINGLESAMEDECK){
            	this.setBonusCard(fixedCards[0]);// BONUS CARD WILL ALWAYS BE THE FIRST CARD AND REMAINING CARDS ON THE BOARD
            }else{
            	this.setBonusCard(shuffledCards.get(0));// BONUS CARD WILL ALWAYS BE THE FIRST CARD AND REMAINING CARDS ON THE BOARD
            }
            generateBoard();
            displayBoard();
            this.setRemainingTrashes(2);
        }else{
            Stack<Integer>[][] deckCards = new Stack[3][3];
        }
        removedCards = new ArrayList<Integer>();
    }

    /*
     * THIS METHOD GENERATES THE CARDS THE ORDER IN WHICH THE CARDS ARE
     * GENERATED ARE: SPADES : 1 - 13 DIAMONDS : 101 - 113 CLUBS : 201 - 213
     * HEARTS : 301 - 313
     */
    private void generateCards() {

        playingCards = new int[52];
        int add = 0;
        int inc = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                playingCards[j + inc] = j + add + 1;
                // System.out.println(playingCards[j + inc]);
            }
            inc += 13;
            add += 100;
        }
    }

    /*
     * THIS METHOD SHUFFLES THE DECK AND GIVES A RANDOM SETTING EACH TIME THE
     * DECK IS SHUFFLED
     */
    private void shuffleDeck() {

        for (int i = 0; i < 52; i++)
            shuffledCards.add(playingCards[i]);

        Collections.shuffle(shuffledCards);

        // System.out.println(shuffledCards);

    }

    private void generateBoard() {
        int counter = 1;

        deckCards = new Stack[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                deckCards[i][j] = new Stack();
                for (int k = 0; k < noOfCards[i][j]; k++) {
                	if(!SINGLESAMEDECK){
                		deckCards[i][j].push(shuffledCards.get(counter));
                	}else{
                		deckCards[i][j].push(fixedCards[counter]);
                	}
                    counter++;
                }
            }
        }

    }

    public void displayBoard() {

        //System.out.println("**SUIT AND CARD**\n0\tSPADES\n1\tDIAMONDS\n2\tCLUBS\n3\tHEARTS\n1 - 13\tCARD VALUE\n");

        System.out.println("Bonus Card: " + bonusCard);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	if(!deckCards[i][j].isEmpty()){
            		System.out.print(deckCards[i][j].peek());
            	}else{
            		System.out.print("Nil");
            	}
            	if(deckCards[i][j].size() < 1){
            		System.out.print(" (" + ("None") + ")\t");
            	}else{
            		System.out.print(" (" + (deckCards[i][j].size() - 1) + ")\t");
            	}
            	
            }
            System.out.println("");
        }
        
        System.out.println("Removed Cards");
        if(removedCards != null){
        	for(Integer card : removedCards){
            	System.out.print(card+", ");
            }
        }
        System.out.println("Trashes"+remainingTrashes);
        
        //System.out.println("");System.out.println("");System.out.println("");

    }

    public int getRemainingTrashes() {
        return remainingTrashes;
    }

    public void setRemainingTrashes(int remainingTrashes) {
        this.remainingTrashes = remainingTrashes;
    }


    public List<Integer> getRemovedCards() {
        return removedCards;
    }

    public void setRemovedCards(List<Integer> removedCards2) {
        this.removedCards = removedCards2;
    }

    // append the input list to removedCards field
    public void addToRemovedCards(List<Integer> cards) {
    	List<Integer> removeList = new ArrayList<Integer>();
		for(Integer rem : getRemovedCards()){
    		removeList.add(rem);
    	}
        removeList.addAll(cards);
        setRemovedCards(removeList);
    }

    public int getBonusCard() {
        return bonusCard;
    }

    public void setBonusCard(int bonusCard) {
        this.bonusCard = bonusCard;
    }

    public Stack<Integer>[][] getDeckCards() {
        return deckCards;
    }

    public void setDeckCards(Stack<Integer>[][] deckCards) {
        this.deckCards = deckCards;
    }

    public boolean sameAs(GameState nState) {
    	 if(this.removedCards.size() != nState.getRemovedCards().size()){
             return false;
         }
         if(!(this.removedCards.containsAll(nState.getRemovedCards()))){
             return false;
         }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	
            	if(!(this.deckCards[i][j].isEmpty() && nState.getDeckCards()[i][j].isEmpty())){
            		if((this.deckCards[i][j].isEmpty() || nState.getDeckCards()[i][j].isEmpty())){
            			return false;
            		}
            		//if (!(this.deckCards[i][j].get(0) == nState.deckCards[i][j].get(0))) {
            		if (!(this.deckCards[i][j].peek() == nState.getDeckCards()[i][j].peek())) {
                        return false;
                    }
            		
            	}
            }
        }
        
        return true;
    }
    
   /* public static void main(String[] args) {
    	
        GameState gs = new GameState(true);
        //gs.displayBoard();
    }*/

	public Stack<Integer>[][] copyDeckCards() {
		
		Stack<Integer>[][] cardsDeck = new Stack[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
            	cardsDeck[i][j] = new Stack();
                for (int k = 0; k < getDeckCards()[i][j].size(); k++) {
                	cardsDeck[i][j].push(getDeckCards()[i][j].get(k));
                }
            }
        }
	        
		return cardsDeck;
	}
}
