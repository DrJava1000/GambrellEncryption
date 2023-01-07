package encryption; 

import java.io.*;
import java.sql.*; 
import java.util.Random; 

// class inherits non-private members and methods from its CaesarEncryption superclass 
public class VigenereEncryption extends CaesarEncryption{ 
	private String url;  
	private Connection connection; // used for obtaining database connection 
	private Statement sqlStatement; // used for execution of SQL database update commands and queries 	
	
	// initialize CaesarEncryption constructor with 7-bit ASCII alphabetic shift count value 
	public VigenereEncryption(int caesarShiftAmount){
		super(caesarShiftAmount);  
	}
	
	public void connectToVigenereCipherDatabase() throws SQLException, ClassNotFoundException{
		url = "jdbc:derby:VigenereConversion;create=true";  
		connection = DriverManager.getConnection(url); // gets a connection to database using derby database driver (added to classpath as jar file) 
		sqlStatement = connection.createStatement(); // creates a Statement object for making calls to methods that can execute SQL commands and queries 
  
		// Statements below were used to create table in VigenereConversion database for Vigenere encryption 
	
		boolean tableExists = true; // used for checking to see if database already exists 
  
		try{
			sqlStatement.executeUpdate("select * from CipherConversion");	
			System.out.println("Database already exists."); 
		}catch (SQLException e) { // SQLException thrown if database didn't exist before execution 
			// if CipherConversion table does not exist  
			if (e.getSQLState().equals("42X05")){
				tableExists = false; 
				e.printStackTrace(); 	
			}
		}finally {
			if(tableExists == false){
				sqlStatement.executeUpdate("create table CipherConversion (PlainCharacter int, CipherCharacter int, shiftAmount int, primary key(CipherCharacter))"); 	
				this.buildNewDatabase(); // fills CipherConversion table in database if the database didn't exist before execution  
			}
		}
	}
 
	// randomly-chosen decimal values, representing all 7-bit ASCII characters, were mapped 
	public void buildNewDatabase() throws SQLException{
		sqlStatement.executeUpdate("drop table CipherConversion"); 
		sqlStatement.executeUpdate("create table CipherConversion (PlainCharacter int, CipherCharacter int, shiftAmount int, primary key(CipherCharacter))"); 
		Random rand = new Random();                         // to different decimal values representing all the different 7-bit ASCII characters 
		int randomCharacter = 0;                            // every plain text character corresponds to a unique cipher text character ->
		int randomShiftAmount = 0;                          // involved catching DerbySQLIntergrityConstraintViolationExceptions and breaking in and out of a loop to 
                                                      // ensure SQL primary key is used correctly 
		// method generates new conversion table each time program is run if uncommented
		// the conversion table itself isn't visible 
		for(int i = 0; i < 128; i++){
			try{
				randomCharacter =  rand.nextInt(128); 
				randomShiftAmount = Math.abs(i - randomCharacter); 
				System.out.println("PlainText: " + i + " - " + "CipherText: " + randomCharacter); 
				sqlStatement.executeUpdate("insert into CipherConversion (PlainCharacter, CipherCharacter, shiftAmount) values(" + "" + i + " , " + randomCharacter + " , " + randomShiftAmount + ")");  
			}catch(Exception constraintViolationOne){
				System.out.println("Violation of Primary Key. Checking for a new value..."); 
				constraintViolationOne.printStackTrace(); 
				while(true){
					try{
						randomCharacter =  rand.nextInt(128); 
						randomShiftAmount = Math.abs(i - randomCharacter);
						System.out.println("PlainText: " + i + " - " + "CipherText: " + randomCharacter); 
						sqlStatement.executeUpdate("insert into CipherConversion (PlainCharacter, CipherCharacter, shiftAmount) values(" + "" + i + " , " + randomCharacter + " , " + randomShiftAmount + ")"); 
						break; 
					}catch(Exception constraintViolationTwo){
						constraintViolationTwo.printStackTrace(); 
						System.out.println("New Violation. Repeating.."); 
					}
				}
				System.out.println("Loop has been broken out of."); 
			}
		} 
	}
 
	@Override 
 	public void writeTextFileContents(File outputTextFile, boolean encrypt) throws IOException{
		fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(outputTextFile)));  
  
		// if true, file is to be encrypted 
		if(encrypt){
			this.encrypt = true; 
   
			int currentEncryptValue = 0; 
			for(int i = 0; i < inputStrs.size() - 1; i++){
				currentEncryptValue = inputStrs.get(i).intValue(); // shifts text characters by certain amount 
    
				try{ // SQL query is used to select CipherCharacter decimal value in table that corresponds or is mapped to the input plain text character's decimal value  
					ResultSet result = sqlStatement.executeQuery("select CipherCharacter from CipherConversion where PlainCharacter = " + currentEncryptValue);
					result.next(); // moves row pointer to first ResultSet row 
					currentEncryptValue = result.getInt("CipherCharacter"); 
				}catch(SQLException ex){
					ex.printStackTrace(); 
				}
    
				fileWriter.print((char)currentEncryptValue); // value obtained from ResultSet object is printed out as a character value 
			}
		}else{
			this.encrypt = false;   
	  
			int currentDecryptValue = 0; 
			for(int i = 0; i < inputStrs.size() - 1; i++){
				currentDecryptValue = inputStrs.get(i).intValue(); 
	
				try{ // SQL query is used to select PlainCharacter decimal value in table that corresponds or is mapped to the input cipher text character's decimal value  
					ResultSet result = sqlStatement.executeQuery("select PlainCharacter from CipherConversion where CipherCharacter = " + currentDecryptValue);
					result.next(); // moves row pointer to first ResultSet row 
					currentDecryptValue = result.getInt("PlainCharacter"); 
				}catch(SQLException ex){
					ex.printStackTrace(); 
				}
	    
				fileWriter.print((char)currentDecryptValue); // value obtained from ResultSet object is printed out as a character value 
			}
		}
  
		fileWriter.close();  	 
	}
}
