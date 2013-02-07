package gyoerkaa.android.accelerationplotscrolling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.os.Environment;

public class LogFile {
	
    private File    file;
    private String  fileName;
	
	
	public LogFile(String fileName) {
		this.fileName = fileName;	
	}
	
	/** Sets a new filename
	 * Deletes the old logfile if needed
     * */		
	public void newFile(String fileName, boolean deleteOld) {
		if (deleteOld)
		{
			delete();
		}		
        this.fileName = fileName;
        
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {	              	
        	file = new File(root, fileName);           	    
        }       
	}
	
	/** Deletes log file.
	  * Returns true on success
      * */	
	public boolean delete() {
		File root;    
	    root = Environment.getExternalStorageDirectory();
	    
	    if (root.canWrite()){	
            File LogFile = new File(root, fileName);
            if (LogFile == null){
                // Logfile does not exist
            	// But treat as succes anyway
            	return true; 
            } else {
            	// Success
            	file.delete();
            	return true;
            }          
        } else {
            // Can't write to file system
            return false; 
        } 	     	
	}
	
    /** Exports Values to a log file. Creates a new file if 
      * neccesary
      * Returns true on success
      * */	
	public boolean writeDataPoint(long time, float[] values) {
		try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){	
                if (file == null){                	
            	    file = new File(root, fileName);           	    
                }
                
                FileWriter writer = new FileWriter(file, true);

                writer.write(Long.toString(time) + " $ ");                
                writer.write(Float.toString(values[0]) + " $ ");
                writer.write(Float.toString(values[1]) + " $ ");
                writer.write(Float.toString(values[2]) + "\r\n ");
                writer.close();
                return true;
            } else {
                // Can't write to file system           	
            	return false;
            }
        }  catch (IOException e) {
            return false;       
        }  	
	}
	
    /** Exports values of dataPoint to a log file. Creates a new file if 
      * neccesary
      * Returns true on success
      * */		
	public boolean writeDataPoint(DataPoint dataPoint) {
		try {
            File root = Environment.getExternalStorageDirectory();
            if (root.canWrite()){	
                if (file == null){                	
            	    file = new File(root, fileName);           	    
                }
                
                FileWriter writer = new FileWriter(file, true);

                writer.write(Long.toString(dataPoint.time) + " $ ");                
                writer.write(Float.toString(dataPoint.values[0]) + " $ ");
                writer.write(Float.toString(dataPoint.values[1]) + " $ ");
                writer.write(Float.toString(dataPoint.values[2]) + "\r\n ");
                writer.close();
                return true;               
            } else {
                // Can't write to file system           	
            	return false;
            }
            	
        }  catch (IOException e) {
            return false;     
        }  
	}
		
}
