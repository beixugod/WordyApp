package Client_Java.clientgui.mainGUI;

import Client_Java.WordyApp.WordyGame;
import Client_Java.clientgui.gameGUI.LoadingGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class WaitingRoomGUI extends JFrame {
    private final JLabel playerListLabel, timerLabel, roomNameLabel, waitingLabel;
    private String[] players;
    private final WordyGame wordyObj;
    private Semaphore loadingSemaphore;
    Timer timer = new Timer(1000, null);
    private String roomName = "temp";
    private static int waitingLabelIndex;
    String username;
    public WaitingRoomGUI(WordyGame wordObj, String username, Point guiLocation) {
        super("Wordy Start");

        wordyObj = wordObj;

        this.username = username;

        setBackground(Color.gray);

        //Create and display a new JFrame
        JLabel gameLabel = new JLabel("LET'S PLAY! ");
        gameLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gameLabel.setForeground(Color.white);

        JLabel playerLabel = new JLabel("P L A Y E R S: ");
        playerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        playerLabel.setForeground(Color.white);

        ImageIcon bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        //Set the layout of the GUI
        setLayout(null);

        //Set the bounds of the GUI components
        gameLabel.setBounds(0, 10, 500, 50);
        gameLabel.setHorizontalAlignment(JLabel.CENTER);

        waitingLabel = new JLabel("Waiting room in session...");
        waitingLabel.setBounds(110, 80, 500, 30);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        waitingLabel.setForeground(Color.white);
        waitingLabelIndex = 1;

        playerLabel.setBounds(0, 120, 500, 50);
        playerLabel.setHorizontalAlignment(JLabel.CENTER);

        playerListLabel = new JLabel("The players are: " + Arrays.toString(players));
        playerListLabel.setBounds(100, 150,500,50);
        playerListLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        playerListLabel.setForeground(Color.white);

        timerLabel = new JLabel("Timer: ");
        timerLabel.setBounds(0, 200,500,50);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBackground(Color.RED);

        roomNameLabel = new JLabel();
        roomNameLabel.setBounds(20, 10,500,50);
        roomNameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        roomNameLabel.setForeground(Color.white);

        JButton homeButton = new JButton("Back to Home");
        homeButton.setBounds(190,270,125,30);
        homeButton.setForeground(Color.blue);
        homeButton.setBackground(Color.yellow);
        //list the players
        populatePlayerList();
        loadingSemaphore = new Semaphore(players.length);
        //starts the timer that automatically updates the table of player names
        startUpdateTimer();

        //begin the list countdown
        homeButton.addActionListener(e -> {
            wordObj.leaveGameRoom(username, roomName);
            timer.stop();
            Point location = getLocation();
            dispose();
            new HomePageGUI(wordObj, username, location);
        });

        //go offline when window is closed
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


        //Add the components to the GUI
        add(playerLabel);
        add(gameLabel);
        add(waitingLabel);
        add(playerListLabel);
        add(homeButton);
        add(timerLabel);
        add(roomNameLabel);

        //Set the size and location of the GUI
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 350);
        setLocation(guiLocation);

        //Set the GUI to be visible
        setResizable(false);
        setVisible(true);
    }
    private void startUpdateTimer() {
        timer.addActionListener(e -> {
            populatePlayerList();
            playerListLabel.setText("The players are: " + Arrays.toString(players));

            if(waitingLabelIndex % 2 == 0) {
                waitingLabel.setText("Waiting room in session. . .");
            } else {
                waitingLabel.setText("Waiting room in session . .");
            }
            waitingLabelIndex++;

            String[] status = wordyObj.getWaitingRoomInformation();
            timerLabel.setText("Timer: " + status[1]);
            timerLabel.setHorizontalAlignment(JLabel.CENTER);
            roomName = status[2];
            roomNameLabel.setText("Room: " + roomName);

            switch (status[0]) {
                case "Waiting room invalid":
                    timer.stop();
                    JOptionPane.showMessageDialog(null, "The waiting room does not have" +
                            " enough players! Returning to main menu.", "Waiting Room Failed", JOptionPane.ERROR_MESSAGE);
                    dispose();
                    wordyObj.leaveGameRoom(username, roomName);
                    new HomePageGUI(wordyObj, username, getLocation());
                    break;
                case "Waiting room ready":
                    timer.stop();
                    dispose();
                    try {
                        loadingSemaphore.acquire();
                        new LoadingGUI(wordyObj, username, status[2], getLocation());
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } finally {
                        loadingSemaphore.release();
                    }
                    break;
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }
    private void populatePlayerList() {
        String[] roomInfo = wordyObj.getWaitingRoomInformation();

        players = Arrays.copyOfRange(roomInfo, 3, roomInfo.length);
    }
}