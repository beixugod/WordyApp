package Client_Java.clientgui.gameGUI;

import Client_Java.WordyApp.WordyGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ScoresGUI extends JFrame {
    private ImageIcon bg;
    private JLabel round, nextRoundLabel, sec;
    private JPanel panel;
    private Timer timer;
    private static WordyGame wordObj;
    private static String username;
    private static String roomName;
    private static String[] roomInfo, playersArray;


    public ScoresGUI(WordyGame wordObj, String username, String roomName, Point location) {
        ScoresGUI.wordObj = wordObj;
        ScoresGUI.username = username;
        ScoresGUI.roomName = roomName;
        setBackground(Color.gray);
        // Set the size and location of the GUI
        setSize(800, 600);
        setLocation(location);

        // Set the background color of the JFrame
        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        roomInfo = wordObj.getGameRoomInformation(roomName, username);
        // Create the round label
        round = new JLabel("Round " + roomInfo[3]);
        round.setFont(new Font("Times New Roman", Font.BOLD, 40));

        // Create the panel
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        sec = new JLabel("10");
        sec.setFont(new Font("Arial", Font.BOLD, 15));
        timer = new Timer();
        timer.scheduleAtFixedRate(new CountdownTask(), 0, 1000);

        // Create the next round label
        nextRoundLabel = new JLabel("Next round starts in");
        nextRoundLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // Create a panel to hold the nextRoundLabel with FlowLayout
        JPanel nextRoundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextRoundPanel.add(nextRoundLabel);
        nextRoundPanel.add(sec);

        populatePlayersArray();

        // Create a panel to hold the player scores with GridLayout
        JPanel scoresPanel = new JPanel(new GridLayout(playersArray.length, 2));
        scoresPanel.setBackground(Color.LIGHT_GRAY);
        for (String player : playersArray) {
            JLabel playerLabel = new JLabel(player);
            playerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            playerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if (player.equals(username)) {
                playerLabel.setForeground(Color.decode("#3d5a80"));  // Set the color to #98c1d9 for the logged-in player
            }
            scoresPanel.add(playerLabel);

            String[] temp = wordObj.getGameRoomInformation(roomName, player);
            String playerScore = temp[1];

            JLabel scoreLabel = new JLabel(playerScore);
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            if (player.equals(username)) {
                scoreLabel.setForeground(Color.decode("#3d5a80"));  // Set the color to #98c1d9 for the logged-in player's score
            }
            scoresPanel.add(scoreLabel);
        }

        // Add the nextRoundPanel to the panel's south position
        panel.add(nextRoundPanel, BorderLayout.SOUTH);
        panel.add(scoresPanel, BorderLayout.CENTER);

        round.setBounds(317, -18, 350, 120);
        panel.setBounds(93, 80, 600, 400);

        add(round);
        add(panel);

        // Set the GUI to be visible
        setResizable(false);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                wordObj.leaveGameRoom(username, roomName);
                wordObj.logout(username);
                System.exit(0);
            }
        });
    }
    class CountdownTask extends TimerTask {
        @Override
        public void run() {

            String[] roomInfo = wordObj.getGameRoomInformation(roomName, username);

            sec.setText(roomInfo[2]);

            if (roomInfo[2].equals("0")) {
                timer.cancel();
                dispose();
                new GameGUI(wordObj, username, roomName, getLocation());
            }
        }
    }
    private void populatePlayersArray() {
        String[] roomInfo = wordObj.getGameRoomInformation(roomName, username);

        playersArray = Arrays.copyOfRange(roomInfo, 5, roomInfo.length);
    }
}


