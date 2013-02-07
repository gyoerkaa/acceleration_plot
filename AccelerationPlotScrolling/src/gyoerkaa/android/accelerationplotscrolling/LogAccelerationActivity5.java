package gyoerkaa.android.accelerationplotscrolling;

import gyoerkaa.android.accelerationlogger5.R;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
 
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator; // Used for testing only
    
public class LogAccelerationActivity5 extends Activity implements SensorListener {
    
	private static final int REQCODE_EDIT_SETTINGS = 1;
    
	// Needed to compute the rotation matrix
    private float   orientValues[] = new float[3];
    private float   accelValues[]  = new float[3];
    
    // Time in milliseconds
    private static final long startTime = android.os.SystemClock.elapsedRealtime();
    private long              currentTime;
   
    // Data queue in which all point get enqueued
    private DataQueue dataQueue = new DataQueue();   
    private DataPoint dataPoint;
    
    private float lastValues[] = new float[3];
    
    // Settings
    private boolean  bWorldCoordinates = false;
    private boolean  bLoggingEnabled   = false;
    
    // TODO Uncomment following line, if testing done:
    private SensorManager mSensorManager;
	// TODO Comment following line, if testing done:	
	//private SensorManagerSimulator mSensorManager;		
	
	private GraphView mGraph;
	private LogFile   mLogfile;
    private Settings  mSettings;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		
	    // TODO Uncomment following line, if testing done:    	
	    mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		// TODO Comment following 2 lines, if testing done:
	    //mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
	    //mSensorManager.connectSimulator();
	    
	    // Get Preferences
	    mSettings = new Settings();
	    mSettings.loadPreferences(getApplicationContext());
	    
	    mLogfile  = new LogFile(mSettings.logfile_name );	    
	    
	    mGraph    = new GraphView(this);
	    setContentView(mGraph);
	       
	}

	
    @Override
	protected void onResume() {
	    super.onResume();
	    mSensorManager.registerListener(this, 
	                                    SensorManager.SENSOR_ACCELEROMETER | 
	                                    SensorManager.SENSOR_ORIENTATION,
	                                    SensorManager.SENSOR_DELAY_UI);
	}

    
	@Override
	protected void onStop() {
	    mSensorManager.unregisterListener(this);
	    super.onStop();
	}

	
	public void onSensorChanged(int sensor, float[] values) {
	    synchronized (this) {
	        switch (sensor){
	            case SensorManager.SENSOR_ORIENTATION: {
		            for (int i=0 ; i<3 ; i++) {
		                orientValues[i] = values[i];
		            }
		            break;
	            }   
	            case SensorManager.SENSOR_MAGNETIC_FIELD: {
	                break;
	            }    
	            case SensorManager.SENSOR_ACCELEROMETER: {                	
	                int i=0;
	                for (i=0; i<3; i++) {
	                    accelValues[i] = values[i];                   	    
	                }	                    	
	                	
	                currentTime = android.os.SystemClock.elapsedRealtime();     
	                dataPoint = new DataPoint(currentTime, accelValues);
	                	
	                if (bWorldCoordinates) {
	                	dataPoint.toWorldCoordinates(orientValues);
	                }               		               	                                      		                
		                
		            if (bLoggingEnabled) {
	                    mLogfile.writeDataPoint(currentTime-startTime, accelValues);                         	                
		            }
		                	                	                  
		            // Put the new data point into the queue
		            dataQueue.offer(dataPoint);
	                                        
		            // Remove all old data points from the queue (older than nTimeInterval)
		            dataPoint = dataQueue.poll(currentTime-mSettings.interval);
		                 
	                // Save values of last data point, which has been removed
		            for (i=0; i<3; i++) {
	                    lastValues[i] = dataPoint.values[i];                  	    
	                }
	                    
		            // Draw all points in the queue
		            mGraph.drawDataPoints(dataQueue, lastValues, mSettings.colors);	                    
	                break;
	            }            
	        }                   
	    }
	}

	
	public void onAccuracyChanged(int sensor, int accuracy) {
	}
 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Get new Settings 
        if ((requestCode == REQCODE_EDIT_SETTINGS) && (resultCode == RESULT_OK)) {
        	Bundle extras = intent.getExtras();
        	mSettings.setSettings(extras);
        	
        	mLogfile.newFile(mSettings.logfile_name, false);
        }
    }
    
    @Override 
    public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.optionsmenu, menu);
	
	 	return (boolean) super.onCreateOptionsMenu(menu);
    }

    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {	        
	 	    // Log into file On/Off    
	 	    case R.id.optionsmenu_logging:	     
      	        loggingOnOff();
	     	    return true;
	     	    
	 	    // Delete Logfile 
	 	    case R.id.optionsmenu_delete_file:
	 	    	logfileDelete();
	 	    	return true;
	 	    	
	        // World-/Devicecoordinates 
	 	    case R.id.optionsmenu_normalize:
	 	    	mapToWorldcoordinatesOnOff();
	     	    return true;
	     	    
	        // Settings 
	 	    case R.id.optionsmenu_settings:    	 
	 	        Intent EditSettingsIntent = new Intent(this, EditSettingsActivity5.class);	 	        
	 			startActivityForResult(EditSettingsIntent, REQCODE_EDIT_SETTINGS);
	 			return true;
		    
	 	    // Exit
	 	    case R.id.optionsmenu_exit:    	 
	 	        finish();
	 			return true;
	 			
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
  
    
    /** Turns logging into file on/off */
    private void loggingOnOff() {
	 	CharSequence toastText;
	 	Resources res = getResources();
	 	Toast toast;
	    
	 	// Create new Instance of LogFile, if not already created
	 	if (mLogfile == null) {
 	    	mLogfile = new LogFile(mSettings.logfile_name);
 	    }
	    	
	 	if (bLoggingEnabled){
 		    // Logging enabled => Disable logging
 	    	toastText = res.getString(R.string.message_logging_off);
 	    	
 	        bLoggingEnabled = false;
 	    } else { 
 		    // Logging Disabled => Enable logging
 	    	toastText = res.getString(R.string.message_logging_on) + 
 		                " " + 
 		                mSettings.logfile_name; 
 		    bLoggingEnabled = true;	     	    
 	    }
        
 	    toast = Toast.makeText(getApplicationContext(), 
                toastText,  
                Toast.LENGTH_SHORT);
        toast.show();  	 	
    }
 
    
    /** Turns mapping to worldcoordinates on/off */
    private void mapToWorldcoordinatesOnOff() {
	 	CharSequence toastText;
	 	Resources res = getResources();
	 	Toast toast;	   

    	if (bWorldCoordinates) {
 		    toastText = res.getString(R.string.message_devicecoord);
 	        bWorldCoordinates = false;
 	    } else {
 		    toastText = res.getString(R.string.message_worldcoord);       		
 		    bWorldCoordinates = true;
 	    }
       
 	    toast = Toast.makeText(getApplicationContext(), 
                toastText, 
                Toast.LENGTH_SHORT);
        toast.show();   
    }
  
    
    /** Deletes Logfile */
    private void logfileDelete() {
	 	CharSequence toastText;
	 	Resources res = getResources();
	 	Toast toast;	   
	    
	 	if (mLogfile == null) {
 	    	mLogfile = new LogFile(mSettings.logfile_name);
	 	}
	 	
	    if (mLogfile.delete()) {
	    	toastText = mSettings.logfile_name + 
	    	            " " + 
	    	            res.getString(R.string.message_logfile_delete_success); 
	 	} else {
	        toastText = res.getString(R.string.message_logfile_delete_failure); 
	 	}
	 	
 	    toast = Toast.makeText(getApplicationContext(), 
                toastText, 
                Toast.LENGTH_SHORT);
        toast.show();   
    }   
    
}
