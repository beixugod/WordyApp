package Client_Java.clientgui.mainGUI;

import Client_Java.WordyApp.WordyGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static Client_Java.clientgui.loginGUI.ClientLoginGUI.username;

public class LeaderboardGUI extends JFrame {

    private ImageIcon bg;
    private JLabel lbLabel;
    private JButton topPlayerButton, topLongestWordButton, backButton;

    public LeaderboardGUI(WordyGame wordObj, Point guiLocation) {
        super("Wordy Leaderboard");
        setBackground(Color.gray);

        //Set up the GUI components
        lbLabel = new JLabel("L E A D E R B O A R D");
        lbLabel.setFont(new Font("Arial", Font.BOLD, 40));
        lbLabel.setForeground(Color.white);

        topPlayerButton = new JButton("Top 5 Players");
        topPlayerButton.setFont(new Font("Arial", Font.BOLD, 20));

        topLongestWordButton = new JButton("Top 5 Longest Words");
        topLongestWordButton.setFont(new Font("Arial", Font.BOLD, 20));

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 15));

        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        //Set the layout of the GUI
        setLayout(null);

        //Set the bounds of the GUI components
        lbLabel.setBounds(0, 10, 500, 50);
        lbLabel.setHorizontalAlignment(JLabel.CENTER);

        topPlayerButton.setBounds(150, 100, 200, 30);
        topPlayerButton.setForeground(Color.blue);
        topPlayerButton.setBackground(Color.yellow);

        topPlayerButton.addActionListener(e -> {
            dispose();
            new TopFiveGUI(wordObj, getLocation());
        });

        topLongestWordButton.setBounds(120, 150, 250, 30);
        topLongestWordButton.setForeground(Color.blue);
        topLongestWordButton.setBackground(Color.yellow);

        topLongestWordButton.addActionListener(e -> {
            Point location = getLocation();
            dispose();
            new LongestWordsGUI(wordObj, location);
        });

        backButton.setBounds(200, 200, 80, 20);
        backButton.setForeground(Color.black);
        backButton.setBackground(Color.gray);

        backButton.addActionListener(e -> {
            Point location = getLocation();
            dispose();
            new HomePageGUI(wordObj, username, location);
        });

        //go offline when window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                wordObj.logout(username);
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            wordObj.logout(username);
            System.exit(0);
        }));

        //Add the components to the GUI
        add(lbLabel);
        add(topPlayerButton);
        add(topLongestWordButton);
        add(backButton);

        // Set the size and location of the GUI
        setSize(500, 300);
        setLocation(guiLocation);

        // Set the GUI to be visible
        setResizable(false);
        setVisible(true);
    }
}
