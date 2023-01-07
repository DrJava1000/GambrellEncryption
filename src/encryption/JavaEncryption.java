package encryption; 

import java.io.*;
import javax.swing.*;
import java.awt.event.*; 
import java.awt.*; 
import javax.sound.sampled.*; 
import java.sql.*; 

public class JavaEncryption extends JFrame{
  
  private static JavaEncryption main;
  private static CaesarEncryption ce; 
  private static VigenereEncryption ve; 
  private static int encType; // used to specify whether Caesar or Vigenere encryption is to take place -> stored value of 1 means Caesar cipher while stored value of 2 means Vigenere cipher 
  private static int caesarShiftValue = 10; // used to hold shift value for Caesar cipher (a value of 10 is the default if text box is ignored)
  private static boolean rebuild; // used to hold a value that represents whether database for Vigenere cipher should be randomized 
  private static boolean check = true; // used to check to see whether it will be okay to build the database again (database should not be randomized between encryption and decryption)
  
  public JavaEncryption(String windowMessage){
	  // Call superclass constructor to set application frame message 
	  super(windowMessage); 
  }
	
  public static void main(String [] args){
	  // main window is built by event dispatching thread (implemented for good technique)  
	  SwingUtilities.invokeLater(new Runnable(){
		  public void run(){
			  try{
				  JavaEncryption.buildMainWindow(); 
			  }catch(IOException ioErr){
				  ioErr.printStackTrace(); 	 
			  }
		  }
	  });   
  }
  
  public static void buildMainWindow() throws IOException{ 
	  // create, decorate, and size main window   
	  
	  JFrame.setDefaultLookAndFeelDecorated(true); 
	  main = new JavaEncryption("GambrellEncryption v1.0");  
	  main.setSize(500,500); // sets size of main program window using width and height measurements in pixels  
	  main.getRootPane().setBorder(BorderFactory.createLineBorder(new Color(204,204,0), 5)); // creates a solid gold border around root pane of main window 
   
	  // File Chooser set up 
   
	  JFileChooser fileChooser = new JFileChooser(); 
	  fileChooser.setAcceptAllFileFilterUsed(false); // disables chooser's default file filter (all files are shown by default) 
	  fileChooser.setFileFilter(new TextFileFilter()); // sets file filter to text file filter from TextFileFilter.java 
   
	  // Colors for use in component coloring 
   
	  Color lightBlueColor = new Color(153,204,255); 
	  Color lightGreenColor = new Color(0,204,102); 
	  Color blueColor = new Color(0,51,153); 
   
	  // Encryption and Decryption Buttons and Listeners 
   
	  JButton encryptButton = new JButton("Choose a file to encrypt..."); // following commands set encrypt button's label, color, and size 
	  encryptButton.setBackground(lightBlueColor); 
	  encryptButton.setPreferredSize(new Dimension(250,125)); 
	  encryptButton.setEnabled(false); // disables encrypt button 
   
	  // utilize lambda expression to create anonymous class that implements ActionListener
	  // for encrypt button
	  encryptButton.addActionListener((ActionEvent e) -> {  
		  try{                                       
			  main.displayFileChooser(fileChooser, true); // displays file chooser and provides a boolean value of true that tells the program encryption is going to occur 
		  }catch(IOException io){
			  System.out.println("IOException occurred."); 
			  io.printStackTrace(); 
		  }
	  });
   
	  JButton decryptButton = new JButton("Choose a file to decrypt..."); // following commands set decrypt button's label, color, and size 
	  decryptButton.setBackground(lightBlueColor); 
	  decryptButton.setPreferredSize(new Dimension(250,125)); 
	  decryptButton.setEnabled(false); // disables decrypt button 
	  
	  // utilize lambda expression to create anonymous class that implements ActionListener
	  // for decrypt button 
	  decryptButton.addActionListener((ActionEvent e) -> { 
		  try{ 
			  main.displayFileChooser(fileChooser, false); // displays file chooser and provides a boolean value of false that tells the program decryption is going to occur 
		  }catch(IOException io){
			  System.out.println("IOException occurred."); 
			  io.printStackTrace();
		  }
	  });  
   
	  // Caesar cipher shift value input components 
   
	  JPanel topWindowContainer = new JPanel(); 
	  topWindowContainer.setLayout(new BoxLayout(topWindowContainer, BoxLayout.Y_AXIS)); // sets top GUI components container to use BoxLayout 
	  
	  JPanel textComponentsContainer = new JPanel(); // holds text field and button for caesar cipher shift value input 
	  textComponentsContainer.setBackground(lightGreenColor); //sets light green color for text input container 
   
	  JLabel shiftValueInputPrompt = new JLabel("Enter an ASCII shift value: "); // creates label for asking user input for Caesar cipher shift value 
	  JTextField caesarCountTextField = new JTextField(20); // creates JTextField component for getting shift value for caesar cipher 
   
	  JButton shiftCountEntry = new JButton("Confirm"); // creates button for confirming shift value input 
	  shiftCountEntry.setBackground(lightBlueColor); 
	  shiftCountEntry.addActionListener((ActionEvent e) -> {
		  try{
			  caesarShiftValue = Integer.parseInt(caesarCountTextField.getText()); // parses input text as an integer shift value 
			  main.playDialogPopUpAudio(false); // play audio file for no errors  
			  JOptionPane.showMessageDialog(main, "The Caesar cipher shift value is " + caesarShiftValue + ".", "Shift Value Change Confirmation", JOptionPane.INFORMATION_MESSAGE); // show dialog that reminds user of the shift value they gave as input 
		  }catch(NumberFormatException inputEx){
			  main.playDialogPopUpAudio(true); // play audio file for use when a NumberFormatException occurs 
			  JOptionPane.showMessageDialog(main, "The shift value has not been given, is too large or too small, or the value given is not an integer.", "NumberFormatException", JOptionPane.ERROR_MESSAGE); // show dialog that highlights a possible error input 
		  }
		  finally{
			  caesarCountTextField.setText(""); // clears text box of any text if error occurs 
		  }
	  }); // register button to retrieve text, store it, display a dialog, and reset the text field 
   
	  JRadioButton caesar = new JRadioButton("Caesar Cipher", false); // Caesar cipher radio button -> initially deselected 
	  caesar.setBackground(lightGreenColor); // sets Caesar cipher button color 
	  caesar.setForeground(blueColor); // sets button's text color 
   
	  JRadioButton vigenere = new JRadioButton("Vigenere Cipher", false); // Vigenere cipher radio button -> initially deselected 
	  vigenere.setBackground(lightGreenColor); // sets Vigenere cipher button color 
	  vigenere.setForeground(blueColor); // set button's text color 
   
	  JPanel databaseOptionsContainer = new JPanel(); // creates container for database options radio buttons 
	  databaseOptionsContainer.setBackground(lightGreenColor); 
   
	  JRadioButton noDatabaseRebuildButton = new JRadioButton("Current Database"); // creates current database radio button 
	  noDatabaseRebuildButton.addActionListener((ActionEvent e) -> {rebuild = false;}); // registers button to prevent program from randomizing database 
	  noDatabaseRebuildButton.setBackground(lightGreenColor); 
   
	  JRadioButton databaseRebuildButton = new JRadioButton("New Database"); // creates new randomized database radio button 
	  databaseRebuildButton.addActionListener((ActionEvent e) -> {rebuild = true;}); // registers button to randomize database when needed 
	  databaseRebuildButton.setBackground(lightGreenColor); 
   
	  ButtonGroup databaseConstructionGroup = new ButtonGroup(); // add database options radio buttons to logical grouping (only one can be selected)
	  databaseConstructionGroup.add(noDatabaseRebuildButton);
	  databaseConstructionGroup.add(databaseRebuildButton);
   
	  databaseOptionsContainer.add(noDatabaseRebuildButton); // adds database options radio buttons to database options container 
	  databaseOptionsContainer.add(databaseRebuildButton); 
   
	  // utilize lambda expression to create anonymous class that implements ActionListener
	  caesar.addActionListener((ActionEvent e) -> {
		  textComponentsContainer.add(shiftValueInputPrompt); 
		  textComponentsContainer.add(caesarCountTextField); // adds text field and corresponding button to Caesar shift value input container 
		  textComponentsContainer.add(shiftCountEntry); 
		  topWindowContainer.remove(databaseOptionsContainer);
		  topWindowContainer.add(textComponentsContainer); // add Caesar shift value input components container to top GUI components container
		  main.pack(); 
		  main.validate(); // validates all containers and components in main JFrame (reflects new changes) 
		  encryptButton.setEnabled(true); // enables encrypt/decrypt buttons 
		  decryptButton.setEnabled(true); 
		  encType = 1; // prepares program to use Caesar cipher 
	  }); 
    
	  // utilize lambda expression to create anonymous class that implements ActionListener for registering Vigenere cipher button 
	  vigenere.addActionListener((ActionEvent e) -> {
		  topWindowContainer.remove(textComponentsContainer); // remove container for components for Caesar shift value input 
		  topWindowContainer.add(databaseOptionsContainer); // adds container that holds database options components 
		  main.pack(); 
		  main.validate(); // validates all containers and components in main JFrame (reflects new changes) 
		  encryptButton.setEnabled(true); // enables encrypt/decrypt buttons 
		  decryptButton.setEnabled(true);
		  encType = 2; // prepares program to use Vigenere cipher 
	  });   
  
	  // vigenere.setEnabled(false); // change when jar file can be successfully recreated 
   
	  ButtonGroup cipherButtonsConstructionGroup = new ButtonGroup(); // object represents logical grouping for JRadioButtons -> only one button can be selected at a time  
	  cipherButtonsConstructionGroup.add(caesar); // add cipher buttons to button group -> only one of the cipher buttons can now be selected at a time 
	  cipherButtonsConstructionGroup.add(vigenere);
   
	  JPanel cipherSelectContainer = new JPanel(); // generic lightweight container
	  cipherSelectContainer.add(caesar); // adds cipher buttons to generic container 
	  cipherSelectContainer.add(vigenere);
	  cipherSelectContainer.setBackground(lightGreenColor); 
   
	  topWindowContainer.add(cipherSelectContainer); // adds cipher selection buttons to top GUI components container 
   
	  JPanel buttonContainer = new JPanel(); // generic lightweight container 
	  buttonContainer.add(encryptButton); // adds encrypt/decrypt buttons to container 
	  buttonContainer.add(decryptButton); 
	  buttonContainer.setBackground(lightGreenColor); 
   
	  ve = new VigenereEncryption(10);
	  try{
		  ve.connectToVigenereCipherDatabase(); // makes a connection to database used for Vigenere encryption  
	  }catch(SQLException|ClassNotFoundException ex){
		  ex.printStackTrace(); 
	  }
   
	  // Add all components to JFrame, configure the main window, and make it visible 
   
	  main.add(topWindowContainer, BorderLayout.NORTH); // adds top GUI components to the top of the main window 
	  main.add(buttonContainer); // adds encrypt/decrypt buttons in the center of the main window 
	  main.pack(); // resizes window to fit components 
	  main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // main window closes when exited out 
	  main.setResizable(false); // window can't be resized by the user 
	  main.setVisible(true); // main window becomes visible to user 
  }
  
  // method retrieves file from a JFileChooser
  // if possible and makes calls to encrypt or 
  // decrypt the file as specified 
  public void displayFileChooser(JFileChooser fileChooser, boolean encrypt) throws IOException{
	  File selection;
	  ce = new CaesarEncryption(caesarShiftValue); 
   
	  if(encrypt == false){
		  TextFileFilter.setFileExtensionFilter("enctxt"); // sets file filter to check for encoded text files if there is to be decryption 
	  }else{
		  TextFileFilter.setFileExtensionFilter("txt"); // sets file filter to check for normal text files for encryption 
	  }
   
	  int selectionResult = fileChooser.showOpenDialog(this); // opens dialog for choosing a file in file explorer 
   
	  if(selectionResult == JFileChooser.APPROVE_OPTION){
		  selection = fileChooser.getSelectedFile();  // input file (obtained if the user selected a file and tried to open it) 
	  }else{
		  return; // if chooser was exited without selecting a file, the user can attempt to select a file again; execution doesn't continue beyond this point if command reached 
	  }
   
	  if(encType == 2){
		  // if user wants to rebuild, the database values are randomized
		  if(rebuild && check){
			  try{
				  int dialogOption = JOptionPane.showConfirmDialog(main, "The database will be randomized for encryption at this time. Do you want to continue?", "Database Rebuild Confirmation", JOptionPane.OK_CANCEL_OPTION); 
				  if(dialogOption == JOptionPane.OK_OPTION){
					  ve.buildNewDatabase(); // randomize database values 
					  JOptionPane.showMessageDialog(main, "The database has been fully rebuilt.", "Database Rebuild Complete", JOptionPane.INFORMATION_MESSAGE);
					  check = false; // keeps randomization from occuring during decryption 
				  }else{
					  return;    
				  }
			  }catch(SQLException buildErr){
				  buildErr.printStackTrace();   
			  }
		  }
	  }
   
	  // if encrypt parameter holds boolean value of true, file is encrypted and a new file with .enctxt ending is created 
	  if(encrypt == true){
		  File cipherFile = main.prepareFileWrite(selection, ".txt", ".enctxt");

		  if(encType == 1){
			  ce.encryptTextFile(selection, cipherFile); // encrypts file with selected input and output file using Caesar cipher  
			  ce.displayCompletionNotice(); 	
		  }else{
			  ve.encryptTextFile(selection, cipherFile); // encrypts file with selected input and output file using Vigenere cipher 
			  ve.displayCompletionNotice(); 	
		  }
	  }else{ // if encrypt parameter holds boolean value of false, file is decrypted and a new file with _dec.txt ending is created 
	
		  File plainFile = main.prepareFileWrite(selection, ".enctxt", "_dec.txt");
    
		  if(encType == 1){
			  ce.decryptTextFile(selection, plainFile); // decrypts file with selected input and output file using Caesar cipher  
			  ce.displayCompletionNotice(); 	
		  }else{
			  ve.decryptTextFile(selection, plainFile); // decrypts file with selected input and output file using Vigenere cipher  
			  ve.displayCompletionNotice(); 	
		  }
	  }
  }
  
  // creates output file for encryption/decryption processes 
  public File prepareFileWrite(File selectedFile, String oldFileExtension ,String newFileExtension) throws IOException{
	  String selFileName = selectedFile.getName().replace(oldFileExtension, newFileExtension); 
	  File cipherTextFile = new File(selectedFile.getParent() + "\\" + selFileName); // creates File object for output file to be placed in same directory as input file 
	  cipherTextFile.createNewFile(); // output file is created  
	  return cipherTextFile; 
  }
  
  // gets main window object 
  public static JavaEncryption getMainWindow(){
	  return main;  
  }
  
  // uses Sound API to play Windows 7 sounds for pop up dialogs 
  public void playDialogPopUpAudio(boolean errorMessage){
	  try{
		  File audioFile; 
	
		  if(errorMessage){
			  audioFile = new File("Windows Error.wav"); // audio file to play for error dialogs 
		  }else{
			  audioFile = new File("Windows Exclamation.wav"); // audio file to play for information dialogs 
		  }
	
		  Clip audioClip = AudioSystem.getClip();  
		  AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile); // obtains input stream from a .wav file 
		  audioClip.open(stream); // input stream is opened with clip 
		  audioClip.start(); // audio playback begins 
		  
	  }catch(LineUnavailableException|IOException|UnsupportedAudioFileException ex){
		  ex.printStackTrace();  
	  }
  }
}
 