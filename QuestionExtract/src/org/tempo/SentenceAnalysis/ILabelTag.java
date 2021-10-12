package org.tempo.SentenceAnalysis;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;

public interface ILabelTag {
	public int getBeginPos();
	public int setBeginPos(int pos);

	public int getEndPos();
	public int setEndPos(int pos);	

	public int getStart();
	public int setStart(int pos);	
	
	public int getEnd();
	public int setEnd(int pos);	
	
	public EnumTagType getTagType();
	
	public static void SetTagWordIndex(List<Position> positions, ILabelTag tag){
		Position pos = Position.GetRangIndex(positions, tag.getBeginPos(),  tag.getEndPos());
		if(pos.x == pos.y)
			pos.y +=1;
		tag.setStart(pos.x);
		tag.setEnd(pos.y);		
	}	
	
	public static void UpdateTagsWordIndex(List<? extends ILabelTag> tags,   List<CoreLabel> tokens ){
			if(tags == null || tags.size() < 1)
				return;
			
			List<Position> positions = NLPTool.GetTokenCharPosition(tokens);
			for(ILabelTag tag: tags){
				ILabelTag.SetTagWordIndex(positions, tag);
			}


	}
	
	public static void UpdateTagsWordIndex(ILabelTag tag,   List<CoreLabel> tokens ){
		if(tag == null )
			return;
		
		List<Position> positions = NLPTool.GetTokenCharPosition(tokens);
		ILabelTag.SetTagWordIndex(positions, tag);
	}
}

