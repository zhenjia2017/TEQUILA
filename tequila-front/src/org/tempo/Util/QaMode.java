package org.tempo.Util;

/** 
* 
* @Description: Underlying KB-QA option.
*  
*/


public enum QaMode {
	
	 
		AQQU("http://sedna.mpi-inf.mpg.de:8995"),  //use AQQU as underlying KB-QA
		QUINT("http://sedna.mpi-inf.mpg.de:8996"),  //use QUINT as underlying KB-QA
		
		//use the sub-question answer file when underlying KB-QA is not available
		
		//sub-question answers from aqqu
        OFFLINE(CGlobalConfiguration.GloabalSourceFolder+"\\dictionary\\TempQuestions_allsubquestion_aqqu.txt"); //on Windows 
		//OFFLINE(CGlobalConfiguration.GloabalSourceFolder+"/dictionary/TempQuestions_allsubquestion_aqqu.txt");  //on Linux
		
	    //sub-question answers from quint
	    //OFFLINE(CGlobalConfiguration.GloabalSourceFolder+"\\dictionary\\TempQuestions_allsubquestion_quint.txt"); //on Windows 
       //OFFLINE(CGlobalConfiguration.GloabalSourceFolder+"/dictionary/TempQuestions_allsubquestion_quint.txt");  //on Linux

	    private String QAService;

		private QaMode(String QAService) { 
	    	this.QAService = QAService;
            
        }  
		
		public String getMode() {  
	        return QAService;  
	    } 
    } 
		


