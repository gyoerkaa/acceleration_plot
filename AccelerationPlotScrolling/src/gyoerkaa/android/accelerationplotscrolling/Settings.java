package gyoerkaa.android.accelerationplotscrolling;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

public class Settings {
	 
	public static final String  DEFAULT_FILENAME = "accelLog.txt";	
	public static final Long    DEFAULT_INTERVAL = 3000l;	
	public static final int     DEFAULT_COLOR_X  = Color.RED;
	public static final int     DEFAULT_COLOR_Y  = Color.GREEN;
	public static final int     DEFAULT_COLOR_Z  = Color.BLUE;
	
	public static final int     INTERPOLATION_NONE   = 0;
	public static final int     INTERPOLATION_LINEAR = 0;
	
	public static final int     MIN_INTERVAL = 1000;

	
	/** Name of the logfile */
	public String  logfile_name;
    /** Time interval (in milliseconds) to be displayed */	   
    public long    interval;
    /** Colors of the three axes */	    
    public int     colors[];
    /** Interpolation 
     * 
     * */
    public int     interpolation;

    
    public static final String KEY_COLOR_X  = "color_x";
    public static final String KEY_COLOR_Y  = "color_y";
    public static final String KEY_COLOR_Z  = "color_z";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_INTERVAL = "interval";	
    public static final String KEY_INTERPOL = "interpol";
    
    public static final String PREFS_NAME = "accellog5_pref";   
    
	public Settings() {
		logfile_name   = DEFAULT_FILENAME;
		colors         = new int[3];
		colors[0]      = DEFAULT_COLOR_X;
		colors[1]      = DEFAULT_COLOR_Y;
		colors[2]      = DEFAULT_COLOR_Z;
		interval       = DEFAULT_INTERVAL;				
		interpolation  = INTERPOLATION_NONE;
	}

	/**
	 * Set settings to values specified in the  bundle
	 */		
	public void setSettings(Bundle bundle) {
		this.colors[0]      = bundle.getInt(Settings.KEY_COLOR_X);  
		this.colors[1]      = bundle.getInt(Settings.KEY_COLOR_Y); 
		this.colors[2]      = bundle.getInt(Settings.KEY_COLOR_Z);
		this.logfile_name   = bundle.getString(Settings.KEY_FILENAME);
		this.interval       = bundle.getLong(Settings.KEY_INTERVAL);   		
	}

	/**
	 * Loads settings from the preference file
	 */	
	public void loadPreferences(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

	 	// Load preferences
	    // X Axis Color, use standard Color if not specified
        this.colors[0]      = prefs.getInt(KEY_COLOR_X, DEFAULT_COLOR_X);
	    // Y Axis Color, use standard Color if not specified   	    
        this.colors[1]      = prefs.getInt(KEY_COLOR_Y, DEFAULT_COLOR_Y);
	    // Z Axis Color, use standard Color if not specified    
        this.colors[2]      = prefs.getInt(KEY_COLOR_Z, DEFAULT_COLOR_Z);   	
	    // Filename, use standard filename if not specified       
        this.logfile_name   = prefs.getString(KEY_FILENAME, DEFAULT_FILENAME);
        if (this.logfile_name == "")
        	this.logfile_name = "DEFAULT_FILENAME";
	    // Time Interval, use standard interval if not a valid	
        this.interval       = prefs.getLong(KEY_INTERVAL, DEFAULT_INTERVAL);    		
	}
	
	/**
	 * Save settings to preference file
	 */		
	public void savePreferences(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
                        	
        // X Axis Color
        editor.putInt(Settings.KEY_COLOR_X, this.colors[0]);
        // Y Axis Color
        editor.putInt(Settings.KEY_COLOR_Y, this.colors[1]);
         // Z Axis Color
        editor.putInt(Settings.KEY_COLOR_Z, this.colors[2]);
	    // Filename, use standard filename if not specified         
        if (this.logfile_name == "")
        	this.logfile_name = "DEFAULT_FILENAME";       
        editor.putString(Settings.KEY_FILENAME, this.logfile_name);        
        // Time Interval
        editor.putLong(Settings.KEY_INTERVAL, this.interval);             
        
        // Commit changes
        editor.commit();       
	}
}
