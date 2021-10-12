package org.tempo.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;

/** 
* @ClassName: AnswerDataSection 
* @Description: the class is for data section in Tempo answer 
*/
public class AnswerDataSection{
	public List<CAnswerCandidate>Answers; //answers 
	public String SPARQL; //sparql string
	public String Predicate;  //predicate string
	public String BesPredicate;  //simlarity
	public String DateInCVTString;
	public String DateInPropertyString;


	public List<CDataInProperty> Date_in_property; //Data property list
	public String Entity;
	public String Relation;
	public String UpdateSPARQL;	
	

	public AnswerDataSection(){
		this.Answers = new ArrayList<CAnswerCandidate>();
		this.Date_in_property =  new ArrayList<CDataInProperty>();
		this.SPARQL = "";
		this.Predicate ="";
		this.BesPredicate ="";
		this.DateInCVTString = "";
		this.DateInPropertyString="";
		this.Entity = "";
		this.Relation = "";
		this.UpdateSPARQL = "";
	}
	
	public List<CAnswerCandidate> getAnswers(){
		return this.Answers;		
	}
	
	public List<CDataInProperty> getDate_in_property(){
		return this.Date_in_property;
	}
	
	public String getPredicate(){
		return this.Predicate;
	}
	
	public String getBestPredicate(){
		return this.BesPredicate;
	}

	public String getEntity() {
		return  this.Entity;
	}
	
	public String getRelation() {
		return  this.Relation;
	}
	
	public String getUpdateSPARQL() {
		return  this.UpdateSPARQL;
	}
	
	public boolean HasDateCandidate(){
		return (this.Date_in_property.size()>0);
	}
	
	public boolean HasQASystemAnswer(){
		return (this.Answers.size()>0);
	}	
	
	public String getDateInPropertyString() {
		return DateInPropertyString;
	}		
	
	public String getDateInCVTString() {
		return DateInCVTString;
	}

	
	/** 
	* @Title: GetQAAnswer 
	* @Description: get pointed answer
	* @param nOrder
	* @return   
	*/
	public String GetQAAnswer(int nOrder){
		if(this.Answers.size()<=nOrder)
			return "";
		
		return this.Answers.get(nOrder).getAnswerString();
	}
	
	/** 
	* @Title: GetQAAnswers 
	* @Description: get all answers string
	* @return   
	*/
	public List<String> GetQAAnswers(){
		List<String> lst = new ArrayList<String>();
		for(CAnswerCandidate dt : this.Answers){
			lst.add(dt.getAnswerString());
		}
		
		return lst;		

	}
	
	/** 
	* @Title: getQAAnswerString 
	* @Description: get QA default answer
	* @return   
	*/
	public String getQAAnswerString(){
		StringBuilder buf = new StringBuilder();
		if(this.Answers == null || this.Answers.size() < 1)
			return "";
		
		for(int i = 0; i < this.Answers.size(); i++){
			//buf.append("\"");
			buf.append(this.Answers.get(i));
			//buf.append("\"");
			//if(i < (this.Answers.size()-1))
				buf.append(";");
					
		}
		return buf.toString();
	}
	

	
	/** 
	* @Title: getAnswersWithoutDateInfo 
	* @Description: get candidate answer which has no any date
	* @return   
	*/
	public List<CAnswerCandidate>  getAnswersWithoutDateInfo(){
		List<CAnswerCandidate > lst = new ArrayList<CAnswerCandidate>();
		for(CAnswerCandidate anw : this.Answers){
			if( this.AnswerHasDate(anw) == false)
				lst.add(anw);				
		}
		
		return lst;
	}
	
	/** 
	* @Title: AnswerHasDate 
	* @Description: check if a answer candidate has a date
	* @param anw
	* @return   
	*/
	boolean AnswerHasDate(CAnswerCandidate anw){
		if(this.Date_in_property.size()<1) 
			return false;
		
		for(CDataInProperty p : this.Date_in_property){
			if(p.MID.compareTo(anw.MID) == 0)
				return true;
		}
		return false;
	}
	
		
	
	
	/** 
	* @Title: CheckIsAnswerIn 
	* @Description: get How many gold answers are included in answer candidates
	* @param goldAnswers
	* @param candidates
	* @return   0: not found; 1:find part ; 2 : found all
	*/
	public static int CheckIsAnswerIn(List<String> goldAnswers, List<CAnswerCandidate> candidates){
		if(candidates.size() < 1)
			return 0;
		
		int nFind = 0;
		for(int i = 0; i< goldAnswers.size(); i++){
			String lowGoldAnswer = goldAnswers.get(i).toLowerCase().trim();
			for(CAnswerCandidate cad: candidates){
				if(cad.Answer.toLowerCase().contains(lowGoldAnswer)){
					nFind++;
					break;
				}
			}
		}
		
		if( nFind <=0 ) {
			return 0;
		}else if( nFind < goldAnswers.size()){
			return 1;			
		}else{
			return 2;
		}
	
	}
	
	/** 
	* @Title: ParseAnswerSection 
	* @Description: create AnswerDataSection object from  strings 
	* @param answers
	* @param sparql
	* @param predicate
	* @param similarity
	* @param dataCVT
	* @param dataProperty
	* @return   
	*/
	public static AnswerDataSection ParseAnswerSection(String answers, 
			String entity, String sparql, String relation, String updateSparql, String predicate, String besPredicate, String dataCVT, String dataProperty    
			){

		AnswerDataSection qaResult = new AnswerDataSection();
		parseAnswers(qaResult.Answers, answers);
		qaResult.SPARQL = sparql;
		qaResult.Predicate = predicate;
		qaResult.BesPredicate=besPredicate;
		qaResult.DateInCVTString = dataCVT;
		qaResult.DateInPropertyString = dataProperty;
		parseDataInProperty(qaResult.Date_in_property, dataCVT, dataProperty);
		qaResult.Entity = entity;
		qaResult.Relation = relation;
		qaResult.UpdateSPARQL = updateSparql;

		return qaResult;
		}

	
	/** 
	* @Title: parseDataInProperty 
	* @Description: create Data in Property from  string
	* @param dpItems
	* @param textData
	* @param txtCVT   
	*/
	static void parseDataInProperty(List<CDataInProperty> dpItems,String txtCVT, String txtProperty){
		String text=txtProperty;
		if(text == null || text.length()<3)
			text = txtCVT;

		if(text == null || text.length() < 3)
			return ;
		
		String[]items = text.split(";");
		for(String item : items){
			CDataInProperty aw = CDataInProperty.Parse(item);
			if(aw != null)
				dpItems.add(aw);
		}

	}
	
	/** 
	* @Title: parseAnswers 
	* @Description: create CAnswerCandidates from answer string 
	* @param answers
	* @param answertext   
	*/
	static void parseAnswers(List<CAnswerCandidate> answers, String answertext){
		if(answertext == null || answertext.length() < 3)
			return ;
		
		String[]items = answertext.split(";");
		for(String item : items){
			CAnswerCandidate aw = CAnswerCandidate.Parse(item);
			if(aw != null)
				answers.add(aw);
		}
			
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Answer=");
		buf.append(Answers);
		buf.append("\r\n");
				
		buf.append("SPARQL==");
		buf.append(SPARQL);
		buf.append("\r\n");
		
		buf.append("Predicate==");
		buf.append(Predicate);
		buf.append("\r\n");	
		
		buf.append("Top1_Similarity=");
		buf.append(BesPredicate);
		buf.append("\r\n");	
		
		
		buf.append("Date=");
		buf.append(Date_in_property);
		buf.append("\r\n");		

		
		
		return buf.toString();
	}
}
