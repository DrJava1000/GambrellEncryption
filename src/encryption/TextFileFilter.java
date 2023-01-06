package encryption; 

import java.io.File; 
import org.apache.commons.io.FilenameUtils; 

public class TextFileFilter extends javax.swing.filechooser.FileFilter {
 private static String fileExtensionFilter = "txt"; 
	
 public boolean accept(File file){
	 
	 String fileExtension = "";
	 
	 try {
		 fileExtension = FilenameUtils.getExtension(file.getName());
	 }catch(IllegalArgumentException alternateStreamError){
		 return false; 
	 }
  
	 // Check to see if files match filter
	 if(fileExtension.equals(fileExtensionFilter)){
		 return true; // is a file with the extension  
	 }else{
		 return false; // isn't a file with the extension  
  	 }	 
 }
 
 public String getDescription()
 {
  if(fileExtensionFilter.equals("txt")){
	  return "Plain Text Files (.txt)"; 
  }
  if(fileExtensionFilter.equals("enctxt")){
	  return  "Encoded Text Files (.enctxt)"; 
  }else{
	  return "";   
  }
 }
 
 public static void setFileExtensionFilter(String extensionFilter) // sets reverse file extension that is used for selecting files to view 
 {
  fileExtensionFilter = extensionFilter; 
 }
}
