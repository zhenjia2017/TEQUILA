package org.tempo.Util;

/** 
* @Description: Remove date option. 
* 
*/

public enum RemoveDateMode {

	Remove(true),    //remove explicit or ordinal temporal expression from original question
	NotRemove(false); //do not remove explicit or ordinal temporal expression from original question
	
	private boolean RemoveMode;

	private RemoveDateMode(boolean RemoveMode) { 
    	this.RemoveMode = RemoveMode;
        
    }  
	
	public boolean getMode() {  
        return RemoveMode;  
    } 
}
