package Client_Java.clientgui.gameGUI;

import Client_Java.WordyApp.WordyGame;
import Client_Java.clientgui.mainGUI.HomePageGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class ResultGUI extends JFrame {
    private ImageIcon bg;
    private JLabel round, nextRoundLabel, sec;
    private JPanel panel;

    private Timer timer;
    private int remainingTime = 11;
    private static WordyGame wordObj;
    private static String username, roomName = "temp";
    private String[] playersArray;
    public ResultGUI(WordyGame wordObj, String rn, String username, Point guiLocation) {
        ResultGUI.wordObj = wordObj;
        ResultGUI.username = username;
        ResultGUI.roomName = rn;
        // Set the size and location of the GUI
        setSize(800, 600);
        setLocation(guiLocation);
        setBackground(Color.gray);
        // Set the background color of the JFrame
        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));



        String[] roomInfo = wordObj.getGameRoomInformation(roomName, username);
        int playerScore = Integer.parseInt(roomInfo[1]);
        populatePlayersArray();

        // Create the round label
        if (playerScore == 3) {
            round = new JLabel("You Win!");
        } else if (playersArray.length < 2) {
            round = new JLabel("You Win!");
        } else {
            round = new JLabel("You Lose!");
        }
        round.setFont(new Font("Times New Roman", Font.BOLD, 40));

        // Create the panel
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        sec = new JLabel();
        sec.setFont(new Font("Arial", Font.BOLD, 15));
        timer = new Timer();
        timer.scheduleAtFixedRate(new CountdownTask(), 0, 1000);

        // Create the next round label
        nextRoundLabel = new JLabel("Thank you for playing");
        nextRoundLabel.setFont(new Font("Arial", Font.PLAIN, 15));

        // Create a panel to hold the nextRoundLabel with FlowLayout
        JPanel nextRoundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nextRoundPanel.add(nextRoundLabel);
        nextRoundPanel.add(sec);

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

            String[] arrayTemp = wordObj.getGameRoomInformation(roomName, player);
            int score = Integer.parseInt(arrayTemp[1]);

            JLabel scoreLabel = new JLabel(Integer.toString(score));
            scoreLabel.setFont(new Font("Arial", Font.BOLD, 20));
            scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            scoresPanel.add(scoreLabel);
        }

        // Add the nextRoundPanel to the panel's south position
        panel.add(nextRoundPanel, BorderLayout.SOUTH);
        panel.add(scoresPanel, BorderLayout.CENTER);

        round.setBounds(317, -18, 350, 120);
        panel.setBounds(93, 80, 600, 400);

        add(round);
        add(panel);

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
        setResizable(false);
        setVisible(true);
    }
    private class CountdownTask extends TimerTask {
        @Override
        public void run() {
            remainingTime--;
            sec.setText(Integer.toString(remainingTime));
            if (remainingTime == 0) {
                timer.cancel();

                // Create the Main Menu button
                JButton mainMenuButton = new JButton("Main Menu");
                mainMenuButton.setFont(new Font("Arial", Font.PLAIN, 15));
                mainMenuButton.addActionListener(e -> {
                    dispose();
                    wordObj.leaveGameRoom(username, roomName);
                    new HomePageGUI(wordObj, username, getLocation());
                });

                // Remove the nextRoundLabel from the nextRoundPanel
                JPanel nextRoundPanel = (JPanel) panel.getComponent(0);
                if (nextRoundPanel.getComponentCount() > 1) {
                    nextRoundPanel.remove(nextRoundLabel);
                    nextRoundPanel.remove(remainingTime);
                }

                // Add the Main Menu button to the nextRoundPanel
                nextRoundPanel.add(mainMenuButton);

                // Refresh the panel
                nextRoundPanel.revalidate();
                nextRoundPanel.repaint();
            }
        }
    }
    private void populatePlayersArray() {
        String[] roomInfo = wordObj.getGameRoomInformation(roomName, username);

        playersArray = Arrays.copyOfRange(roomInfo, 5, roomInfo.length);
    }
}
