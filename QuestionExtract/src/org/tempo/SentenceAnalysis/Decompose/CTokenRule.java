package org.tempo.SentenceAnalysis.Decompose;

import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

/** 
* @ClassName: CTokenRule 
* @Description: token regular expression rules identifying complete,incomplete sentence, and data,ordinal words.
*  
*/
public class CTokenRule{
	public final static String STR_Simple="SIMPLEQUESTION";
	public final static String STR_ComplexComplete="COMPLEXQUESTION_COMPLETE";
	public final static String STR_ComplexComplete_Divide="COMPLETE_DIVIDE";
	
	public final static String STR_ComplexIncomplete="COMPLEXQUESTION_UNCOMPLETE";
	public final static String STR_ComplexIncomplete_Divide="UNCOMPLETE_DIVIDE";
	
	public final static String STR_When="WHEN";
	public final static String STR_Date="DATE";
	public final static String STR_Date_Ordinal="DATE_Ordinal";
	public final static String STR_Ordinal="ORDINAL";
	public final static String STR_Event="EVENT";
	public final static String STR_Unknown="Unknown";
	
	public final static String[] RelationWords = new String[]{"before","after","when","since","until","till","while","during","prior to",
															  "BEFORE","AFTER","WHEN","SINCE","UNTIL","TILL","WHILE","DURING","PRIOR TO"};
	
	static HashMap<String, List<String> > groupRules;
	

	public static List<String> GetRules(String ruleGroupName){
		if( groupRules == null)
			Init();
		
		List<String> group = groupRules.get(ruleGroupName);
		
		return group;
	}
	
	public static HashMap<String, List<String> >  GetAllRules(){
		if( groupRules == null)
			Init();
		return groupRules;
	}	
	
	
	/** 
	* @Title: Init 
	* @Description: initial expressions table   
	*/
	static void Init(){
		final String STR_TRelation1= "[lemma:/before|after|when|since|until|till|while|during|BEFORE|AFTER|WHEN|SINCE|UNTIL|TILL|WHILE|DURING/]";		
		final String STR_TRelation3= "[lemma:/since|from|after|before|untill|till|during|in|on|SINCE|FROM|AFTER|BEFORE|UNTILL|TILL|DURIING|IN|ON/]";
	

		
		groupRules = new HashMap<String, List<String> >();		
		
		List<String> lst = Arrays.asList(
		
	//		" [pos:/W.*/] []* [pos:/VB.*/] []* " + STR_TRelation1 + " []* [pos:/NN.*|PRP|JJ|FW|RB/] []* [pos:/VB.*/]",
			" [pos:/W.*/] []* [pos:/VB.*/] []* " + STR_TRelation1 + " []* [pos:/VB.*/]"
		);
		
		groupRules.put(STR_ComplexComplete, lst);
		
		lst = Arrays.asList(
//				 STR_TRelation1 + " []* [pos:/NN.*|PRP|JJ|FW|RB/] []* [pos:/VB.*/]"
//				 STR_TRelation1 + " []* [pos:/JJ/] []* [pos:/VB.*/]",
//				 STR_TRelation1 + " []* [pos:/FW/] []* [pos:/VB.*/]"
				 STR_TRelation1 + " []* [pos:/VB.*/]"
				);
			
		groupRules.put(STR_ComplexComplete_Divide, lst);
		
		
		
		lst = Arrays.asList(
				"[pos:/W.*/] []* [pos:/VB.*/] []* " + STR_TRelation1 + " [pos:/DT|IN/]? [pos:/NN.*|PRP.|JJ|FW/] [!{pos:/VB.*/}]* $",
				"[pos:/W.*/] []* [pos:/VB.*/] []* " + "[lemma:/prior/] /to/" + " [pos:/DT|IN/]? [pos:/NN.*|PRP.|JJ|FW/] [!{pos:/VB.*/}]* $"
//				"[pos:/W.*/] []* [pos:/VB.*/] []* " + STR_TRelation1 + " [pos:/DT|IN/]? [pos://] [!{pos:/VB.*/}]* $",
//				"[pos:/W.*/] []* [pos:/VB.*/] []* " + STR_TRelation1 + " [pos:/DT|IN/]? [pos://] [!{pos:/VB.*/}]* $",
//				"[pos:/W.*/] []* [pos:/VB.*/] []* " + STR_TRelation1 + " [pos:/DT|IN/]? [pos://] [!{pos:/VB.*/}]* $"
			);
			
	    groupRules.put(STR_ComplexIncomplete, lst);		

		lst = Arrays.asList(
				STR_TRelation1 + " [pos:/DT|IN/]? [pos:/NN.*|PRP.|JJ|FW/] [!{pos:/VB.*/}]* $",
				"[lemma:/prior/] /to/" + " [pos:/DT|IN/]? [pos:/NN.*|PRP.|JJ|FW/] [!{pos:/VB.*/}]* $"
//				STR_TRelation1 + " [pos:/DT|IN/]? [pos:/PRP./] [!{pos:/VB.*/}]* $",
//				STR_TRelation1 + " [pos:/DT|IN/]? [pos:/JJ/] [!{pos:/VB.*/}]* $",
//				STR_TRelation1 + " [pos:/DT|IN/]? [pos:/FW/] [!{pos:/VB.*/}]* $"
			);
			
	    groupRules.put(STR_ComplexIncomplete_Divide, lst);		
	    
	    
		lst = Arrays.asList(
//				"^[pos:/W.*/] (/the/)? /time|day|date|dates|year|years/",
//				"^[pos:/W.*/] (/the/)? /year|date|century/",
//				"^/what\'s|What\'s/ (/a/)? /year|years/",
//				"^/what|whats|What|Whats/ (/is/)? (/a/)? /year|years/",
//				"^/when|When|WHEN/"	,
				"^[pos:/W.*/] [lemma:/the/]? [lemma:/time|day|TIME|DAY/]",
				"^[pos:/W.*/] [lemma:/the/]? [lemma:/date|dates|DATE|DATES/]",
				"^[pos:/W.*/] [lemma:/the/]? [lemma:/year|years|century|YEAR|YEARS|CENTURY/]",
				"^[lemma:/what\'s|What\'s|WHAT\'S/] (/a/)? /year|years|YEAR|YEARS/",
				"^[lemma:/what|whats|WHAT|WHATS/] (/is/)? (/a/)? [lemma:/year|years|YEAR|YEARS/]",
				"^[lemma:/what|whats|WHAT|WHATS/] [lemma:/day|DAY/]? (/of/)? (/the/)? [lemma:/year|YEAR/]",
				"^[lemma:/when|WHEN/]",
				"^" + STR_TRelation3 + " [lemma:/when|WHEN/]"
				);
			
	    groupRules.put(STR_When, lst);		
	   
		lst = Arrays.asList(
				"([ ner:/DATE|TIME|DURATION/ ]+ )",
				"[ ner:/DATE|TIME|DURATION/ ]+ /and|to|-/  [ ner:/DATE|TIME|DURATION/ ]+ ",
				STR_TRelation3 + " ([ ner:/DATE|TIME|DURATION/ ]+)",
				STR_TRelation3 + " [ ner:/DATE|TIME|DURATION/ ]+ /and|to|-/[ ner:/DATE|TIME|DURATION/ ]+ "
			);
			
	    groupRules.put(STR_Date, lst);		

	    
		lst = Arrays.asList(
				"[ner:/ORDINAL/] [pos:/CD/]?",
				"[lemma:/last|LAST/] [pos:/CD/]?"
				
				);
			
	    groupRules.put(STR_Ordinal, lst);		
	    
		lst = Arrays.asList(
				"[ner:/ORDINAL/] [pos:/CD/]?",
				"[lemma:/last|LAST/] [pos:/CD/]?",
				"([ ner:/DATE|TIME|DURATION/ ]+ )",
				"[ ner:/DATE|TIME|DURATION/ ]+ /and|to|AND|TO|-/  [ ner:/DATE|TIME|DURATION/ ]+ ",
				STR_TRelation3 + " ([ ner:/DATE|TIME|DURATION/ ]+)",
				STR_TRelation3 + " [ ner:/DATE|TIME|DURATION/ ]+ /and|to|AND|TO|-/  [ ner:/DATE|TIME|DURATION/ ]+ "
				
			);
			
	    groupRules.put(STR_Date_Ordinal, lst);		
	    
	    
	}
	
	/** 
	* @Title: IsRuleOf 
	* @Description: judge if a rule is in a rule group. 
	* @param rule
	* @param ruleGroup
	* @return   
	*/
	public static boolean IsRuleOf(String rule, String ruleGroup){
		if(ruleGroup == null || ruleGroup.length() < 1)
			return false;
		
		List<String> groupRule = CTokenRule.GetRules(ruleGroup);
		if( groupRule == null || groupRule.size()<1)
			return false;
		
		return (groupRule.contains(rule));
	}
	
	
	/** 
	* @Title: getMatchSignalWord 
	* @Description: find signal word by keyword
	* @param keyword
	* @return   
	*/
	public static String getMatchSignalWord(String keyword){
		for(String str: RelationWords){
			if(str.compareTo(keyword) ==0  || str.contains(keyword + " ")){
				return str;
			}	
		}
		return "";
	}


}
