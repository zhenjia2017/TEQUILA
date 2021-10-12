package org.tempo.SentenceAnalysis;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.tempo.SentenceAnalysis.TimeTool.HeildeTimeWrapper;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TrueCaseTextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.util.CoreMap;


/** 
* @ClassName: NLPTool 
* @Description: NLP functions
*  
*/
public class NLPTool {
	
	private static AnnotationPipeline cAnotationPipline;
	
	private static HeildeTimeWrapper cHeilderTime;
	
	public static String STR_SYMPLE = "~!@#$%^&*()_+{}|:<>?,./;[]\\=-'\"";
	
	/** PosCharMap: signal char map for truecase */  
	static HashMap<String,String> PosCharMap=new HashMap<String, String>(){
		{
				put("-LRB-","("); put("-RRB-",")"); 
				put("-LSB-","["); put("-RSB-","]");
				put("-LCB-","["); put("-RCB-","]");
		}
	};
	
	/** 
	* @Title: GetHeilderTimeIntance 
	* @Description: Create HeilderTime instance
	* @return   
	*/
	public static HeildeTimeWrapper GetHeilderTimeIntance(){
		if( cHeilderTime == null){
			cHeilderTime = new HeildeTimeWrapper();
			cHeilderTime.Initialize();
		}
		
		return cHeilderTime;
	}
	
	/** 
	* @Title: GetStandfordNLPIntance 
	* @Description: get NLP instance
	* @return   
	*/
	public static AnnotationPipeline GetStandfordNLPIntance(){
		return NLPTool.cAnotationPipline;
	}
	
	private NLPTool(){
		
	}
	
	public static boolean InitStandfordNLP(){
		return NLPTool.InitStandfordNLP("", "");
	}
	
	/** 
	* @Title: GetSimepleNLPTokenTool 
	* @Description: get NLP instance with minimum parse.
	* @return   
	*/
	public static AnnotationPipeline GetSimepleNLPTokenTool(){
		   Properties props = new Properties();
		   props.put("annotators", "tokenize, ssplit, pos");
		   return (new StanfordCoreNLP(props));
		}

	
	/** 
	* @Title: GetNLPTokenTool 
	* @Description: get truecase NLP instance
	* @return   
	*/
	public static AnnotationPipeline GetTruecaseNLP(){
	   Properties props = new Properties();
	   props.put("annotators", "tokenize, ssplit, pos, lemma, truecase");
	   return (new StanfordCoreNLP(props));
		
//		return GetStandfordNLPIntance();
	}
	
	/** 
	* @Title: InitStandfordNLP 
	* @Description: create a full parse NLP instance.
	* @param annotatorsParam
	* @param otherParam
	* @return   
	*/
	public static boolean InitStandfordNLP(String annotatorsParam, String otherParam){
		if( NLPTool.cAnotationPipline != null)
			return false;
		
	   Properties props = new Properties();
	   if( annotatorsParam==null || annotatorsParam.trim().length()<1){
		   	props.put("annotators", "tokenize, ssplit,pos, lemma, ner, parse");//, dcoref
	   }

	   //props.put(key, value)
	   NLPTool.cAnotationPipline = new StanfordCoreNLP(props);
	   //NLPTool.cAnotationPipline.addAnnotator(new TimeAnnotator("sutime", props));
		
	   return (NLPTool.cAnotationPipline != null);
	}
	
	public static CoreMap GetSentence(Annotation document, int sentenceOrder){		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		if(sentences == null || sentences.size()<(sentenceOrder+1))
			return null;
		return  sentences.get(sentenceOrder);
	}
	
	/** 
	* @Title: GetTokens 
	* @Description: Parse a document to tokens.
	* @param document
	* @param sentenceOrder
	* @return   
	*/
	public static List<CoreLabel> GetTokens(Annotation document, int sentenceOrder){		
		CoreMap sentence = GetSentence(document, sentenceOrder);
		if( sentence == null ) return null;
		
		return sentence.get(TokensAnnotation.class);
	}
	
	/** 
	* @Title: GetTextOfSection 
	* @Description: get text string of section by tokens.
	* @param tokens
	* @param sec
	* @return   
	*/
	public static String GetTextOfSection(List<CoreLabel> tokens, SentenceSectionDescription sec){
		StringBuilder buf = new StringBuilder();
		//joint token's word
		for(int i = sec.Start ; i < sec.End; i++){
			CoreLabel token = tokens.get(i);
	        String word = token.get(TextAnnotation.class);
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        String ne = token.get(NamedEntityTagAnnotation.class);
	        
	        buf.append(word + "\\" + pos + "\\" + ne + " " );
	       // buf.append(str)
		}
		return buf.toString();

	}
	
	/** 
	* @Title: SetCharPostion 
	* @Description: update section's position by tokens
	* @param tokens
	* @param sections   
	*/
	public static void  SetCharPostion(List<CoreLabel> tokens, List<SentenceSectionDescription> sections){
		for(SentenceSectionDescription sec: sections){
			if(sec.End > 0){
				sec.CharStart = tokens.get(sec.Start).get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
				sec.CharEnd = tokens.get(sec.End-1).get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
			}
		}
	}	
	
	/** 
	* @Title: GetTokenCharPosition 
	* @Description: get tokens' char positions
	* @param tokens
	* @return   
	*/
	public static List<Position>  GetTokenCharPosition(List<CoreLabel>  tokens){
		List<Position> wordPositions = new ArrayList<Position>();
		for(CoreLabel sec: tokens){
			 wordPositions.add(  new Position(
					 				sec.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
					 					sec.get(CoreAnnotations.CharacterOffsetEndAnnotation.class)));
			 
		
		}
		
		return wordPositions;
	}	


	
	/** 
	* @Title: Locate 
	* @Description: Locate tokens index by a char position
	* @param tokens
	* @param charSplitPos
	* @return   
	*/
	public static int Locate(List<CoreLabel> tokens, int charSplitPos){
		int start, end;
		//count which token cover the char position
		for(int i = 0; i < tokens.size(); i++){
			CoreLabel tk = tokens.get(i);
			start = tk.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class);
			end = tk.get(CoreAnnotations.CharacterOffsetEndAnnotation.class);
			
			if(start <= charSplitPos && end>=charSplitPos)
				return i;
			
		}
		return -1;
	}
	
	
	static String ConvertToSourceChar(String str){
		return PosCharMap.getOrDefault(str, str);
	}
	
	/** 
	* @Title: GetTextOfTokens 
	* @Description: get text of tokens.
	* @param tokens
	* @param start
	* @param end
	* @return   
	*/
	public static String GetTextOfTokens(List<CoreLabel> tokens, int start, int end){
	
		StringBuilder buf = new StringBuilder();
		for(int i = start ; i < end; i++){
			CoreLabel token = tokens.get(i);
	        String word = token.get(TextAnnotation.class);
	        word = ConvertToSourceChar(word);
	        buf.append(word + " " );
		}
		return buf.toString().trim();
	}

	/** 
	* @Title: GetTextOfTruecase 
	* @Description: get text of tokens by their truecase.
	* @param tokens
	* @param begin
	* @param end
	* @param splitString
	* @return   
	*/
	public static String GetTextOfTruecase(List<CoreLabel> tokens, int begin, int end, String splitString){
		if(splitString==null)
			splitString = " ";
		
		if( end < 0)
			end = tokens.size();
		
		if( begin < 0 || begin>=tokens.size() || end<=begin || end > tokens.size())
			return "";
		
		//joint trucase to one string
		StringBuilder buf = new StringBuilder();
		for(int i = begin; i < end; i++){
			CoreLabel tk = tokens.get(i);
			String pos = tk.get(PartOfSpeechAnnotation.class);

			if( pos.compareTo("POS") != 0 && pos.compareTo(".") !=0 ){
				buf.append(splitString);
			}
			
			String str1 = tk.get(TrueCaseTextAnnotation.class);
			str1 = ConvertToSourceChar(str1);
			buf.append( str1 );	
		}
	
		return buf.toString().trim();
	}
	
	/** 
	* @Title: GetTextBetweenWord 
	* @Description: get part of sentence text by token index range.
	* @param startIndex
	* @param endIndex
	* @param tokens
	* @param sentenceText
	* @return   
	*/
	public static String GetTextBetweenWord(int startIndex, int endIndex,List<CoreLabel> tokens, String sentenceText){
		if(startIndex > endIndex || endIndex > tokens.size())
			return "";
		
		if(startIndex <0)
			startIndex = 0;
		if(endIndex < 0)
			endIndex = tokens.size()-1;
		
		int start = tokens.get(startIndex).beginPosition();
		int end = tokens.get(endIndex).beginPosition();
		String str = sentenceText.substring(start, end);
		return str.trim();
	}
		
}
