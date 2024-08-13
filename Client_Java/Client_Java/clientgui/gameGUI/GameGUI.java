package Client_Java.clientgui.gameGUI;

import Client_Java.WordyApp.WordyGame;
import Client_Java.clientgui.mainGUI.HomePageGUI;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;


public class GameGUI extends JFrame {
    private ImageIcon bg;
    private JLabel round, user, time, score, word, sec, validation = new JLabel();
    private JButton shuffleButton;
    private JTextField ans;
    private Timer timer;
    private static WordyGame wordObj;
    private static String username, roomName, firstLetterLine, secondLetterLine;
    public GameGUI(WordyGame obj, String username, String roomName, Point guiLocation) {
        wordObj = obj;
        GameGUI.username = username;
        GameGUI.roomName = roomName;
        setBackground(Color.gray);

        String[] roomInfo = wordObj.getGameRoomInformation(roomName, username);
        String points = roomInfo[1];

        // Set the size and location of the GUI
        setSize(800, 650);
        setLocation(guiLocation);

        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new CountdownTask(), 0, 1000);

        //set frame title to be room name
        setTitle(roomName);

        // Set the background color of the JFrame
        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        round = new JLabel("Round ");
        round.setFont(new Font("Times New Roman", Font.BOLD, 40));

        user = new JLabel(username);
        user.setFont(new Font("Times New Roman", Font.BOLD, 23));

        time = new JLabel("Timer");
        time.setFont(new Font("Times New Roman", Font.BOLD, 30));

        sec = new JLabel("10"); //edit the 10
        sec.setFont(new Font("Times New Roman", Font.BOLD, 30));


        score = new JLabel("Score: " + points);
        score.setFont(new Font("Times New Roman", Font.BOLD, 23));

        shuffleButton = new JButton("Shuffle");
        shuffleButton.setFont(new Font("Times New Roman", Font.BOLD, 17));
        shuffleButton.setBackground(Color.YELLOW);
        shuffleButton.setForeground(Color.BLUE);

        word = new JLabel("<html><div style='text-align:center;'>" + firstLetterLine +
                "<br><span style='display:inline-block;width:130px;text-align:center;'>" +
                secondLetterLine + "</span></div></html>");
        word.setFont(new Font("Times New Roman", Font.BOLD, 50));
        word.setVerticalAlignment(JLabel.TOP);

        String randomLetters = roomInfo[4];
        shuffleLetters(randomLetters);

        class UpperCaseDocument extends PlainDocument {  // CAPSLOCK
            @Override
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                super.insertString(offs, str.toUpperCase(), a); // Convert text to upper case
            }
        }

        //DEVELOPER TOOL
        if(username.equals("admin")) {
            enableCheats(randomLetters);
        }

        ans = new JTextField();
        ans.setFont(new Font("Arial", Font.PLAIN, 20));
        ans.setDocument(new UpperCaseDocument()); // Set the custom document
        ans.setHorizontalAlignment(JTextField.CENTER);
        ans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String answer = ans.getText();

                if(!sec.getText().equals("0")) {
                    if (wordObj.submitWord(answer, username, Integer.parseInt(roomInfo[3]), roomName)) {
                        getContentPane().remove(validation);
                        validation = new JLabel("ANSWER IS VALID");
                        validation.setFont(new Font("Arial", Font.BOLD, 15));
                        validation.setForeground(Color.GREEN);
                        validation.setBounds(340, 500, 550, 45);
                        add(validation);
                    } else {
                        getContentPane().remove(validation);
                        validation = new JLabel("INVALID WORD FORMED");
                        validation.setFont(new Font("Arial", Font.BOLD, 15));
                        validation.setForeground(Color.RED);
                        validation.setBounds(300, 500, 550, 45);
                        add(validation);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Sorry, the round time is up!", "Time is up!", JOptionPane.ERROR_MESSAGE);
                }

                ans.setText("");
                // Refresh the JFrame to show the JLabel
                getContentPane().validate();
                getContentPane().repaint();
            }
        }) ;

        shuffleButton.addActionListener(e -> shuffleLetters(randomLetters)) ;

        round.setBounds(320, -25, 350, 120);
        user.setBounds(10, -40, 200, 120);
        score.setBounds(685, -40, 200, 120);

        time.setBounds(357, 70, 200, 120);
        sec.setBounds(387, 105, 200, 120);


        word.setBounds(160, 230, 800, 120);

        ans.setBounds(120, 430, 550, 45);
        shuffleButton.setBounds(570, 395, 100, 25);

        add(round);
        add(user);
        add(time);
        add(score);
        add(sec);
        add(word);
        add(ans);
        add(shuffleButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                wordObj.logout(username);
                wordObj.leaveGameRoom(username, roomName);
                System.exit(0);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            wordObj.logout(username);
            wordObj.leaveGameRoom(username, roomName);
            System.exit(0);
        }));

        // Set the GUI to be visible
        setResizable(true);
        setVisible(true);
    }

    private class CountdownTask extends TimerTask {
        String previousTimeValue = "";
        int repetitionCount = 0;
        @Override
        public void run() {
            String[] roomInfo = wordObj.getGameRoomInformation(roomName, username);
            String currentTimeValue = roomInfo[0];

            //Checker to see if the timer repeats itself seven times in a row
            //Brings back to main menu if so
            if (currentTimeValue.equals(previousTimeValue)) {
                repetitionCount++;
                if (repetitionCount == 7) {
                    JOptionPane.showMessageDialog(null, "The game has unexpectedly ended! " +
                            " Returning to main menu.", "Game Room Ended", JOptionPane.ERROR_MESSAGE);
                    dispose();
                    new HomePageGUI(wordObj, username, getLocation());
                    timer.cancel();
                }
            } else {
                repetitionCount = 1; //Reset repetition count
            }
            previousTimeValue = currentTimeValue;

            sec.setText(roomInfo[0]);

            round.setText("Round " + roomInfo[3]);

            if (currentTimeValue.equals("0")) {
                timer.cancel();
                Point location = getLocation();
                if (!wordObj.endRound(username, roomName)) {
                    dispose();
                    new ScoresGUI(wordObj, username, roomName, location);
                } else {
                    dispose();
                    new ResultGUI(wordObj, roomName, username, location);
                    }
            }
        }
    }

    private void shuffleLetters(String randomLetters) {

        char[] lettersArray = randomLetters.toCharArray();

        //THE FISHER-YALES SHUFFLE ALGORITHM
        Random rand = new Random();
        for (int i = lettersArray.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            char temp = lettersArray[index];
            lettersArray[index] = lettersArray[i];
            lettersArray[i] = temp;
        }

        firstLetterLine = new String(lettersArray, 0, 10);
        secondLetterLine = new String(lettersArray, 10, 7);

        firstLetterLine = firstLetterLine.replaceAll("", " ").trim();
        secondLetterLine = secondLetterLine.replaceAll("", " ").trim();

        word.setText("<html><div style='text-align:center;'>" + firstLetterLine +
                "<br><span style='display:inline-block;width:130px;text-align:center;'>" +
                secondLetterLine + "</span></div></html>");
    }

    /*
    ONLY DEVELOPER ASSETS FROM HERE ON
    */
    private static String[] arrayOfValidWords;
    private static ArrayList<String> arrayOfCheatedAnswers;
    private static int cheatArrayIndex = 0,  printCheatWordsIndex = 0;
    private void enableCheats(String randomLetters) {

        JButton cheatModeButton = new JButton("HACK");
        cheatModeButton.setFont(new Font("Times New Roman", Font.BOLD, 17));
        cheatModeButton.setBackground(Color.RED);
        cheatModeButton.setForeground(Color.WHITE);
        cheatModeButton.setBounds(570, 365, 100, 25);
        add(cheatModeButton);
        /*
        cheat mode populates an array of correct answers and sets the ans field to the first array word.
        if user presses enter then it should populate the next word. press cheatmode once
         */
        cheatModeButton.addActionListener(e -> {
            scanTextFile();
            arrayOfCheatedAnswers = new ArrayList<>();
            for (String word : arrayOfValidWords) {
                boolean canFormWord = canFormWordFromLetters(word, randomLetters);
                if (canFormWord) {
                    if((printCheatWordsIndex % 20 - 1) == 0) {
                        System.out.print(word);
                    } else if (printCheatWordsIndex % 20 == 0) {
                        System.out.println(word + ", ");
                    } else {
                        System.out.print(word + ", ");
                    }
                    printCheatWordsIndex++;
                    arrayOfCheatedAnswers.add(word);
                }
            }
            showHackedAnswers();
            getContentPane().validate();
            getContentPane().repaint();
        });
    }
    private void scanTextFile() {
        ArrayList<String> listOfStrings = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader("CORBA/src/res/words.txt"))) {
            String line = bf.readLine();

            while (line != null) {
                listOfStrings.add(line);
                line = bf.readLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Admin files not found, cannot activate cheats", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        arrayOfValidWords = listOfStrings.toArray(new String[0]);
    }
    private void showHackedAnswers() {
        if (cheatArrayIndex < arrayOfCheatedAnswers.size()) {
            ans.setText(arrayOfCheatedAnswers.get(cheatArrayIndex));
            cheatArrayIndex++;
        }
    }
    //Check if a word can be formed from the random letters
    private static boolean canFormWordFromLetters(String word, String letters) {
        // Convert the word and letters to lowercase
        word = word.toLowerCase();
        letters = letters.toLowerCase();

        //count char for letters
        int[] letterCount = new int[26];
        for (char c : letters.toCharArray()) {
            letterCount[c - 'a']++;
        }

        //can word be formed from the letters
        for (char c : word.toCharArray()) {
            if (letterCount[c - 'a'] <= 0) {
                return false;
            }
            letterCount[c - 'a']--;
        }
        return true;
    }
}

