package org.tempo.SentenceAnalysis;

/** 
* @ClassName: CEventTag 
* @Description: Event Entity Tag
*  
*/
public class CEventTag extends CLabelTag {
	public int EntityID;  
	
	public CEventTag( int startchar, int endchar, int entityid){
		super(EnumTagType.Event, -1,-1, startchar, endchar);

		this.EntityID = entityid;
	}

}
