package org.tempo.Util;

/** 
* @Description: Temporal relation option.
* 
*/
public enum OnlyLatestOneMode {
    Latest(true),  //Before or after signal words are suggesting the relation of Meet/Met_by. which means the system only maintains the candidates nearest to the temporal constraint.
    NotLatest(false);  //Before or after signal words are suggesting the relation of Before/After.
	
	private boolean RelationMode;

	private OnlyLatestOneMode(boolean RelationMode) { 
    	this.RelationMode = RelationMode;
        
    }  
	
	public boolean getMode() {  
        return RelationMode;  
    } 
}
