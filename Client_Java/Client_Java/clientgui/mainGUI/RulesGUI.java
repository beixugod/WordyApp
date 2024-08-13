package Client_Java.clientgui.mainGUI;

import Client_Java.WordyApp.WordyGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.awt.Font;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static Client_Java.clientgui.loginGUI.ClientLoginGUI.username;

public class RulesGUI extends JFrame {
    private static WordyGame wordObj;
    private static final long serialVersionUID = 1L;
    private JTextArea textArea;
    private ImageIcon bg;
    private JButton backButton;

    public RulesGUI(WordyGame wordyObj, Point guiLocation) {
        super("Rules");

        setBackground(Color.gray);
        wordObj = wordyObj;

        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        // Set the layout of the GUI
        setLayout(null);

        //Set the size and location of the GUI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400);
        setLocation(guiLocation);

        //Set the GUI to be visible
        setResizable(false);
        setVisible(true);

        //Back Button
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD,15));
        backButton.setBounds(225, 335, 80, 20);
        backButton.setForeground(Color.black);
        backButton.setBackground(Color.gray);

        backButton.addActionListener(e -> {
            dispose();
            new HomePageGUI(wordObj, username, getLocation());
        });
        add(backButton);

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

        //Create the text area
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setOpaque(false);

        //Set the font of the text
        Font font = new Font("Arial", Font.BOLD, 15);
        textArea.setFont(font);

        //Create a scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(20,10,500,315);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        //Add the scroll pane to the frame
        Container contentPane = getContentPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);

        //Read the file and display the Rules
        String filePath = "CORBA\\src\\res\\WordyRules.txt";
        displayFileContent(filePath);
    }

    private void displayFileContent(String filePath) {
        File file = new File(filePath);

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) !=null) {
                textArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
