package org.tempo.Util;

/** 
* @Description: Add Candidate without Date option.
* 
*/

public enum AddNoDateAnswersMode {
    Add(true),   //Add:Candidate without date is always added in the final result.

    NotAdd(false); //NotAdd:Candidate without date is not added in the final result when the answer is a list, containing more than one candidate.

	
	private boolean AddnodateMode;

	private AddNoDateAnswersMode(boolean AddnodateMode) { 
    	this.AddnodateMode = AddnodateMode;
        
    }  
	
	public boolean getMode() {  
        return AddnodateMode;  
    } 
}


