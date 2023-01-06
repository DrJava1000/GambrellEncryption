package encryption; 

import java.io.File; 
import org.apache.commons.io.FilenameUtils; 

/**
* TextFileFilter is the file filter for the application's file chooser.
* 
* It is responsible for filtering out files so that only text/encrypted text
* files can be chosen for use in the program. 
*/
public class TextFileFilter extends javax.swing.filechooser.FileFilter {
	private static String fileExtensionFilter = "txt"; 

	/**
	* File filtering method that filters out files
	* that can't be used. 
	*
	* @param  file current file being evaluated
	* @return boolean whether the file is filtered in (true) or filtered out (false)
	*/
	public boolean accept(File file){
		
		String fileExtension = ""; 
		
		// Obtain file extension for current file
		// If the file is for a separate stream,
		// filter it out. 
		try {
			fileExtension = FilenameUtils.getExtension(file.getName());
		}catch(IllegalArgumentException alternateStreamError){
			return false; 
		}
	  
		 // Check to see if files match extension filter
		if(fileExtension.equals(fileExtensionFilter)){
			return true; // matches 
		}else{
			return false; // filter out 
		}	 
	}
 
	/**
	* Returns the description that is shown in the file chooser
	* filter. 
	*
	* @return string a description of the currently-defined filter (including extension)
	*/
	public String getDescription(){
		if(fileExtensionFilter.equals("txt")){
			return "Plain Text Files (.txt)"; 
		}
		if(fileExtensionFilter.equals("enctxt")){
			return  "Encoded Text Files (.enctxt)"; 
		}else{
			return "";   
		}
	}
 
	/**
	* Set the file extension filter that this template should be using.  
	*
	* @param string an extension that the filter should be aiming to filter in
	*/
	public static void setFileExtensionFilter(String extensionFilter){
		fileExtensionFilter = extensionFilter; 
	}
}
