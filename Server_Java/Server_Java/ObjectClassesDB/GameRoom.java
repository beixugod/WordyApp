package Server_Java.ObjectClassesDB;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static Server_Java.WordyServerHelper.WordyServerMethods.produceRandomLetters;

public class GameRoom {
    private final String roomName;
    private final ArrayList<String> playerList;
    private final ArrayList<String> randomLetters;
    private final ArrayList<String> confirmEndRound = new ArrayList<>();
    private int gameRoundTimer;
    private int scoresRoomTimer;
    private int currentRound;
    private Timer timer1;
    private Timer timer2;

    private boolean winnerFound;

    public GameRoom(String r) {
        playerList = new ArrayList<>();
        randomLetters = new ArrayList<>();
        gameRoundTimer = 10;
        scoresRoomTimer = 10;
        currentRound = 1;
        roomName = r;
    }

    public void startGameRoundTimer() {
        System.out.println("GAME " + roomName + ": Round timer started.");
        timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                if (winnerFound) {
                    System.out.println("GAME " + roomName + ": Winner found, ending game");
                    timer1.cancel();
                } else if (playerList.size() <= 1) {
                    System.out.println("GAME " + roomName + ": Only one player left, ending game");
                    timer1.cancel();
                }
                if (gameRoundTimer != 0) {
                    gameRoundTimer--;
                    System.out.println("GAME " + roomName + " Countdown: " + gameRoundTimer);
                } else {
                    currentRound++;
                    scoresRoomTimer = 10;
                    gameRoundTimer = 10;
                    timer1.cancel();
                    startScoresRoomTimer();
                }
            }
        }, 0, 1000);
    }

    public void startScoresRoomTimer() {
        System.out.println("GAME " + roomName + ": Scores room timer started.");
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                if (winnerFound) {
                    System.out.println("GAME " + roomName + ": Winner found, ending game");
                    timer2.cancel();
                } else if (playerList.size() <= 1) {
                    System.out.println("GAME " + roomName + ": Only one player left, ending game");
                    timer2.cancel();
                }
                if (scoresRoomTimer > 0) {
                    scoresRoomTimer--;
                } else {
                    scoresRoomTimer = 10;
                    gameRoundTimer = 10;
                    timer2.cancel();
                    startGameRoundTimer();
                }
            }
        }, 0, 1000);
    }

    public int getGameRoundTimer() {
        return gameRoundTimer;
    }

    public int getScoresRoomTimer() {
        return scoresRoomTimer;
    }
    public int getCurrentRound() { return currentRound;}

    public void setWinnerFound(boolean b) {
        winnerFound = b;
    }

    public void addPlayer(String username) {
        playerList.add(username);
    }

    public void removePlayer(String username) {
        playerList.remove(username);
    }

    public String[] getPlayerList() {
        return playerList.toArray(new String[0]);
    }

    public void startGame() {
        generateRandomLettersEvery9Seconds();
        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startGameRoundTimer();
    }

    public String getRandomLetters(int index) {
        return randomLetters.get(index - 1);
    }

    public void confirmEndRound(String username) {
        confirmEndRound.add(username);
    }

    public void generateRandomLettersEvery9Seconds() {
        System.out.println("GAME ROOM " + roomName + " has begun generating letters");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String random = produceRandomLetters();
                randomLetters.add(random);
            }
        }, 0, 9000);
    }
}
