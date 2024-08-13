package Server_Java.WordyServerHelper;

import java.sql.*;
import java.util.ArrayList;

public class ServerDataAccess {
    private static Connection con;
    public ServerDataAccess() {
        setUpConnection();
    }
    /**
     * The setUpConnection sets up the database connection
     */
    public static void setUpConnection() {
        String url = "jdbc:mysql://localhost:3306/wordy";
        String username = "root";
        String password = "";
        try {
            con = DriverManager.getConnection(url, username, password);
            System.out.println("SERVER: Server helper database connection successful.");
        } catch (SQLException e) {
            System.out.println("SERVER: Server helper database connection failed. " + e);
            System.exit(0);
        }
    }

    /**
     * The getLongestWordLength method takes an userID as input, and retrieves the longest word formed by the user in a
     * game session from the database. If the longest word is not null, the length of the word is returned. Otherwise,
     * the method returns 0. This  uses a SQL SELECT statement with a prepared statement to retrieve the longestWord
     * field from the game_session table where the userID matches the input userID. The retrieved result set is checked
     * to see if there is a longest word value present, and if so, its length is returned. If there is no longest word
     * present, the method returns 0.
     * @param username
     * @return
     */
    public int getLongestWordLength(String username) {
        String query = "SELECT longestWord FROM game_session INNER JOIN users using (userID) " +
                "WHERE users.username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            String longestWord;
            stmt.setString(1, username);
            // execute the SQL statement and retrieve the results
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                longestWord = rs.getString("longestWord");
                if (longestWord != null) {
                    return longestWord.length();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * The updateLongestWordFormed method updates the longest word in the game_session table of the database for a
     * specific user in a specific room with a new value provided.
     * @param username
     * @param wordFormed
     * @param roomName
     */
    public void updateLongestWordFormed(String username, String wordFormed, String roomName) {
        String query = "UPDATE game_session AS gs " +
                "INNER JOIN users AS u USING (userID) " +
                "SET gs.longestWord = ? " +
                "WHERE gs.roomName = ? AND u.username = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, wordFormed);
            statement.setString(2, roomName);
            statement.setString(3, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    /**
     * The checkUserIDInDatabase method checks if a given userID already exists in the leaderboard table of the database
     * @param username
     * @return
     */
    public boolean checkUserIDInDatabase(String username) {
        String query = "SELECT userID FROM userleaderboard INNER JOIN users USING (userID) " +
                "WHERE users.username = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        return false;
    }
    // Helper method to get the user's longest word
    public String getUserLongestWord(String username, String roomName) {
        String query = "SELECT longestWord FROM game_session INNER JOIN users USING (userID)" +
                " WHERE users.userName = ? AND roomName = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, roomName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("longestWord");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Helper method to get the user's current points
    public static int getUserPoints(String username, String roomName) {
        int points = 0;
        String query = "SELECT pointsAchieved FROM game_session INNER JOIN users USING (userID) WHERE users.username = ? AND roomName = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, roomName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                points = rs.getInt("pointsAchieved");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }
    // Helper method to get the longest word length from other users in the room
    public int getLongestWordLength(String username, String roomName) {
        int longestWordLength = 0;
        String query = "SELECT longestWord FROM game_session INNER JOIN users USING (userID) WHERE users.username != ? AND roomName = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, roomName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String longestWords = rs.getString("longestWord");
                if (longestWords != null && longestWords.length() > longestWordLength) {
                    longestWordLength = longestWords.length();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return longestWordLength;
    }
    // Helper method to update the user's points
    public void updateUserPoints(int points, String username, String roomName) {
        String query = "UPDATE game_session AS gs INNER JOIN users AS u USING (userID) SET gs.pointsAchieved = ? WHERE u.username = ? AND gs.roomName = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, points);
            stmt.setString(2, username);
            stmt.setString(3, roomName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * The setLongestWordToNull method updates the longestWord column in the game_session table in the database
     * to null for the given user ID and room name every new round.
     * @param roomName
     */
    public void setLongestWordToNull(String roomName) {
        String query1 = "UPDATE game_session SET longestWord = 'NULL' WHERE roomName = ?";
        try (PreparedStatement statement = con.prepareStatement(query1)) {
            statement.setString(1, roomName);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    /**
     * The checkPointsAchieved method retrieves the points achieved by all players in a specified room from the
     * database and checks if any player has achieved 3 points. If so, it returns true, indicating that the game
     * in the room should end. Otherwise, it returns false, indicating that the game should continue.
     * @param roomName
     * @return
     */
    public boolean checkPointsAchieved(String roomName) {
        ArrayList<String> userIDList = new ArrayList<>();
        String query = "SELECT pointsAchieved, userID FROM game_session WHERE roomName = ?";
        try (PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, roomName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String points = resultSet.getString("pointsAchieved");
                String userID = resultSet.getString("userID");
                if (points != null && points.contains("3")) {
                    return true;
                }
                userIDList.add(userID);
            }
            if (userIDList.size() < 2) {
                return true;
            }
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The updateMatchesWon method retrieves the number of matches won by a user with a given userID from the leaderboard
     * table, increments the value by 1, and updates the matchesWon column of that user in the leaderboard table
     * @param username
     */
    public void updateMatchesWon(String username) {
        String query1 = "SELECT matchesWon FROM userleaderboard INNER JOIN users USING (userID) " +
                "WHERE users.username = ?";
        try (PreparedStatement statement1 = con.prepareStatement(query1)) {
            statement1.setString(1, username);
            ResultSet resultSet = statement1.executeQuery();
            if (resultSet.next()) {
                int matchesWon = resultSet.getInt("matchesWon");
                matchesWon = matchesWon + 1;
                String query2 = "UPDATE userleaderboard INNER JOIN users USING (userID) " +
                        "SET matchesWon = ? WHERE username = ?";
                PreparedStatement statement2 = con.prepareStatement(query2);
                statement2.setInt(1, matchesWon);
                statement2.setString(2, username);
                statement2.executeUpdate();
            }
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }
    }


    public static void insertIntoLeaderboardTable(String username, String longestWord, int matchesWon) {
        String query1 = "INSERT INTO leaderboard (userID, longestWord, matchesWon) " +
                "SELECT userID, ?, ? " +
                "FROM users " +
                "WHERE username = ?";
        try (PreparedStatement stmt = con.prepareStatement(query1)) {
            stmt.setString(1, longestWord);
            stmt.setInt(2, matchesWon);
            stmt.setString(3, username);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void compareWordFormedToLongestWord(String username, String wordFormed) {
        String query1 = "SELECT longestWord FROM leaderboard INNER JOIN users USING (userID) " +
                "WHERE users.username = ?";
        try (PreparedStatement stmt1 = con.prepareStatement(query1)) {
            String longestWord;
            stmt1.setString(1, username);
            // execute the SQL statement and retrieve the results
            ResultSet rs1 = stmt1.executeQuery();

            if (rs1.next()) {
                longestWord = rs1.getString("longestWord");
                if (longestWord.length() <= wordFormed.length()) {
                    String query2 = "UPDATE leaderboard INNER JOIN users USING (userID) " +
                            "SET longestWord = ? WHERE username = ?";
                    try (PreparedStatement stmt2 = con.prepareStatement(query2)) {
                        stmt2.setString(1, wordFormed);
                        stmt2.setString(2, username);
                        stmt2.executeUpdate();
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
