package Client_Java.clientgui.mainGUI;

import Client_Java.WordyApp.WordyGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static Client_Java.clientgui.loginGUI.ClientLoginGUI.username;

public class LongestWordsGUI extends JFrame {

    private WordyGame wordObj;

    private ImageIcon bg;
    private JLabel wordyLabel;
    private JButton backButton;

    private JScrollPane longestWordsPane;

    private JList longestWordsList;

    private String[] longestWordArray;

    public LongestWordsGUI(WordyGame wordObj, Point guiLocation) {
        super("Wordy Login");
        setBackground(Color.gray);
        this.wordObj = wordObj;

        // Set up the GUI components
        wordyLabel = new JLabel("Top 5 Longest Words");
        wordyLabel.setFont(new Font("Arial", Font.BOLD, 40));
        wordyLabel.setForeground(Color.white);

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));

        longestWordsPane = new JScrollPane();

        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        // Set the layout of the GUI
        setLayout(null);

        // Set the bounds of the GUI components
        wordyLabel.setBounds(50, 30, 500, 50);
        wordyLabel.setHorizontalAlignment(JLabel.CENTER);

        longestWordArray = wordObj.getFiveLongestWords();
        if (longestWordArray.length == 0) {
            // No words recorded, displays that no records are read from the database
            JLabel noWordsLabel = new JLabel("No words recorded");
            noWordsLabel.setFont(new Font("Arial", Font.BOLD, 30));
            noWordsLabel.setHorizontalAlignment(JLabel.CENTER);
            longestWordsPane.setViewportView(noWordsLabel);
        } else {
            longestWordsList = new JList(longestWordArray);
            longestWordsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            longestWordsList.setEnabled(false);
            longestWordsList.setFont(new Font("Arial", Font.BOLD, 30));
            longestWordsList.setBackground(Color.YELLOW);
            longestWordsList.setOpaque(false);
            DefaultListCellRenderer renderer = (DefaultListCellRenderer) longestWordsList.getCellRenderer();
            renderer.setHorizontalAlignment(JLabel.CENTER);

            longestWordsPane.setViewportView(longestWordsList);
        }
        longestWordsPane.setBounds(80,120,430,220);
        longestWordsPane.setOpaque(false);

        backButton.setBounds(195,370,200,30);
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
        add(longestWordsPane);
        add(wordyLabel);
        add(backButton);

        // Set the size and location of the GUI
        setSize(600, 500);
        setLocation(guiLocation);

        // Set the GUI to be visible
        setResizable(false);
        setVisible(true);

    }
}
