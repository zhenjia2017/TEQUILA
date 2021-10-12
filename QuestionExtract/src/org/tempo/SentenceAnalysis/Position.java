package org.tempo.SentenceAnalysis;

import java.util.List;

/** 
* @ClassName: Position 
* @Description: 
*  
*/
public class Position{
	public int x;
	public int y;
	
	public Position(){
		x = -1; 
		y=-1;
	}
	public Position(int x1, int y1){
		this.x= x1;
		this.y = y1;
	}
	
	/** 
	* @Title: GetRangIndex 
	* @Description: get a position(start and end index) by the words' positions
	* @param wordPositions
	* @param startChar
	* @param endChar
	* @return   
	*/
	public static Position  GetRangIndex(List<Position> wordPositions, int startChar, int endChar){
		int startIndex=-1, endIndex=-1;
		for(int i = 0; i < wordPositions.size(); i++){
			Position curWord = wordPositions.get(i);
			if(curWord.x <= startChar && curWord.y > startChar ){
				startIndex = i;
			}
			
			if( curWord.x < endChar && curWord.y >=endChar){
				endIndex=i;
			}			
		}
		
		return new Position(startIndex, endIndex);
	}		
	
	
}
