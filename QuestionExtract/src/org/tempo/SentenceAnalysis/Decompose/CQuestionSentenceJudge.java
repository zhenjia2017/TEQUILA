package org.tempo.SentenceAnalysis.Decompose;

import java.util.List;

import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;

import java.util.HashMap;
import edu.stanford.nlp.ling.CoreLabel;


/** 
* @ClassName: CQuestionSentenceJudge 
* @Description: recognize sentence's type by regression rule.
*  
*/
public class CQuestionSentenceJudge {
	static HashMap<String, CQuestionSentenceJudge> allSentenceJudge;
	
	public enum EnumRuleMatchMode{NotMatch, MatchOne, MatchTwo, MatchMore};
	
	CTokenRegxParser  SentencePaser;
	
	public CQuestionSentenceJudge(CTokenRegxParser paser){
		this.SentencePaser = paser;
	}
	
	/** 
	* @Title: JudgeSentenceType 
	* @Description: judge sentence type from a string .
	* @param text
	* @return   
	*/
	public EnumRuleMatchMode JudgeSentenceType(String text){
		return GetRuleMatchMode(this.SentencePaser.FindPatterns(text));
	}

	
	/** 
	* @Title: JudgeSentenceType 
	* @Description: judge sentence type from NLP CoreLabels;
	* @param text
	* @return   
	*/
	public EnumRuleMatchMode  JudgeSentenceType(List<CoreLabel> text){
		return GetRuleMatchMode(this.SentencePaser.FindPatterns(text));
	}
	
	/** 
	* @Title: MatchSentence 
	* @Description:  get report for a CoreLabels
	* @param text
	* @return   
	*/
	public CSentenceAnalysisReport  MatchSentence(List<CoreLabel> text){
		return this.SentencePaser.FindPatterns(text);
	}
	
	/** 
	* @Title: MatchSentence 
	* @Description: get report for a text
	* @param text
	* @return   
	*/
	public CSentenceAnalysisReport  MatchSentence(String text){
		return this.SentencePaser.FindPatterns(text);
	}	
	
	/** 
	* @Title: GetSentenceType 
	* @Description: get sentence type according to sentence analysis report
	* @param result
	* @return   
	*/
	public static EnumRuleMatchMode GetRuleMatchMode(CSentenceAnalysisReport result){
		if( result == null ){
			return EnumRuleMatchMode.NotMatch;
		}
		
		//check rule match times
		int nMatched = result.GetMatchedTimes();
		if( nMatched < 1)
			return EnumRuleMatchMode.NotMatch;
		
		if( nMatched == 1)
			return EnumRuleMatchMode.MatchOne;
			
		if( nMatched == 2)
			return EnumRuleMatchMode.MatchTwo;	
		
		return EnumRuleMatchMode.MatchMore;
		
	}
	
	/** 
	* @Title: GetSentenceClassfication 
	* @Description: get rule group judge.
	* @param groupName
	* @return   
	*/
	public static CQuestionSentenceJudge   GetSentenceClassfication(String groupName){
		
		//build rules map
		if( allSentenceJudge == null)
			allSentenceJudge = new HashMap<String, CQuestionSentenceJudge>();
		
		if( allSentenceJudge.containsKey(groupName))
			return allSentenceJudge.get(groupName);		
		
		//find a rule group
		List<String> lstRule = CTokenRule.GetRules(groupName);
		if( lstRule == null)
			return null;
		
		//parse and add rules for group rule
		CTokenRegxParser paser1 = new CTokenRegxParser(groupName);
		for(String r : lstRule)
			paser1.AddRule(r);
		
		CQuestionSentenceJudge judge = new CQuestionSentenceJudge(paser1);
		allSentenceJudge.put(groupName, judge); // map group rule to Judge object
		
		return judge;
		
	}

	/** 
	* @Title: CreateComplexCompleteSentenceClassfication 
	* @Description: create Judge handler for complex complete sentence
	* @return   
	*/
	public static CQuestionSentenceJudge   CreateComplexCompleteSentenceClassfication(){
		return GetSentenceClassfication(CTokenRule.STR_ComplexComplete);
		
	}
	
	/** 
	* @Title: CreateComplexIncompleteSentenceClassfication 
	* @Description: create Judge handler for complex Incomplete sentence
	* @return   
	*/
	public static CQuestionSentenceJudge   CreateComplexIncompleteSentenceClassfication(){
		return GetSentenceClassfication(CTokenRule.STR_ComplexIncomplete);

	}
	
	/** 
	* @Title: CreateDATESentenceClassfication 
	* @Description: create Judge handler for Date and ordinal sentence
	* @return   
	*/
	public static CQuestionSentenceJudge   CreateDATESentenceClassfication(){
		return GetSentenceClassfication(CTokenRule.STR_Date_Ordinal);
	}
	
	/** 
	* @Title: CreateORDINALSentenceClassfication 
	* @Description: create Judge handler for ordinal sentence
	* @return   
	*/
	public static CQuestionSentenceJudge   CreateORDINALSentenceClassfication(){
		return GetSentenceClassfication(CTokenRule.STR_Ordinal);
	}
	
	/** 
	* @Title: CreateEventSentenceClassfication 
	* @Description: create Judge handler for event sentence
	* @return   
	*/
	public static CQuestionSentenceJudge   CreateEventSentenceClassfication(){
		return GetSentenceClassfication(CTokenRule.STR_Event);
	}
	
	/** 
	* @Title: CreateWhenSentenceClassfication 
	* @Description: create Judge handler for When sentence
	* @return   
	*/
	public static CQuestionSentenceJudge   CreateWhenSentenceClassfication(){
		return GetSentenceClassfication(CTokenRule.STR_When);
	}
	


}
