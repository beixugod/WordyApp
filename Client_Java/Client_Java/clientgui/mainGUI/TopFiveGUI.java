package Client_Java.clientgui.mainGUI;

import Client_Java.WordyApp.WordyGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static Client_Java.clientgui.loginGUI.ClientLoginGUI.username;

public class TopFiveGUI extends JFrame {

    private ImageIcon bg;
    private JLabel wordyLabel;
    private JButton backButton;

    private JScrollPane topFivePane;

    private JList topFiveUsersList;

    private String[] topFiveUsers;

    public TopFiveGUI(WordyGame wordObj, Point location) {
        super("Wordy Login");
        setBackground(Color.gray);
        setLocation(location);
        // Set up the GUI components
        wordyLabel = new JLabel("Top 5 Players!");
        wordyLabel.setFont(new Font("Arial", Font.BOLD, 40));
        wordyLabel.setForeground(Color.white);

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));

        topFivePane = new JScrollPane();

        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        // Set the layout of the GUI
        setLayout(null);

        // Set the bounds of the GUI components
        wordyLabel.setBounds(0, 30, 500, 50);
        wordyLabel.setHorizontalAlignment(JLabel.CENTER);

        topFiveUsers = wordObj.getTopFivePlayers();
        if (topFiveUsers.length == 0) {
            // No users recorded, displays that no records are read from the database
            JLabel noWordsLabel = new JLabel("No players were found");
            noWordsLabel.setFont(new Font("Arial", Font.BOLD, 30));
            noWordsLabel.setHorizontalAlignment(JLabel.CENTER);
            topFivePane.setViewportView(noWordsLabel);
        }else {
            topFiveUsersList = new JList(topFiveUsers);
            topFiveUsersList.setOpaque(false);
            topFiveUsersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            topFiveUsersList.setEnabled(false);
            topFiveUsersList.setFont(new Font("Arial", Font.BOLD, 30));
            topFiveUsersList.setBackground(Color.YELLOW);
            topFiveUsersList.setOpaque(false);
            DefaultListCellRenderer renderer = (DefaultListCellRenderer) topFiveUsersList.getCellRenderer();
            renderer.setHorizontalAlignment(JLabel.CENTER);
            topFivePane.setViewportView(topFiveUsersList);
        }


        topFivePane.setBounds(80,120,340,200);
        topFivePane.setOpaque(false);
        topFivePane.getViewport().setOpaque(false);

        backButton.setBounds(150,350,200,30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setOpaque(false);
        backButton.setForeground(Color.yellow);

        backButton.addActionListener(e-> {
            dispose();
            new LeaderboardGUI(wordObj, getLocation());
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

        // Add the components to the GUI
        add(topFivePane);
        add(wordyLabel);
        add(backButton);

        //Set the size and location of the GUI
        setSize(500, 450);

        //Set the GUI to be visible
        setResizable(false);
        setVisible(true);
    }
}
