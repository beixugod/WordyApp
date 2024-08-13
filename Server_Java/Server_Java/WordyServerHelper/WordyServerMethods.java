package Server_Java.WordyServerHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordyServerMethods {

    public static String produceRandomLetters() {
        String VOWELS = "AEIOU";
        String CONSONANTS = "BCDFGHJKLMNPQRSTVWXYZ";

        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        int vowelCount = 0;
        char lastVowel = '\0';
        int lastVowelCount = 0;

        for (int i = 0; i < 17; i++) {
            if (vowelCount < 5 || vowelCount < 7 && random.nextBoolean()) {
                // Generate a vowel
                char c;
                do {
                    int index = random.nextInt(VOWELS.length());
                    c = VOWELS.charAt(index);
                } while (c == lastVowel && lastVowelCount >= 2);
                sb.append(c);
                vowelCount++;
                if (c == lastVowel) {
                    lastVowelCount++;
                } else {
                    lastVowel = c;
                    lastVowelCount = 1;
                }
            } else {
                // Generate a consonant
                int index = random.nextInt(CONSONANTS.length());
                char c = CONSONANTS.charAt(index);
                sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String generateRandomRoomname() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCSDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static void insertDataIntoLeaderboard(String username, String longestWord, Connection con) {
        String query1 = "INSERT INTO leaderboardwords (word, userID) VALUES (?, ?)";

        // Fetch userID based on the username
        String query2 = "SELECT userID FROM users WHERE username = ?";

        try (PreparedStatement stmt = con.prepareStatement(query2)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("userID");

                try (PreparedStatement stmt2 = con.prepareStatement(query1)) {
                    stmt2.setString(1, longestWord);
                    stmt2.setInt(2, userID);
                    stmt2.execute();
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertUserIDInUserLeaderboard(String username, int matchesWon, Connection con) {
        String query1 = "INSERT INTO userleaderboard (userID, matchesWon) VALUES (?, ?)";

        // Fetch userID based on the username
        String query2 = "SELECT userID FROM users WHERE username = ?";

        try (PreparedStatement stmt = con.prepareStatement(query2)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userID = rs.getInt("userID");

                try (PreparedStatement stmt2 = con.prepareStatement(query1)) {
                    stmt2.setInt(1, userID);
                    stmt2.setInt(2, matchesWon);
                    stmt2.execute();
                }
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String[] readWordsData() {
        ArrayList<String> data = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("CORBA/src/res/words.txt"));

            String line = br.readLine();
            while (line != null) {
                data.add(line);
                line = br.readLine();
            }
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return data.toArray(new String[0]);
    }

    public static String joinIntegers(List<Integer> integers) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < integers.size(); i++) {
            sb.append(integers.get(i));
            if (i < integers.size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
