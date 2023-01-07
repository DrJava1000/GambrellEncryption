package encryption; 

import java.io.*; 
import java.util.*; 
import javax.swing.*; 

public class CaesarEncryption{
	public ArrayList<Integer> inputStrs; 
	public BufferedReader fileReader; 
	public PrintWriter fileWriter; 	
	public int asciiShiftCount; // represents a count of how much the ASCII alphabet is shifted during caesar cipher 
	public boolean encrypt; // used for checking which confirmation dialog is to be displayed
 
	// sets ASCII alphabetic shift by initializing asciiShiftCount variable 
	public CaesarEncryption(int shiftCount){
		asciiShiftCount = shiftCount;  
	}
 
	// encrypts plainText file and outputs to cipherText file 
	public void encryptTextFile(File plainText, File cipherText) throws IOException{
		this.readTextFileContents(plainText); 
		this.writeTextFileContents(cipherText, true);
	}
 
	// decrypts cipherText file and outputs to a plainText file
	public void decryptTextFile(File cipherText, File plainText) throws IOException{
		this.readTextFileContents(cipherText);	
		this.writeTextFileContents(plainText, false);
	}
 
	// reads input file for encryption/decryption (process is same for both)
	public void readTextFileContents(File inputTextFile) throws IOException{
		fileReader = new BufferedReader(new FileReader(inputTextFile));   
		inputStrs = new ArrayList<Integer>(); 
	  
		boolean continueRead = true; 
		  
		for(int i = 0; continueRead; i++){
			inputStrs.add(fileReader.read());
			// checks to see if the end of the file has been reached 
			if(inputStrs.get(i).intValue() == -1){
				continueRead = false;  
			}
		}
		fileReader.close();  
	}
 
	public void writeTextFileContents(File outputTextFile, boolean encrypt) throws IOException{
		fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputTextFile)));  
  
		// if true, file is to be encrypted 
		if(encrypt){
			this.encrypt = true; // encryption confirmation dialog will be visible after encryption is completed 
   
			int currentEncryptValue = 0; 
			for(int i = 0; i < inputStrs.size() - 1; i++){
				currentEncryptValue = inputStrs.get(i).intValue() + asciiShiftCount; // shifts text characters by certain amount 
				
				// checks to see if end of ASCII character set has been reached 
				while(currentEncryptValue > 127){
					currentEncryptValue -= 127;  // resets ASCII character set to beginning  
				}
    
				fileWriter.print((char)currentEncryptValue); // prints out value to a file as a character in 7-bit ASCII
			}
		// if else statement reached, file is to be decrypted 
		}else{
			this.encrypt = false; // decryption confirmation dialog will be visible after decryption is completed 
			
			int currentDecryptValue = 0; 
			for(int i = 0; i < inputStrs.size() - 1; i++){

				currentDecryptValue = inputStrs.get(i).intValue() - asciiShiftCount; // shifts text characters back by specified amount in order to do decrypt
				
				// checks to see if ASCII character set has been set back to the end 
				while(currentDecryptValue < 0){
					currentDecryptValue += 127; // resets ASCII character set to end  
				}
	    
				fileWriter.print((char)currentDecryptValue);  // prints out value to a file as a character in 7-bit ASCII 
			}
		}
  
		fileWriter.close();  	 
	}
 
	// use JOptionPane to display modal dialogs for confirming encryption/decryption activities
	public void displayCompletionNotice(){
		JavaEncryption.getMainWindow().playDialogPopUpAudio(false); 
		if(encrypt) // if true, encryption completion dialog is shown, but decryption completion dialog is shown otherwise  
			JOptionPane.showMessageDialog(JavaEncryption.getMainWindow(), "The text file has been encrypted.", "Encryption Complete", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(JavaEncryption.getMainWindow(), "The text file has been decrypted.", "Decryption Complete", JOptionPane.INFORMATION_MESSAGE);   
	}
}
