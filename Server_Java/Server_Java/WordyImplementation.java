package Server_Java;

import Server_Java.ObjectClassesDB.GameRoom;
import Server_Java.WordyApp.WordyGamePOA;
import Server_Java.WordyApp.InvalidCredentials;
import Server_Java.WordyApp.UserActive;
import Server_Java.WordyServerHelper.ServerDataAccess;

import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.Timer;

import static Server_Java.WordyServerHelper.ServerDataAccess.*;
import static Server_Java.WordyServerHelper.WordyServerMethods.*;

public class WordyImplementation extends WordyGamePOA {
    private static Connection con;
    ServerDataAccess dataAccess = new ServerDataAccess();
    private ArrayList<String> playerList = new ArrayList<>();
    int timerCount = 10;
    String waitingRoomStatus = "No waiting room in session";

    private final HashMap<String, GameRoom> gameRoomHashMap = new HashMap<>();

    private static String roomName = "temp";
    public WordyImplementation(){
        setUpConnection();
    }

    /**
     * The setUpConnection sets up the Database Connection
     */
    public static void setUpConnection() {
        String url = "jdbc:mysql://localhost:3306/wordy";
        String username = "root";
        String password = "";
        try {
            con = DriverManager.getConnection(url, username, password);
            System.out.println("SERVER: Servant class database connection successful.");
        } catch (SQLException e) {
            System.out.println("SERVER: Servant class database connection failed. " + e);
            System.exit(0);
        }
    }

    /**
     * It takes a username and password as input parameters and throws InvalidCredentials and UserActive exceptions if
     * necessary. It performs a database query to check if the username and password are valid, and if the user's status
     * is "OFFLINE." If all conditions are met, it does not return anything; otherwise, it throws an appropriate exception.
     *
     * @param username
     * @param password
     * @throws InvalidCredentials
     * @throws UserActive
     */
    @Override
    public void login(String username, String password) throws InvalidCredentials, UserActive {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (storedPassword.equals(password)) {
                    String status = resultSet.getString("status");
                    if (status.equals("OFFLINE")) {
                        PreparedStatement updateStatement = con.prepareStatement("UPDATE users SET status = 'ONLINE' WHERE username = ?");
                        updateStatement.setString(1, username);
                        updateStatement.executeUpdate();
                    } else {
                        UserActive exception = new UserActive();
                        exception.message = "User is already online!";
                        throw exception;
                    }
                } else {
                    InvalidCredentials exception = new InvalidCredentials();
                    exception.message = "Incorrect password!";
                    throw exception;
                }
            } else {
                InvalidCredentials exception = new InvalidCredentials();
                exception.message = "Invalid username!";
                throw exception;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The setStatus method updates the status of the user to OFFLINE when they choose to logout or close the application window.
     * It changes the user's status from ONLINE to OFFLINE in the database to reflect their current status.
     * @param username
     */
    @Override
    public void logout(String username) {
        String query = "UPDATE users SET status = ? WHERE username=?";
        try (PreparedStatement statement = con.prepareStatement(query, ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE)) {
            statement.setString(1, "OFFLINE");
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    /** The register method registers a new user with the provided username and password.
     * It checks if the input is empty, and returns false if either is null.
     * If the username already exists, it returns false. Otherwise, it adds the new user to the database and returns true.
     * If an error occurs, the method returns false and logs the error message.
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public boolean register(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                // If username is unique, it adds it to the database
                query = "INSERT INTO users (username, password) VALUES (?, ?)";
                try (PreparedStatement insertStatement = con.prepareStatement(query)) {
                    insertStatement.setString(1, username);
                    insertStatement.setString(2, password);
                    insertStatement.executeUpdate();
                    System.out.println("SERVER: " + username + " has successfully registered.");
                    return true;
                } catch (SQLException e) {
                    System.err.println("SQLException: " + e.getMessage());
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            return false;
        }
    }


    /**
     *The getFiveLongestWords method retrieves the top five longest words from the leaderboardwords table along with the
     * associated usernames of the players who achieved those words. It uses a query that groups the words by length,
     * filters out words shorter than the length of the fifth longest word, and orders the result by word length.
     * The usernames of players are concatenated using the GROUP_CONCAT function. The method returns an array of strings
     * containing the longest words appended with their respective usernames.
     *
     * @return
     */
    @Override
    public String[] getFiveLongestWords() {
        String[] longestWords;
        String wordListQuery = "SELECT lbw.word, GROUP_CONCAT(u.userName ORDER BY u.userName) AS playerNames " +
                "FROM leaderboardwords lbw " +
                "INNER JOIN users u ON lbw.userID = u.userID " +
                "GROUP BY lbw.word " +
                "HAVING LENGTH(lbw.word) >= " +
                "(SELECT LENGTH(word) FROM leaderboardwords " +
                "GROUP BY word " +
                "ORDER BY LENGTH(word) DESC LIMIT 1 OFFSET 4) " +
                "ORDER BY LENGTH(lbw.word) DESC " +
                "LIMIT 5";

        try (Statement wordListStatement = con.createStatement();
             ResultSet rs = wordListStatement.executeQuery(wordListQuery)) {

            List<String> wordList = new ArrayList<>();

            while (rs.next()) {
                String longestWord = rs.getString("word");
                String playerNames = rs.getString("playerNames");
                wordList.add(longestWord + " (" + playerNames + ")");
            }

            longestWords = wordList.toArray(new String[wordList.size()]);

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            longestWords = new String[0]; // Return an empty array in case of exception
        }

        return longestWords;
    }


    /**
     *The getTopFivePlayers method retrieves the top five players based on the number of matches they have won.
     * It creates a temporary table to store the top five distinct matchesWon values from the userleaderboard table.
     * Then, it joins the userleaderboard and users tables with the temporary table to fetch the corresponding usernames
     * and matchesWon values for the top players. The method returns an array of strings containing the usernames and
     * matchesWon values of the top five players, sorted in descending order of matchesWon.
     * @return
     */
    @Override
    public String[] getTopFivePlayers() {
        String[] topFive;

        String query = "SELECT userName, matchesWon FROM userleaderboard INNER JOIN users using (userID) ORDER BY" +
                " matchesWon DESC";

        try {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(query);
            List<String> playerList = new ArrayList<>();

            while (rs.next()) {
                String username = rs.getString("userName");
                int matchesWon = rs.getInt("matchesWon");
                playerList.add(username + " - " + matchesWon);
            }

            topFive = playerList.toArray(new String[playerList.size()]);
            statement.close();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
            topFive = new String[0]; // Return an empty array in case of exception
        }

        return topFive;
    }

    /**
     * This method creates a new waiting room for a game by setting the waiting room status to "Waiting room in session,"
     * generating random letters, creating a random room name, and starting a timer that repeats every second.
     * If there are at least two players in the player list when the timer counts down to zero,
     * it sets the waiting room status to "Waiting room ready" and passes the player list to the creation of a new room object.
     * If not, it sets the waiting room status to "Waiting room invalid." It then clears the player list, sleeps for 3 seconds,
     * sets the waiting room status to "No waiting room in session," and cancels the timer. Finally, it prints out messages
     * to the console to indicate the status of the waiting room.
     */
    @Override
    public void startNewWaitingRoom() {
        waitingRoomStatus = "Waiting room in session";
        roomName = generateRandomRoomname();

        System.out.println("SERVER: Waiting room " + roomName + " started.");

        //new game room obj
        GameRoom gameRoomObj = new GameRoom(roomName);

        //add the object to the hash map
        gameRoomHashMap.put(roomName, gameRoomObj);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timerCount--;
                if (timerCount <= 0) {
                    // stop the timer after execution
                    if (gameRoomObj.getPlayerList().length >= 2) {
                        System.out.println("SERVER: Game starting with " + playerList.size() + " players.");
                        waitingRoomStatus = "Waiting room ready";

                        gameRoomObj.startGame();
                        // code to pass onto the creation of a room object, pass the playerList array too

                        playerList.clear();
                    } else {
                        System.out.println("SERVER: Not enough players to start the game.");
                        waitingRoomStatus = "Waiting room invalid";
                        playerList.clear();
                        try {
                            Thread.sleep(3000); // delay for 3 seconds
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    waitingRoomStatus = "No waiting room in session";
                    timer.cancel(); // stop the timer after execution
                    timerCount = 10; // reset timerCount to 10
                }
            }
        }, 0, 1000); // delay 0ms, repeat every 1000ms (1s)
    }

    /**
     * The joinWaitingRoom method adds the given username to the player list of the waiting room if the waiting room status is "Waiting room in session."
     * It also tries to insert the user's data into a database table called "game_session" by using their user ID and the room name.
     * If this is successful, it returns true. If not, it returns false. If the waiting room status is "No waiting room in session," it also returns false.
     * @param username
     * @return
     */
    @Override
    public boolean joinWaitingRoom(String username) {
        if(waitingRoomStatus.equals("Waiting room in session")) {
            playerList.add(username);

            GameRoom gr = gameRoomHashMap.get(roomName);
            gr.addPlayer(username);

            try {
                String sql = "INSERT INTO game_session (roomName, userID) SELECT ?, userID FROM users WHERE username = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, roomName);
                ps.setString(2, username);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } else if(waitingRoomStatus.equals("No waiting room in session")) {
            return false;
        }
        return false;
    }

    /**
     * The leaveGameRoom method called leaveGameRoom() removes the given username from the player list of the waiting room.
     * It also tries to delete the user's data from a database table called "game_session" by using their user ID.
     * @param username
     */
    @Override
    public void leaveGameRoom(String username, String roomName) {
        GameRoom gr = gameRoomHashMap.get(roomName);
        gr.removePlayer(username);


        playerList.remove(username);
        try {
            String sql = "DELETE FROM game_session WHERE userID IN (SELECT userID FROM users WHERE userName = ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * The checkWaitingRoomStatus method returns an array of strings containing the
     * Index 1. Current waiting room status (roomInfo[0])
     * 2. The remaining time on the timer (roomInfo[1])
     * 3. and the name of the room (roomInfo[2])
     * 4. Every index after roomInfo[2] is populated with the current players
     * @return roomInfo
     */
    @Override
    public String[] getWaitingRoomInformation() {
        String[] roomInfo = new String[3 + playerList.size()];
        roomInfo[0] = waitingRoomStatus;
        roomInfo[1] = String.valueOf(timerCount);
        roomInfo[2] = roomName;
        GameRoom gr = gameRoomHashMap.get(roomName);

        String[] players = gr.getPlayerList();
        if (roomInfo.length < players.length + 3) {
            roomInfo = Arrays.copyOf(roomInfo, players.length + 3);
        }
        System.arraycopy(players, 0, roomInfo, 3, players.length);
        return roomInfo;
    }

    /**
     *The validateInput method validates whether a given input string contains only the letters that are present in
     * the given random letters, taking into account the frequency of each letter in the random letters. This also
     * validates if the input string's length is greater than 4.
     */
    @Override
    public boolean submitWord(String answer, String username, int roundNumber, String roomName) {
        String vowels = "AEIOU";
        String consonants = "BCDFGHJKLMNPQRSTVWXYZ";
        Map<Character, Integer> letterCounts = new HashMap<>();

        String[] data = readWordsData();

        ArrayList<String> dataList = new ArrayList<>(Arrays.asList(data));
        answer = answer.replaceAll("\\s", "").toUpperCase();
        String lowercaseString = answer.replaceAll("\\s", "").toLowerCase();

        GameRoom gr = gameRoomHashMap.get(roomName);
        gr.getRandomLetters(roundNumber);

        String randomLetters = gr.getRandomLetters(roundNumber);
        randomLetters = randomLetters.replaceAll("\\s", "").toUpperCase();

        // Count the frequency of each letter in randomLetters
        for (int i = 0; i < randomLetters.length(); i++) {
            char c = randomLetters.charAt(i);
            if (!letterCounts.containsKey(c)) {
                letterCounts.put(c, 1);
            } else {
                letterCounts.put(c, letterCounts.get(c) + 1);
            }
        }

        // Check if inputString is valid
        if (lowercaseString.length() < 5 || !dataList.contains(lowercaseString)) {
            return false;
        }

        // Check if each letter in inputString is present in randomLetters
        for (int i = 0; i < answer.length(); i++) {
            char c = answer.charAt(i);
            if (vowels.indexOf(c) >= 0 && !letterCounts.containsKey(c)) {
                // The letter is a vowel and is not present in the random randomLetters
                return false;
            } else if (consonants.indexOf(c) >= 0 && !letterCounts.containsKey(c)) {
                // The letter is a consonant and is not present in the random randomLetters
                return false;
            } else {
                // The letter is present in the random randomLetters, decrement its count
                int count = letterCounts.get(c);
                if (count == 0) {
                    // The letter is not available anymore in the random randomLetters
                    return false;
                } else {
                    letterCounts.put(c, count - 1);
                }
            }
        }
        //updating the leaderboards and tables
        if (answer.length() >= dataAccess.getLongestWordLength(username)) {
            dataAccess.updateLongestWordFormed(username, answer, roomName);
            insertDataIntoLeaderboard(username, answer, con);
        }
        // All randomLetters are present in the random randomLetters given, the length is greater than 4 and answer is on the words.txt
        return true;
    }

    /**
     * The modifyPointsAchieved method takes a user ID as input and modifies the number of points achieved by the user
     * in a game session. It first retrieves the user's longest word from the game_session table and their current
     * points achieved. If the user has a longest word, it compares it to the longest words of other users in the
     * same game session with shorter lengths and increments the user's points if their longest word is longer.
     * Finally, it updates the user's points achieved in the game_session table and returns the new total points.
     * @param username
     * @return
     */
    @Override
    public boolean endRound(String username, String roomName) {
        int points = 0;
        GameRoom gr = gameRoomHashMap.get(roomName);
        gr.confirmEndRound(username);

        //Get the user's longest word
        String userLW = dataAccess.getUserLongestWord(username, roomName);

        //Get the user's current points
        points = getUserPoints(username, roomName);

        //Compare the user's longest word with other users' longest words
        if (userLW != null) {
            int longestWordLength = dataAccess.getLongestWordLength(username, roomName);
            if (!(longestWordLength == userLW.length() || longestWordLength > userLW.length())) {
                points += 1;
            }
        }

        //Update the user's points
        dataAccess.updateUserPoints(points, username, roomName);

        try {
            Thread.sleep(3000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dataAccess.setLongestWordToNull(roomName);
        if (points == 3) {
            gr = gameRoomHashMap.get(roomName);
            gr.setWinnerFound(true);

            if (!dataAccess.checkUserIDInDatabase(username)) {
                insertUserIDInUserLeaderboard(username, 0, con);
            }
            dataAccess.updateMatchesWon(username);
            //return true if someone won
            return true;
        }
        if (dataAccess.checkPointsAchieved(roomName)) {
            return true;
        }
        //return false if no one reached 3 pts yet, to keep the game going
        return false;
    }

    /**
     * Returns an array with all the necessary information of the current game room
     * Index 1. Game round timer (game proper) gameRoomInfo[0]
     * 2. points of the player (username is in arguments) gameRoomInfo[1]
     * 3. Scores room timer (the in-between GUI of each round gameRoomInfo[2]
     * 4. The current round gameRoomInfo[2]
     * 5. The current round's random letters
     * 6 onwards. Current players in room
     * @param roomName game room name
     * @param username username
     * @return array of gameroom info
     */
    @Override
    public String[] getGameRoomInformation(String roomName, String username) {
        GameRoom gr = gameRoomHashMap.get(roomName);
        String[] gameRoomInfo = new String[4 + gr.getPlayerList().length];

        String points = String.valueOf(getUserPoints(username, roomName));

        gameRoomInfo[0] = String.valueOf(gr.getGameRoundTimer());
        gameRoomInfo[1] = points;
        gameRoomInfo[2] = String.valueOf(gr.getScoresRoomTimer());
        gameRoomInfo[3] = String.valueOf(gr.getCurrentRound());
        gameRoomInfo[4] = gr.getRandomLetters(Integer.parseInt(gameRoomInfo[3]));

        String[] players = gr.getPlayerList();
        if (gameRoomInfo.length < players.length + 5) {
            gameRoomInfo = Arrays.copyOf(gameRoomInfo, players.length + 5);
        }
        System.arraycopy(players, 0, gameRoomInfo, 5, players.length);
        return gameRoomInfo;
    }
}

