package encryption; 

import java.io.*; 
import java.util.*; 
import javax.swing.*; 

public class CaesarEncryption 
{
 public ArrayList<Integer> inputStrs; 
 public BufferedReader fileReader; 
 public PrintWriter fileWriter; 	
 public int asciiShiftCount; // represents a count of how much the ASCII alphabet is shifted during caesar cipher 
 public boolean encrypt; // used for checking which confirmation dialog is to be displayed
 
 public CaesarEncryption(int shiftCount) // sets ASCII alphabetic shift by initializing asciiShiftCount variable 
 {
  asciiShiftCount = shiftCount;  
 }
 
 public void encryptTextFile(File plainText, File cipherText) throws IOException // encrypts plainText file and outputs to cipherText file 
 {
  this.readTextFileContents(plainText); 
  this.writeTextFileContents(cipherText, true);
 }
 
 public void decryptTextFile(File cipherText, File plainText) throws IOException // decrypts cipherText file and outputs to a plainText file 
 {
  this.readTextFileContents(cipherText);	
  this.writeTextFileContents(plainText, false);
 }
 
 public void readTextFileContents(File inputTextFile) throws IOException // reads input file for encryption/decryption (process is same for both)
 {
  fileReader = new BufferedReader(new FileReader(inputTextFile));   
  inputStrs = new ArrayList<Integer>(); 
	  
  boolean continueRead = true; 
		  
  for(int i = 0; continueRead; i++)
  {
   inputStrs.add(fileReader.read());
   if(inputStrs.get(i).intValue() == -1) // checks to see if the end of the file has been reached 
   {
    continueRead = false;  
   }
  }
   fileReader.close();  
 }
 
 public void writeTextFileContents(File outputTextFile, boolean encrypt) throws IOException 
 {
  fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputTextFile)));  
  
  if(encrypt) // if true, file is to be encrypted 
  {
   this.encrypt = true; // encryption confirmation dialog will be visible after encryption is completed 
   
   int currentEncryptValue = 0; 
   for(int i = 0; i < inputStrs.size() - 1; i++)
   {
    currentEncryptValue = inputStrs.get(i).intValue() + asciiShiftCount; // shifts text characters by certain amount 
    while(currentEncryptValue > 127) // checks to see if end of ASCII character set has been reached 
    {
     currentEncryptValue -= 127;  // resets ASCII character set to beginning  
    }
    
    fileWriter.print((char)currentEncryptValue); // prints out value to a file as a character in 7-bit ASCII
   }
  }else // if else statement reached, file is to be decrypted 
  {
   this.encrypt = false; // decryption confirmation dialog will be visible after decryption is completed 
	  
   int currentDecryptValue = 0; 
   for(int i = 0; i < inputStrs.size() - 1; i++)
   {
	currentDecryptValue = inputStrs.get(i).intValue() - asciiShiftCount; // shifts text characters back by specified amount in order to do decrypt
    while(currentDecryptValue < 0) // checks to see if ASCII character set has been set back to the end 
	{
	 currentDecryptValue += 127; // resets ASCII character set to end  
	}
	    
	fileWriter.print((char)currentDecryptValue);  // prints out value to a file as a character in 7-bit ASCII 
   }
  }
  
  fileWriter.close();  	 
 }
 
 public void displayCompletionNotice() // use JOptionPane to display modal dialogs for confirming encryption/decryption activities 
 {
  JavaEncryption.getMainWindow().playDialogPopUpAudio(false); 
  if(encrypt) // if true, encryption completion dialog is shown, but decryption completion dialog is shown otherwise  
   JOptionPane.showMessageDialog(JavaEncryption.getMainWindow(), "The text file has been encrypted.", "Encryption Complete", JOptionPane.INFORMATION_MESSAGE);
  else
   JOptionPane.showMessageDialog(JavaEncryption.getMainWindow(), "The text file has been decrypted.", "Decryption Complete", JOptionPane.INFORMATION_MESSAGE);   
 }
}
