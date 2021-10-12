package org.tempo.SentenceAnalysis.Decompose;

import java.util.*;

import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.NLPTool;
import org.tempo.SentenceAnalysis.SentenceSectionDescription;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.tokensregex.*; 

/** 
* @ClassName: CTokenRegxParser 
* @Description: Load Token Rules, and find match text section according to the rules. 
*  
*/
public class CTokenRegxParser {
	
	/** Name:Parser Name */  
	String Name;
	
	/** RulePatterns: match patterns */  
	Map< String, TokenSequencePattern>  RulePatterns;
	
	/** OrderRules: the rules is ordered by length */  
	List<String> OrderRules;

	public Map< String, TokenSequencePattern> getRegexRules() {
		return this.RulePatterns;
	}

	
	public CTokenRegxParser(String paserName){
		this.Name = paserName;
		this.RulePatterns = new HashMap< String, TokenSequencePattern>();
	}
	
	/** 
	* @Title: AddRule 
	* @Description: Load expression rule.
	* @param newRule
	* @return   
	*/
	public boolean AddRule(String newRule){
		if( this.RulePatterns.containsKey(newRule))
			return false;
		
		//generate token pattern from the rule text.
		TokenSequencePattern pattern = GetPattern(newRule);
		
		if( pattern == null)
			return false;
		
		
		this.RulePatterns.put(newRule, pattern);		
		
		//order the rules by text length
		this.OrderRules = GetOrderRuleString();	
		return true;
		
	}
	
	/** 
	* @Title: GetOrderRuleString 
	* @Description: order  rules.
	* @return   
	*/
	List<String> GetOrderRuleString(){

		Comparator<String> c = new Comparator<String>(){  
		        public int compare(String s1, String s2) {  
		            if( s1.length() > s2.length() )
		            	return -1;
		            
		            if( s1.length() < s2.length())
		            	return 1;
		            
		            return  (- s1.compareTo(s2)); 
		        }  
		    };  
		    
		List<String> lst =  new ArrayList<String>();
		lst.addAll(this.RulePatterns.keySet());
		    
		Collections.sort(lst,c);      
		   
		return lst;
	}
	

	/** 
	* @Title: GetPattern 
	* @Description: create and compile token pattern by rule text
	* @param newRule
	* @return   
	*/
	TokenSequencePattern GetPattern(String newRule){
	     TokenSequencePattern pattern = null;
	     try{
	    	 pattern =    TokenSequencePattern.compile(newRule);
	     }catch(Exception e){
	    	 System.out.println(e.getMessage());
	     }
	 
	     return pattern;
	    
	}

	/** 
	* @Title: Find 
	* @Description: find the pointed patterns in the text.
	* @param text
	* @return   
	*/
	public CSentenceAnalysisReport  FindPatterns(String text){
		Annotation document = new Annotation(text);
		NLPTool.GetStandfordNLPIntance().annotate(document);

	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
	    CSentenceAnalysisReport r = FindPatterns(sentences.get(0));
	    if( r!=null)
	    	r.Document = document;
	    return r;
	}
	
	/** 
	* @Title: MatchSentence 
	* @Description: find pattern in tokens.
	* @param tokens
	* @return   
	*/
	public List<SentenceSectionDescription>  MatchSentence(List<CoreLabel> tokens){
		SentenceSectionDescription baseSection = new SentenceSectionDescription(0, tokens.size());
		return (Find(tokens, 0, baseSection));		
	}
	
	
	/** 
	* @Title: Find 
	* @Description: find very patterns in rules table , and return matched pattern section.
	* @param tokens
	* @param testRule
	* @param baseSection
	* @return   
	*/
	List<SentenceSectionDescription> Find(List<CoreLabel> tokens, int testRule, SentenceSectionDescription baseSection){
		
		List<SentenceSectionDescription>  lstResult = new ArrayList<SentenceSectionDescription>();
		//int nEnd = (baseSection.End+1>=tokens.size())? baseSection.End : baseSection.End + 1;
		List<CoreLabel> secNodeList = tokens.subList(baseSection.Start, baseSection.End);
		
		for(int ruleIndex=testRule;( ruleIndex < this.RulePatterns.size()) && (lstResult.size() < 1) ; ruleIndex++){
			String rule = this.OrderRules.get(ruleIndex);
			TokenSequencePattern pattern = this.RulePatterns.get(rule);
		
			TokenSequenceMatcher matcher  = pattern.getMatcher(secNodeList);	
			
			List<SentenceSectionDescription> findresults = GetResult(matcher, secNodeList, rule);
			
			for (SentenceSectionDescription sec : findresults){
				if( sec.HasMatchedRule() || sec.Mark > 0 || ruleIndex == this.OrderRules.size() -1) {
					sec.Mark = 1;
					SentenceSectionDescription.OffsetSection(baseSection,sec, 0);
					lstResult.add(sec);
				}
				else{
					List<CoreLabel> newSubSec = secNodeList.subList(sec.Start, sec.End);
					SentenceSectionDescription newSec = new SentenceSectionDescription(0, newSubSec.size());
					List<SentenceSectionDescription> lstDeep = Find(newSubSec, ruleIndex+1, newSec);				
					if( lstDeep.size() > 0){
						SentenceSectionDescription.OffsetSection(baseSection, lstDeep, sec.Start);
						lstResult.addAll(lstDeep);
					}
				}
			}
		}
		
		if( lstResult.size() < 1)
			lstResult.add(baseSection);
		
		if(testRule==this.RulePatterns.size() -1){
			for(SentenceSectionDescription sec : lstResult){
					sec.Mark = 1;
			}
		}
		
		if( lstResult.size() > 0)
			NLPTool.SetCharPostion(tokens, lstResult);
		
		return lstResult;
		
	}
		

	
	/** 
	* @Title: Find 
	* @Description: 
	* @param sentence
	* @return   
	*/
	public CSentenceAnalysisReport  FindPatterns(CoreMap sentence){
		List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
		
		CSentenceAnalysisReport r = FindPatterns(tokens);
		
		if( r != null){
			r.TextTokens = sentence;
		}
		return r;
	}
	
	
	public CSentenceAnalysisReport  FindPatterns(List<CoreLabel> tokens){
		
		SentenceSectionDescription baseSection = new SentenceSectionDescription(0, tokens.size());
		
		List<SentenceSectionDescription> lstSections = Find(tokens, 0, baseSection);//find patterns' sections
		
		if(lstSections == null || lstSections.isEmpty())
			return null;
		
		CSentenceAnalysisReport result = new CSentenceAnalysisReport();
		
		result.PaserName = this.Name;
	//	result.Text = sentence.get(TokensAnnotation.class).toString();
		result.Text = tokens.toString();
		//result.TextTokens =  tokens;
		result.Sections.addAll(lstSections);
		
		return result;
	}
	

	/** 
	* @Title: GetLastSection 
	* @Description: get last section
	* @param result
	* @return   
	*/
	SentenceSectionDescription GetLastSection( List<SentenceSectionDescription> result){
		if( result.size() < 1)
			return null;
		else
			return result.get(result.size() - 1);
		
	}
	
     /** 
    * @Title: GetResult 
    * @Description: get text section for the  matcher.
    * @param matcher
    * @param tokens
    * @param rule
    * @return   
    */
    List<SentenceSectionDescription> GetResult( TokenSequenceMatcher matcher,  List<CoreLabel> tokens, String rule){
    	 
    	 List<SentenceSectionDescription> result = new  ArrayList<SentenceSectionDescription>();	
		
		SentenceSectionDescription lastSec;
		while( matcher.find()){
			lastSec = GetLastSection(result ); //lastsection)
			
			int nStart = matcher.start();
			int nEnd = matcher.end();
			
			if(lastSec == null && nStart > 0){ //first find , and section is started after first token
				result.add(new SentenceSectionDescription(0, nStart));
			}else if( lastSec != null && nStart >= lastSec.End){ //find again, and section is started after lastSec.End +1  				
				result.add(new SentenceSectionDescription(lastSec.End, nStart));
			}
			result.add(new SentenceSectionDescription(nStart, nEnd, rule, 1));		
		}
		

		lastSec = GetLastSection( result);
		if(lastSec != null && lastSec.End < tokens.size()){
			result.add(new SentenceSectionDescription(lastSec.End, tokens.size()));		
		}
	
		return  result;
	}
	

}
