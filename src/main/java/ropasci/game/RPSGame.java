package ropasci.game;

import java.util.HashMap;
import java.util.logging.Logger;

import static ropasci.game.RPSGame.Action.PAPER;
import static ropasci.game.RPSGame.Action.ROCK;
import static ropasci.game.RPSGame.Action.SCISSORS;

public class RPSGame {
    private static final Logger log = Logger.getLogger(RPSGame.class.getName());

    public enum Action {
        ROCK, PAPER, SCISSORS
    }

    private HashMap<String, Integer> globalScores;

    public HashMap<String, Integer> getGlobalScores() {
        return globalScores;
    }

    public HashMap<String, Integer> getLastScores() {
        return lastScores;
    }

    private HashMap<String, Integer> lastScores;

    public RPSGame() {
        this.globalScores = new HashMap<String, Integer>();
        this.lastScores = new HashMap<String, Integer>();
    }

    public void addPlayer(String id) {
        addPlayer(id, 0);
    }

    public void addPlayer(String id, int score) {
        globalScores.put(id, score);
    }

    public void removePlayer(String id) {
        globalScores.remove(id);
        lastScores.remove(id);
    }

    public int getTotalScoreForPlayer(String id) {
        return globalScores.getOrDefault(id, 0);
    }

    public void calculateScore(HashMap<String, Action> playerActions) {
        /*
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
        int numRocks = 0, numPapers = 0, numScissors = 0;
        for (Action action : playerActions.values()) {
            switch (action) {
                case ROCK:
                    numRocks++;
                    break;
                case PAPER:
                    numPapers++;
                    break;
                case SCISSORS:
                    numScissors++;
                    break;
            }
        }
        int rocksPoints = numRocks > 0 ? numScissors : 0;
        int paperPoints = numPapers > 0 ? numRocks : 0;
        int scissorsPoints = numScissors > 0 ? numPapers : 0;

        for (String id : playerActions.keySet()) {
            int points = 0;
            switch (playerActions.get(id)) {
                case ROCK:
                    points = rocksPoints;
                    break;
                case PAPER:
                    points = paperPoints;
                    break;
                case SCISSORS:
                    points = scissorsPoints;
                    break;
            }
            lastScores.put(id, points);
        }
    }

    public void updateGlobalScore() {
        for (String id : lastScores.keySet()) {
            int newScore = globalScores.getOrDefault(id, 0) + lastScores.get(id);
            globalScores.put(id, newScore);
        }
    }

    public void clearGlobalScore() {
        globalScores.clear();
    }

    private void logLastScores() {
        log.info(lastScoresStr());
    }

    private String lastScoresStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (String id : lastScores.keySet()) {
            sb.append("\tid(").append(id).append("): ").append(lastScores.get(id));
            sb.append("\n");
        }
        return sb.toString();
    }

    private void logGlobalScores() {
        log.info(globalScoresStr());
    }


    private String globalScoresStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (String id : globalScores.keySet()) {
            sb.append("\tid(").append(id).append("): ").append(globalScores.get(id));
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        testScoreCalc();
    }

    private static void testScoreCalc() {
        HashMap<String, Action> playerActions = new HashMap<>();
        playerActions.put("a", ROCK);
        playerActions.put("b", ROCK);
        playerActions.put("c", ROCK);
        playerActions.put("d", ROCK);
        playerActions.put("e", PAPER);
        playerActions.put("f", PAPER);
        playerActions.put("g", SCISSORS);

        RPSGame game = new RPSGame();
        game.calculateScore(playerActions);
        game.logLastScores();
        game.updateGlobalScore();
        game.logGlobalScores();
        game.calculateScore(playerActions);
        game.updateGlobalScore();
        game.logGlobalScores();
        game.clearGlobalScore();
        game.logGlobalScores();
    }
}
