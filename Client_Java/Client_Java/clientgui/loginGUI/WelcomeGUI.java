package Client_Java.clientgui.loginGUI;

import Client_Java.WordyApp.WordyGame;
import Client_Java.WordyApp.WordyGameHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Properties;
import java.util.regex.Pattern;

public class WelcomeGUI extends JFrame {

    private ORB orb;
    private JTextField ipField, portField;
    private String ip;
    private int port;

    public WelcomeGUI(ORB orb, String[] args) {
        super("Connect to Wordy Server");

        this.orb = orb;
        setBackground(Color.gray);
        // Set up the GUI components
        JLabel wordyLabel = new JLabel("W o r d y");
        wordyLabel.setFont(new Font("Arial", Font.BOLD, 40));
        wordyLabel.setForeground(Color.white);

        JLabel ipLabel = new JLabel("Enter IP address");
        ipLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ipLabel.setForeground(Color.white);

        JLabel portLabel = new JLabel("Enter port number");
        portLabel.setFont(new Font("Arial", Font.BOLD, 20));
        portLabel.setForeground(Color.white);

        ipField = new JTextField(30);
        ipField.setFont(new Font("Arial", Font.BOLD, 17));

        portField = new JTextField(30);
        portField.setFont(new Font("Arial", Font.BOLD, 17));

        JButton connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Arial", Font.BOLD, 20));
        JButton localhostButton = new JButton("LAN Connect");
        localhostButton.setFont(new Font("Arial", Font.BOLD, 20));

        ImageIcon bg = new ImageIcon("CORBA/src/res/login.jpg");
        setContentPane(new JLabel(bg));

        // Set the layout of the GUI
        setLayout(null);

        // Set the bounds of the GUI components
        ipLabel.setBounds(0, 130, 500, 20);
        ipField.setBounds(150, 170, 200, 30);
        portLabel.setBounds(0, 215, 500, 20);
        portField.setBounds(150, 250, 200, 30);
        wordyLabel.setBounds(0, 50, 500, 50);
        connectButton.setBounds(190, 290, 120, 30);
        localhostButton.setBounds(170, 330, 170, 30);
        wordyLabel.setHorizontalAlignment(JLabel.CENTER);
        ipLabel.setHorizontalAlignment(JLabel.CENTER);
        portLabel.setHorizontalAlignment(JLabel.CENTER);

        connectButton.setForeground(Color.blue);
        connectButton.setBackground(Color.yellow);
        localhostButton.setForeground(Color.blue);
        localhostButton.setBackground(Color.yellow);

        ipField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(verifyFields()) {
                        connect(args);
                    }
                }
            }
        });

        portField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(verifyFields()) {
                        connect(args);
                    }
                }
            }
        });

        connectButton.addActionListener(e -> {
            if(verifyFields()) {
                connect(args);
            }
        });

        localhostButton.addActionListener(e -> {
            //manually type it out lol
            ip = "localhost";
            String portString = portField.getText();
            if (portString.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Connecting locally requires a port number, please" +
                        " enter a port number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            connect(args);
        });

        // Add the components to the GUI
        add(wordyLabel);
        add(ipLabel);
        add(ipField);
        add(portLabel);
        add(portField);
        add(connectButton);
        add(localhostButton);

        // Set the size and location of the GUI
        setSize(500, 450);
        setLocationRelativeTo(null);

        // Set the GUI to be visible
        setResizable(false);
        setVisible(true);
    }
    public void connect(String[] args) {
        WordyGame wordObj = null;
        try {

            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitRef", "NameService=corbaloc:iiop:" + ip + ":" + port + "/NameService");

            ORB orb = ORB.init(args, props);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt namingContextExt = NamingContextExtHelper.narrow(objRef);

            NameComponent[] path = { new NameComponent("WordyGame", "") };
            wordObj= WordyGameHelper.narrow(namingContextExt.resolve(path));

        } catch (Exception ex) {
            System.err.println("ERROR: " + ex);
            ex.printStackTrace(System.out);
            JOptionPane.showMessageDialog(this, "Error connecting to server", "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (wordObj != null) {
            try {
                dispose();
                new ClientLoginGUI(wordObj, getLocation());
            } catch (Exception ex) {
                System.err.println("ERROR: " + ex);
                ex.printStackTrace(System.out);
                JOptionPane.showMessageDialog(this, "Error connecting to server", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public boolean verifyFields() {
        //only allow "localhost" or a valid ip address format
        // \d is a digit
        Pattern ipPattern = Pattern.compile("^localhost$|^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
        //check ip field
        ip = ipField.getText();
        String portString = portField.getText();
        if (!ipPattern.matcher(ip).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid IP address or 'localhost'.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an IP address or connect to localhost", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (portString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a port number", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid port number", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //all fields are ok so return true
        return true;
    }
}
