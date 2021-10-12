package org.tempo.SentenceAnalysis;

import java.util.List;

/** 
* @ClassName: SentenceSectionDescription 
* @Description: section description for text.
*  
*/
public	class SentenceSectionDescription{
	public String RegexRule;
	public int Start;
	public int End; //not inclue the end position  , text is from [start, end)
	public int CharStart;
	public int CharEnd;
	//public String Desciption;
	public int Mark;
	
	protected SentenceSectionDescription(){
		this.Start = -1;
		this.End = 0;
		this.RegexRule = "";
		this.Mark = 0;
		this.CharStart = 0;
		this.CharEnd = 0;
	}

	public SentenceSectionDescription(int nstart, int nend){
		this.Start = nstart;
		this.End = nend;
		this.RegexRule = "";
		this.Mark = 0;
		this.CharStart = 0;
		this.CharEnd = 0;
	}
	
	public SentenceSectionDescription(int nstart, int nend, String rule, int nmarked){
		this.Start = nstart;
		this.End = nend;
		this.RegexRule = rule;
		this.Mark = nmarked;
		this.CharStart = 0;
		this.CharEnd = 0;
	}
	
	
	public boolean HasMatchedRule(){
		return (this.RegexRule != null && this.RegexRule.length()>0);
	}
	
	public int Size(){
		return (this.End - this.Start );
	}
	
	/** 
	* @Title: OffsetSection 
	* @Description: count new values by the offset on baseSection.
	* @param baseSection
	* @param resetSection
	* @param offset   
	*/
	public static void OffsetSection(SentenceSectionDescription baseSection, SentenceSectionDescription resetSection, int offset){
		resetSection.Start +=  baseSection.Start + offset;
		resetSection.End +=  baseSection.Start + offset;
	}

	
	/** 
	* @Title: OffsetSection 
	* @Description: count sections by off
	* @param baseSection
	* @param resetSections
	* @param offset   
	*/
	public static void OffsetSection(SentenceSectionDescription baseSection, List<SentenceSectionDescription> resetSections, int offset){
		for(SentenceSectionDescription sec : resetSections){
			OffsetSection(baseSection, sec, offset);
		}
	}
	
	
	/** 
	* @Title: MergeSection 
	* @Description: joint sections to one section.
	* @param resetSection
	* @param mergedSections   
	*/
	public static void MergeSection(SentenceSectionDescription resetSection, List<SentenceSectionDescription> mergedSections ){		
		for(SentenceSectionDescription sec : mergedSections){
			if(resetSection.Start > sec.Start){
				resetSection.Start = sec.Start;
				resetSection.CharStart = sec.CharStart;
			}
			if(resetSection.End < sec.End){
				resetSection.End =  sec.End;
				resetSection.CharEnd = sec.CharEnd;
			}
		}
	}
	
	/** 
	* @Title: MergeSection 
	* @Description: joint two sections.
	* @param resetSection
	* @param mergedSection   
	*/
	public static void MergeSection(SentenceSectionDescription resetSection, SentenceSectionDescription mergedSection ){
		if(resetSection.Start > mergedSection.Start){
			resetSection.Start = mergedSection.Start;
			resetSection.CharStart = mergedSection.CharStart;
		}
		
		
		if(resetSection.End < mergedSection.End){
			resetSection.End =  mergedSection.End;
			resetSection.CharEnd = mergedSection.CharEnd;
		}
		
	}
	

}