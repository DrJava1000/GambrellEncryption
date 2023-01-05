package encryption; 

import java.io.File; 

public class TextFileFilter extends javax.swing.filechooser.FileFilter {
 private static String fileExtensionReversed = "txt."; 
	
 public boolean accept(File f)
 {
  String fileName = f.getName(); 
  String extensionTest = ""; 
	  
  int currentIndex = fileName.length() - 1; 
  int iterationCount = 0; 
  
  if(fileExtensionReversed.equals("txt."))
  {
   while(iterationCount < 4) // Loop reads back last 4 characters of file name String and a new String holds the result 
   {
	extensionTest = extensionTest + fileName.charAt(currentIndex); 
	currentIndex--; 
	iterationCount++; 
   }
  }
  else
  {
   while(iterationCount < 8) // Loop reads back last 8 characters of file name String and a new String holds the result 
   {
    extensionTest = extensionTest + fileName.charAt(currentIndex); 
	currentIndex--; 
	iterationCount++; 
   }  
  }
	  
  if(extensionTest.equals(fileExtensionReversed)) // compares the result String to the file extension backward to determine if the file can be shown  
  {
   return true; // is a file with the extension  
  }else
   {
	return false; // isn't a file with the extension  
   }	 
 }
 
 public String getDescription()
 {
  if(fileExtensionReversed.equals("txt.")) // if text file 
  {
   return "Text Files (.txt)"; 
  }
  if(fileExtensionReversed.equals("txt.cne_")) // if encoded text file 
  {
   return  "Encoded Text Files (_enc.txt)"; 
  }else
  {
   return "";   
  }
 }
 
 public static void setFileExtension(String extension) // sets reverse file extension that is used for selecting files to view 
 {
  fileExtensionReversed = extension; 
 }
}
