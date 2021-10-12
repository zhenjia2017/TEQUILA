package org.tempo.Util;

import org.tempo.QuestionAnswer.CQAServiceFactory;

/** 
* @ClassName: CGlobalConfiguration 
* @Description: Set the global configurations.
*  
*/

import org.tempo.SentenceAnalysis.Dictionary.CTagEntityFromDictionary;

public class CGlobalConfiguration {
	
	public static CTagEntityFromDictionary entitySearch =null;
	public static final String CurrentRefDate="2018-9-1";
	public static final int HttpTimeout = 180000; //(ms), about 3 min
		
	/** 
	on Windows
	*/
	public static final String GloabalSourceFolder =".\\source";
	public static final String HeilderTime_TREETAGGERHOME="C://TreeTagger";  //on Windows
	public static final String DictionaryFilePath = CGlobalConfiguration.GloabalSourceFolder + "\\dictionary\\event_dictionary_only.txt";
	public static final String PairNameFilePath = CGlobalConfiguration.GloabalSourceFolder + "\\dictionary\\temporal-predicate-pairs";
	public static final String MidNameDescription = CGlobalConfiguration.GloabalSourceFolder + "\\dictionary\\TempQuestions_mid_name_wiki_des.txt";
	public static final String QAServiceAddr_Or_QAFilePath= CGlobalConfiguration.GloabalSourceFolder 
			+ "\\dictionary\\TempQuestions_allsubquestions_aqqu.txt";	//sub-question answers file 

	/** 
	on Linux
	*/
	/*public static final String GloabalSourceFolder ="./source";
	public static final String HeilderTime_TREETAGGERHOME="/home/zjia/treetagger";  //on Linux
	public static final String DictionaryFilePath = CGlobalConfiguration.GloabalSourceFolder + "/dictionary/event_dictionary_only.txt";
	public static final String PairNameFilePath = CGlobalConfiguration.GloabalSourceFolder + "/dictionary/temporal-predicate-pairs";
	public static final String MidNameDescription = CGlobalConfiguration.GloabalSourceFolder + "/dictionary/TempQuestions_mid_name_wiki_des.txt";
*/	
	
	
		
	public static boolean TestMode = false;
	public static void Initial(){
		
	}
	
	

}

