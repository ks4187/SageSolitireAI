/**
 * Created by Karan Shah on 4/7/2016.
 */
public class BoardSetup {


    /*
        THIS METHOD GENERATES THE CARDS
        THE ORDER IN WHICH THE CARDS ARE GENERATED ARE:
            SPADES : 1 - 13
            DIAMONDS : 101 - 113
            CLUBS : 201 - 213
            HEARTS : 301 - 313
     */
    void generateCards() {
        int[] playingCards;
        playingCards = new int[52];
        int add = 0;
        int inc = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                playingCards[j + inc] = j + add + 1;
                System.out.println(playingCards[j + inc]);
            }
            inc += 13;
            add += 100;
        }
    }
}
