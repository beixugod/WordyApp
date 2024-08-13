package Server_Java;

import Server_Java.WordyApp.WordyGame;
import Server_Java.WordyApp.WordyGameHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.sql.*;

import static Server_Java.WordyServerHelper.WordyServerMethods.*;

public class WordyServer {
    public static void main(String[] args) {

        try{
            // Initialize the ORB
            ORB orb = ORB.init(args, null);

            // Get the root POA and activate the POAManager
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            // Create a servant for the WordyGame object
            WordyImplementation wordyGameImpl = new WordyImplementation();

            // Register the servant with the ORB
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(wordyGameImpl);
            WordyGame wordyGameRef = WordyGameHelper.narrow(ref);

            // Get a reference to the Naming Service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // Create a name for the WordyGame object
            NameComponent[] path = { new NameComponent("WordyGame", "") };

            // Bind the WordyGame object to the Naming Service
            ncRef.rebind(path, wordyGameRef);

            System.out.println("SERVER: Server is ready and waiting...");

            //shutdown hook to run some code when the server closes, for now it only sets all users to offline, calls the
            //onClose() method
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    // code to be executed on shutdown
                    System.out.println("SERVER: Server is shutting down...");
                    onClose();
                }
            });

            orb.run();
        } catch (org.omg.CORBA.SystemException se) {
            System.err.println("CORBA system exception: " + se.getMessage());
        } catch (org.omg.CORBA.UserException ue) {
            System.err.println("CORBA user exception: " + ue.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
    }
    //this is code that runs as the server closes, such as when the server closes, it sets all users to offline
    protected static void onClose() {
        String query = "UPDATE users SET status='OFFLINE'";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wordy", "root", "");
             PreparedStatement statement = con.prepareStatement(query, ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_INSENSITIVE)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }
}
