/**
 * Created by Karan Shah on 4/7/2016.
 */

import java.util.*;

public class BoardSetup {

    public int[] playingCards;
    public int[] noOfCards = {8, 8, 8, 7, 6, 5, 4, 3, 2};

    public int bonusCard;

    public Stack<Integer>[] boardCards = new Stack[9];


    public List<Integer> shuffledCards = new ArrayList<>();


    /*
        THIS METHOD GENERATES THE CARDS
        THE ORDER IN WHICH THE CARDS ARE GENERATED ARE:
            SPADES : 1 - 13
            DIAMONDS : 101 - 113
            CLUBS : 201 - 213
            HEARTS : 301 - 313
     */
    void generateCards() {

        playingCards = new int[52];
        int add = 0;
        int inc = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                playingCards[j + inc] = j + add + 1;
                //System.out.println(playingCards[j + inc]);
            }
            inc += 13;
            add += 100;
        }
    }

    /*
    * THIS METHOD SHUFFLES THE DECK AND GIVES A RANDOM SETTING EACH TIME THE DECK IS SHUFFLED
    * */
    void shuffleDeck() {

        for (int i = 0; i < 52; i++)
            shuffledCards.add(playingCards[i]);

        Collections.shuffle(shuffledCards);

        //System.out.println(shuffledCards);

    }


    void generateBoard() {
        int counter = 1;


        for (int i = 0; i < 9; i++) {
            boardCards[i] = new Stack<Integer>();
            for (int j = 0; j < noOfCards[i]; j++) {
                boardCards[i].push(shuffledCards.get(counter));
                counter++;
            }
        }


    }

    public void displayBoard() {


        System.out.println("**SUIT AND CARD**\n0\tSPADES\n1\tDIAMONDS\n2\tCLUBS\n3\tHEARTS\n1 - 13\tCARD VALUE\n");

        System.out.println("Bonus Card: " + bonusCard);

        for (int i = 0; i < 9; i = i + 3) {
            System.out.print(boardCards[i].get(0));
            System.out.print(" (" + (boardCards[i].size() - 1) + ")\t");
            System.out.print(boardCards[i + 1].get(0));
            System.out.print(" (" + (boardCards[i + 1].size() - 1) + ")\t");
            System.out.print(boardCards[i + 2].get(0));
            System.out.println(" (" + (boardCards[i + 2].size() - 1) + ")");
        }


    }


    public void BoardSetup() {
        generateCards();
        shuffleDeck();

        bonusCard = shuffledCards.get(0);   //BONUS CARD WILL ALWAYS BE THE FIRST CARD AND REMAINING CARDS ON THE BOARD
        generateBoard();
        displayBoard();


    }
}
