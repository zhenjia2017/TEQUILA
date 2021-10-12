package org.tempo.Util;

public class CGlobalConfiguration {
	public static final String GloabalSourceFolder ="G:\\QA\\sigir2018-results-code-publish\\0515version_submission\\code\\frontend\\TEQUILA\\source";
	
	public static final String QAService_Selection=""; //AQQU, QUNIT, ""; if use offline service, set "". 
	
	public static final String QAService_AQQU="http://AQQU KBQA Server:8995"; //Please replace "QUINT KBQA server" and port with your KBQA service 
    public static final String QAService_QUINT="http://QUINT KBQA Server:8996"; //Please replace "QUINT KBQA server" and port with your KBQA service 

	public static final String QAServiceAddr_Or_QAFilePath= CGlobalConfiguration.GloabalSourceFolder 
					+ "\\dictionary\\TempQuestions_truecase_allsubquestion_aqqu.txt";	//sub-question answers file 

	public static final String HeilderTime_TREETAGGERHOME="C://TreeTagger";
	public static final String DictionaryFilePath = CGlobalConfiguration.GloabalSourceFolder + "\\dictionary\\event_dictionary_only.txt";
	public static final String PairNameFilePath = CGlobalConfiguration.GloabalSourceFolder + "\\dictionary\\temporal-predicate-pairs";
	public static final String CurrentRefDate="2013-1-1";
	
	public static final boolean RemoveDateTag = true;
	public static final boolean AlwaysAddNoDateAnswers = false;
	
	public static final int HttpTimeout = 180000; //(ms), about 3 min
	
	public static final boolean OnlyLatestOneByTimeFilter = false;
	public static final int SelectedRank = 1;  // 1 to 3;
	
	public static boolean TestMode = false;
	public static void Initial(){
		
	}
//	
//	public static String getQAServiceString(){
//		String strSv = QAService_Selection.toUpperCase().trim();
//		if( strSv.compareTo("AQQU") == 0)
//			return QAService_AQQU;
//		else  if(strSv.compareTo("QUINT") == 0) 
//			return QAService_QUINT;
//		else
//			return QAServiceAddr_Or_QAFilePath;
//	}

}
