package Client_Java.clientgui.loginGUI;

import Client_Java.WordyApp.InvalidCredentials;
import Client_Java.WordyApp.UserActive;
import Client_Java.WordyApp.WordyGame;
import Client_Java.clientgui.mainGUI.HomePageGUI;

import javax.swing.*;
import java.awt.*;

public class ClientLoginGUI extends JFrame {

    private WordyGame wordObj;
    private ImageIcon bg;
    private JLabel wordyLabel, usernameLabel, passwordLabel, registerNote;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;

    public static String username;

    public ClientLoginGUI(WordyGame wordObj, Point guiLocation) {
        super("Wordy Login");

        this.wordObj = wordObj;
        setBackground(Color.gray);
        // Set up the GUI components
        wordyLabel = new JLabel("W o r d y");
        wordyLabel.setFont(new Font("Arial", Font.BOLD, 40));
        wordyLabel.setForeground(Color.white);

        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        usernameLabel.setForeground(Color.white);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 20));
        passwordLabel.setForeground(Color.white);

        usernameField = new JTextField(30);
        usernameField.setFont(new Font("Arial", Font.BOLD, 17));

        passwordField = new JPasswordField(30);
        passwordField.setFont(new Font("Arial", Font.BOLD, 17));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 20));

        registerNote = new JLabel("Not yet a player?");
        registerNote.setFont(new Font("Arial", Font.BOLD, 20));

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 20));

        bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        // Set the layout of the GUI
        setLayout(null);

        // Set the bounds of the GUI components
        wordyLabel.setBounds(0, 30, 500, 50);
        wordyLabel.setHorizontalAlignment(JLabel.CENTER);

        usernameLabel.setBounds(150, 100, 100, 30);
        usernameField.setBounds(150, 130, 180, 30);

        passwordLabel.setBounds(150, 170, 100, 30);
        passwordField.setBounds(150, 200, 180, 30);

        registerNote.setBounds(160,300,200,30);

        loginButton.setBounds(180, 250, 120, 30);
        registerButton.setBounds(180, 330, 120, 30);

        loginButton.setForeground(Color.blue);
        loginButton.setBackground(Color.yellow);
        registerNote.setForeground(Color.white);
        registerButton.setForeground(Color.blue);
        registerButton.setOpaque(false);

        loginButton.addActionListener(e -> {
            username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try {
                wordObj.login(username, password);
                JOptionPane.showMessageDialog(this, "Login successful!");
                Point location = getLocation();
                dispose();
                new HomePageGUI(wordObj, username, location);
            } catch (InvalidCredentials ex) {
                String errorMessage = ex.message;
                JOptionPane.showMessageDialog(this, errorMessage, "Invalid Credentials", JOptionPane.ERROR_MESSAGE);
            } catch (UserActive ex) {
                String errorMessage = ex.message;
                JOptionPane.showMessageDialog(this, errorMessage, "User Online", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e-> {
            dispose();
            new RegisterGUI(wordObj, getLocation());
        });

        // Add the components to the GUI
        add(wordyLabel);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerNote);
        add(registerButton);

        // Set the size and location of the GUI
        setSize(500, 450);
        setLocation(guiLocation);

        // Set the GUI to be visible
        setResizable(false);
        setVisible(true);
    }
}
