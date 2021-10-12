package org.tempo.SentenceAnalysis;

import java.util.ArrayList;
import java.util.List;

/** 
* @ClassName: CProcessFixInvalidTags 
* @Description: the process node is to remove all duplicate or invalid tags
*  
*/
public class CProcessFixInvalidTags extends CNodeProcessorBase<CSentenceAnalysisReport>{


	public CProcessFixInvalidTags(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	}
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!= null && context.Document!=null);
	}

	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		
		FixNEREntityTag(context.NEREntities);
		
		//remove date tag from Events;
		this.RemoveTagIfInSourceTags(context.Events, context.Dates);
		
		//remove date tag which in event Tags
		this.RemoveTagIfInSourceTags(context.Events, context.TimeReports);
		
		//remove date tag which in When Tags
		this.RemoveTagIfInSourceTags(context.WhenTags,  context.Dates);
		
		//remove date tag which in Tempral Tags
		this.RemoveTagIfInSourceTags(context.TimeReports,context.Dates);


		//remove ordinal tag which in Tempral Tags
		this.RemoveTagIfInSourceTags(context.Events, context.Ordinals);	
		
		//remove ordinal tag which in Tempral Tags
		//this.RemoveTagIfInSourceTags(context.TimeReports, context.Dates);			

	}	
	
	/** 
	* @Title: FixNEREntityTag 
	* @Description: only remove date, time, percent, money tag.
	* @param tags   
	*/
	void FixNEREntityTag(List<CNEREntityTag> tags){
		List<Integer> indexList = new ArrayList<Integer>();
		for(int i = tags.size()-1; i>=0; i--){
			CNEREntityTag tag = tags.get(i);
			if(tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Date || 
					tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Time ||
					tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Percent || 
					tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Money ){
				
				indexList.add(i);
				
			}
		}
		
		for(Integer i: indexList){
			tags.remove(i);
		}
	}
	
	
	/** 
	* @Title: RemoveTagIfInSourceTags 
	* @Description: remove tags in a set.
	* @param compareTags
	* @param removedTagSet   
	*/
	void RemoveTagIfInSourceTags(List<? extends ILabelTag> compareTags,   List<? extends ILabelTag> removedTagSet){
		if(compareTags == null || compareTags.size() < 1 || removedTagSet == null || removedTagSet.size() < 1)
			return;
		
		for(ILabelTag tag: compareTags){
			//get tag index in the set.
			int dupIndex = getTagIndexInSet(tag,removedTagSet);
			if(dupIndex >= 0)
				removedTagSet.remove(dupIndex); //remove the tag 
		}	
	}
	
	
	/** 
	* @Title: getTagIndexInSet 
	* @Description: 
	* @param src
	* @param dst
	* @return   
	*/
	int getTagIndexInSet(ILabelTag src, List<? extends ILabelTag> dst){
		//locate tag index by compare char position
		for(int i =0; i < dst.size(); i++){
			ILabelTag dateTag = dst.get(i);
			if(src.getBeginPos()<= dateTag.getBeginPos() && src.getEndPos()>= dateTag.getEndPos())
				return i;
		}
		return -1;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
