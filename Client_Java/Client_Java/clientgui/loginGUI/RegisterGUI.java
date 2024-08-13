package Client_Java.clientgui.loginGUI;

import Client_Java.WordyApp.WordyGame;

import javax.swing.*;
import java.awt.*;

public class RegisterGUI extends JFrame {
    private WordyGame wordObj;
    private ImageIcon bg;
    private JLabel wordyLabel, usernameLabel, passwordLabel, registerNote;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    public RegisterGUI(WordyGame wordObj, Point guiLocation){

        super("Wordy Register");

        this.wordObj = wordObj;
        setBackground(Color.gray);
        // Set up the GUI components
        wordyLabel = new JLabel("Register ");
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

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 20));

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.BOLD, 20));

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

        registerButton.setBounds(180, 250, 120, 30);
        registerButton.setForeground(Color.blue);

        backButton.setBounds(150,320,200,30);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setOpaque(false);
        backButton.setForeground(Color.yellow);

        backButton.addActionListener(e -> {
            dispose();
            new ClientLoginGUI(wordObj, getLocation());
        });

        registerButton.setOpaque(false);

        registerButton.addActionListener(e->{
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean isValid;
            isValid = wordObj.register(username, password);
            if (isValid) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
            } else {
                JOptionPane.showMessageDialog(this, "Error! Username taken or empty fields detected.");
            }
        });

        // Add the components to the GUI
        add(wordyLabel);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(registerButton);
        add(backButton);

        // Set the size and location of the GUI
        setSize(500, 450);
        setLocation(guiLocation);

        // Set the GUI to be visible
        setResizable(false);
        setVisible(true);
    }

}
