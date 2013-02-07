package gyoerkaa.android.accelerationplotscrolling;

import android.util.FloatMath;

/**
 * @author gyoerkaa
 * Holds 3 acceleration values & matching timestamp
 * Values can be converted to world coordinates, if
 * orientation values are supplied
 */
public class DataPoint {
	
	public long  time;
	public float values[] = new float[3];	
	
	public DataPoint() {		
	}

	
	public DataPoint(long time, float[] values) {
		this.time = time;
    	for (int i=0; i<3; i++) {
    		this.values[i] = values[i];                   	    
        }		
	}

	
	/** Set values */
	public void set (long time, float[] values) {
		this.time = time;
    	for (int i=0; i<3; i++) {
    		this.values[i] = values[i];                   	    
        }
	}

	
	/** Converts fValue to radians */	
	public float degToRad(float fvalue) {
		return (fvalue * (float)Math.PI / 180) ;
	}

	
	/** Converts values to world coordinates, where
	 * X-axis points east
	 * Y-axis points north
	 * Z-axis points towards sky 
	 *  */	
	public void toWorldCoordinates(float[] orientation) {
		values = rotateY(values, degToRad( orientation[2]));			
		values = rotateX(values, degToRad(-orientation[1]));
		values = rotateZ(values, degToRad( orientation[0]));		                    

	}

	
	/** Rotates fVector by fangle (in radians) around Z-axis */
	private float[] rotateX(float fVector[], float fangle) {
		float fResult[] = new float[3]; // Result  
		float fRot[]    = new float[9]; // Rotation Matrix
		
		// Set up rotation matrix
		//  /    1      0      0      \
		// |     0  cos(a)   -sin(a)   |
		//  \    0  sin(a)    cos(a)  /
		fRot[0] = 1;        
		fRot[1] = 0; 
		fRot[2] = 0;
		fRot[3] = 0;
		fRot[4] = FloatMath.cos(fangle);  
		fRot[5] = -FloatMath.sin(fangle);  
		fRot[6] = 0; 
		fRot[7] = FloatMath.sin(fangle);
		fRot[8] = FloatMath.cos(fangle); 
		
		// Rotate
		int i;
		for (i=0; i<3; i++){
			fResult[i] = 
				fRot[0+i*3]*fVector[0] +
			    fRot[1+i*3]*fVector[1] +
			    fRot[2+i*3]*fVector[2];				
		}
		
		return fResult;
	}

	
	/** Rotates fVector by fangle (in radians) around Y-axis */
	private float[] rotateY(float fvector[], float fangle) {
		float fresult[] = new float[3]; // result  
		float frot[]    = new float[9]; // Rotation Matrix
		
		// Set up rotationmatrix
		//  /  cos(a)      0      sin(a)  \
		// |     0         1        0      |
		//  \  -sin(a)    0)      cos(a)  /		
		frot[0] = FloatMath.cos(fangle); 
		frot[1] = 0; 
		frot[2] = FloatMath.sin(fangle); 
		frot[3] = 0; 
		frot[4] = 1; 
		frot[5] = 0; 
		frot[6] = -FloatMath.sin(fangle);
		frot[7] = 0;
		frot[8] = FloatMath.cos(fangle);
		
		// Rotate
		int i;
		for (i=0; i<3; i++){
			fresult[i] = 
				frot[0+i*3]*fvector[0] +
			    frot[1+i*3]*fvector[1] +
			    frot[2+i*3]*fvector[2];				
		}
		
		return fresult;
	}

	
	/** Rotates fVector by fangle (in radians) around Z-axis */
	private float[] rotateZ(float fVector[], float fangle) {
		float fResult[] = new float[3]; // Result  
		float fRot[]    = new float[9]; // Rotation Matrix
		
		// Set up rotation matrix
		//  /  cos(a)   -sin(a)     0     \
		// |   sin(a)    cos(a)     0      |
		//  \    0         0        1     /			
		fRot[0] = FloatMath.cos(fangle); 
		fRot[1] = -FloatMath.sin(fangle); 
		fRot[2] = 0; 
		fRot[3] = FloatMath.sin(fangle);  
		fRot[4] = FloatMath.cos(fangle);  
		fRot[5] = 0; 
	    fRot[6] = 0;
	    fRot[7] = 0;
		fRot[8] = 1;
		
		// Rotate
		int i;
		for (i=0; i<3; i++){
			fResult[i] = 
				fRot[0+i*3]*fVector[0] +
			    fRot[1+i*3]*fVector[1] +
			    fRot[2+i*3]*fVector[2];				
		}
				
		return fResult;
	}
	
}

