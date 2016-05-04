package aigames.sagesol.general;

public class ImperfectGameState {
	
	int bonusSuit;
    int[][] deckCards;
    int remainingTrashes;
    int[][] remBehind;
    
	public int[][] getRemBehind() {
		return remBehind;
	}
	public void setRemBehind(int[][] remBehind) {
		this.remBehind = remBehind;
	}
	
	
	public int getBonusSuit() {
		return bonusSuit;
	}
	public void setBonusSuit(int bonusSuit) {
		this.bonusSuit = bonusSuit;
	}
	public int[][] getDeckCards() {
		return deckCards;
	}
	public void setDeckCards(int[][] deckCards) {
		this.deckCards = deckCards;
	}
	public int getRemainingTrashes() {
		return remainingTrashes;
	}
	public void setRemainingTrashes(int remainingTrashes) {
		this.remainingTrashes = remainingTrashes;
	}

}
