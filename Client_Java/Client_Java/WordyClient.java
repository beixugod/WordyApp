package Client_Java;

import Client_Java.clientgui.loginGUI.WelcomeGUI;
import org.omg.CORBA.*;

public class WordyClient {
    public static void main(String[] args) {
        try {
            // Initializes the ORB
            ORB orb = ORB.init(args, null);

            // Show the WelcomeGui and passes orb
            new WelcomeGUI(orb, args);

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
}

