package Client_Java.WordyApp;


/**
* WordyApp/WordyGameOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from WordyApp.idl
* Sunday, May 28, 2023 11:06:31 PM SGT
*/

public interface WordyGameOperations 
{
  void login (String username, String password) throws InvalidCredentials, UserActive;
  void logout (String username);
  boolean register (String username, String password);
  String[] getFiveLongestWords ();
  String[] getTopFivePlayers ();
  void startNewWaitingRoom ();
  boolean joinWaitingRoom (String username);
  void leaveGameRoom (String username, String roomName);
  String[] getWaitingRoomInformation ();
  boolean submitWord (String inputString, String username, int roundNumber, String roomName);
  boolean endRound (String username, String roomName);
  String[] getGameRoomInformation (String roomName, String username);
} // interface WordyGameOperations
