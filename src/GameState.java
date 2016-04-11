import java.util.List;
import java.util.Stack;

/**
 * Created by Karan Shah on 4/11/2016.
 */
public class GameState {

    int bonusCard;
    List<Integer> removedCards;
    Stack<Integer>[] deckCards = new Stack[9];
    int remainingTrashes;

    public int getRemainingTrashes() {
        return remainingTrashes;
    }

    public void setRemainingTrashes(int remainingTrashes) {
        this.remainingTrashes = remainingTrashes;
    }

    public List<Integer> getRemovedCards() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setRemovedCards(List<Integer> cards) {
        // TODO Auto-generated method stub

    }

    //return card value at position i,j(range is 0-3)
    public Stack<Integer> getDeckCards(int i) {
        return null;
    }

    //append the input list to removedCards field
    public void addToRemovedCards(List<Integer> cards) {
        // TODO Auto-generated method stub

    }

    //return all the cards on the deck stack matrix
    public Stack<Integer>[] getAllDeckCards() {
        return deckCards;
        // TODO Auto-generated method stub

    }

    public int getBonusCard() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setBonusCard(int card) {

    }

    public void setDeckCards(Stack<Integer>[] cards) {
        // TODO Auto-generated method stub

    }

    //IMPLEMENT THIS
    public boolean sameAs(GameState nState) {
        // TODO Auto-generated method stub
        return false;
    }
}
