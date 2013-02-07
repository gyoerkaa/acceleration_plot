package gyoerkaa.android.accelerationplotscrolling;

import gyoerkaa.android.accelerationlogger5.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
  
public class EditSettingsActivity5 extends Activity {
			    	 		
	private Button buttonOK;
	private Button buttonCancel;
	private Button buttonReset;	
	private EditText editTextFilename;
	private EditText editTextColorX;
	private EditText editTextColorY;
	private EditText editTextColorZ;
	private EditText editTextInterval;	
				
	private Settings currentSettings;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
    	 	        	    
	    setContentView(R.layout.settings);	        
        
	    // Get input elements
        buttonReset  = (Button) findViewById(R.id.settings_reset);        
	    buttonCancel = (Button) findViewById(R.id.settings_cancel);         
        buttonOK     = (Button) findViewById(R.id.settings_ok);
        
        editTextColorX   = (EditText) findViewById(R.id.settings_colorX);
        editTextColorY   = (EditText) findViewById(R.id.settings_colorY);
        editTextColorZ   = (EditText) findViewById(R.id.settings_colorZ);        
	    editTextFilename = (EditText) findViewById(R.id.settings_filename);
        editTextInterval = (EditText) findViewById(R.id.settings_interval);              
        
        buttonReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Reset values to standard
        	    editTextColorX.setText(colorToHexString(Settings.DEFAULT_COLOR_X), TextView.BufferType.EDITABLE);
        	    editTextColorY.setText(colorToHexString(Settings.DEFAULT_COLOR_Y), TextView.BufferType.EDITABLE); 
        	    editTextColorZ.setText(colorToHexString(Settings.DEFAULT_COLOR_Z), TextView.BufferType.EDITABLE);
        	    editTextFilename.setText(Settings.DEFAULT_FILENAME, TextView.BufferType.EDITABLE);
        	    editTextInterval.setText(Settings.DEFAULT_INTERVAL.toString(), TextView.BufferType.EDITABLE);
            }
        });
        
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	// Cancel 
        	    setResult(RESULT_CANCELED);
            	finish();           
            }
        });
        
        buttonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
            	// Get form Entries & save them          	
            	readSettings();
            	
            	// Send Settings back to LogAcceleration Activity
            	Bundle message = new Bundle();
            	message.putInt(Settings.KEY_COLOR_X, currentSettings.colors[0]);
            	message.putInt(Settings.KEY_COLOR_Y, currentSettings.colors[1]);
            	message.putInt(Settings.KEY_COLOR_Z, currentSettings.colors[2]);            	
            	message.putString(Settings.KEY_FILENAME, currentSettings.logfile_name);
            	message.putLong(Settings.KEY_INTERVAL, currentSettings.interval);
           		        	           	
            	Intent intent = new Intent();
            	intent.putExtras(message); 
            	setResult(RESULT_OK, intent);           	
            	
        	    //setResult(RESULT_OK); 
        	    finish();           
            }
        });	    
	    
        // Load preferences
        currentSettings = new Settings();          
        currentSettings.loadPreferences(getApplicationContext());
	    
	 	// Put them into the text field
	    // X Axis Color	    
	    editTextColorX.setText(colorToHexString(currentSettings.colors[0]), 
	    		               TextView.BufferType.EDITABLE);
	    
	    // Y Axis Color  	    
	    editTextColorY.setText(colorToHexString(currentSettings.colors[1]), 
	               TextView.BufferType.EDITABLE);
	    
	    // Z Axis Color   
	    editTextColorZ.setText(colorToHexString(currentSettings.colors[2]), 
	               TextView.BufferType.EDITABLE);
	    
	    // Filename     	    
	    editTextFilename.setText(currentSettings.logfile_name, 
	    		                 TextView.BufferType.EDITABLE);
	    
	    // Time Interval
        editTextInterval.setText(String.valueOf(currentSettings.interval), 
        		                 TextView.BufferType.EDITABLE); 	    		
	        		        
    }
  
	
	@Override
    protected void onStop(){      
		super.onStop();
	    setResult(RESULT_CANCELED);
	}

	
	/**
	 * Reads the form entries and saves them
	 * into the preference file
	 */
    protected void readSettings() {	 	      	 	
        // X Axis
        try { 
        	currentSettings.colors[0] = Color.parseColor(editTextColorX.getText().toString());
            	      
        } catch (IllegalArgumentException e) {
    	 	Toast toast;    	 	
     	    toast = Toast.makeText(getApplicationContext(), 
                    "Ungültige Farbe für X Achse", 
                    Toast.LENGTH_SHORT);
            toast.show();
            
            currentSettings.colors[0] = Settings.DEFAULT_COLOR_X;         	
        }       
        // Y Axis
        try { 
        	currentSettings.colors[1] = Color.parseColor(editTextColorY.getText().toString());
            	      
        } catch (IllegalArgumentException e) {
    	 	Toast toast;    	 	
     	    toast = Toast.makeText(getApplicationContext(), 
                    "Ungültige Farbe für Y Achse", 
                    Toast.LENGTH_SHORT);
            toast.show();
            
            currentSettings.colors[1] = Settings.DEFAULT_COLOR_Y;         	
        }       
        // Z Axis
        try { 
        	currentSettings.colors[2] = Color.parseColor(editTextColorZ.getText().toString());
            	      
        } catch (IllegalArgumentException e) {
    	 	Toast toast;    	 	
     	    toast = Toast.makeText(getApplicationContext(), 
                    "Ungültige Farbe für Z Achse", 
                    Toast.LENGTH_SHORT);
            toast.show();
            
            currentSettings.colors[2] = Settings.DEFAULT_COLOR_Z;         	
        }                         
        // Filename
        currentSettings.logfile_name   = editTextFilename.getText().toString();        
        // Interval
        currentSettings.interval       = Long.valueOf(editTextInterval.getText().toString());            
	 	if (currentSettings.interval <= Settings.MIN_INTERVAL) {
            Toast toast;    	 	
 	        toast = Toast.makeText(getApplicationContext(), 
                    "Ungültiges Intervall (min. " + Integer.valueOf(Settings.MIN_INTERVAL) + " ms)", 
                    Toast.LENGTH_SHORT);
            toast.show();
        
            currentSettings.interval = Settings.MIN_INTERVAL;         	
        }       
        
        // Commit changes
        currentSettings.savePreferences(getApplicationContext());      	
    }
 
    
    /**
     * Converts a color int (argb) to its hex
     * repesentation (String)
     * @param color
     * @return
     */
    protected String colorToHexString(int color) {
    	String result = "#";
    	int colorComponent;
    	
    	// Red Component
    	colorComponent = Color.red(color);
    	if (colorComponent <= 15) {
    		// Add leading zeroes
    		result = result + "0";
    	}
        result = result + Integer.toHexString(colorComponent);
        
    	// Green Component
    	colorComponent = Color.green(color);
    	if (colorComponent <= 15) {
    		// Add leading zeroes
    		result = result + "0";
    	}
        result = result + Integer.toHexString(colorComponent);
        
    	// Blue Component
    	colorComponent = Color.blue(color);
    	if (colorComponent <= 15) {
    		// Add leading zeroes
    		result = result + "0";
    	}
        result = result + Integer.toHexString(colorComponent);       
        
    	return result;
    }

}
