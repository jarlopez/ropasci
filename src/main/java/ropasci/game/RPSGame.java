package ropasci.game;

import java.util.HashMap;

public class RPSGame {
    /**
     * Rock smashes scissors, scissors cut paper, and paper covers rock.
     * Points are awarded as follows:
     *      - Assume n > 1 players
     *      - Award m players (n - m) points each if they choose the
     *        same gesture and beat the other (n - m) players.
     *        This rule implies that
     *               - if all n players choose the same gesture (i.e. if m == n),
     *                 then points are not awarded
     *               - if a player beats all the others (i.e. if m == 1),
     *                 then the winner is awarded (n -1) points.
     */
    private HashMap globalScores;
    private HashMap lastScores;

    public RPSGame() {
        this.globalScores = new HashMap();
        this.lastScores = new HashMap();
    }
    public void addPlayer(String id) {

    }
    public void removePlayer(String id) {

    }

    public int getScoreForPlayer(String id) {
        return 0;
    }

    public void calculateScore(HashMap playerActions) {

    }
}
