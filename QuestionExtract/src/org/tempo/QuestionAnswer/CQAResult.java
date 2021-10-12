package org.tempo.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;


/** 
* @ClassName: CQAResult 
* @Description: the class is to manage QA answers
*  
*/
public class CQAResult {

	public String  Question; //source question
	public AnswerDataSection[] AnswerRanks; //answer ranks
	public List<CPairAnswerCandidate>  PairCandidates; // pair candidates.
	public List<CPairAnswerCandidate>  FilterCandidates; //filtered candidates
	AnswerDataSection  _currentDateRankSection; // answer rank section
	
	protected CQAResult(){
		this.Question="";
		this.PairCandidates = new ArrayList<CPairAnswerCandidate>();
		this.FilterCandidates = new ArrayList<CPairAnswerCandidate>(); 
		this.AnswerRanks = new AnswerDataSection[3];
	}
	

	/** 
	* @Title: HasFilteredCandidate 
	* @Description: check if has filtered result
	* @return   
	*/
	public boolean HasFilteredCandidate(){
		return (this.FilterCandidates.size()>0);
	}
	
	

	
	/** 
	* @Title: get_currentDateRankSection 
	* @Description: get current rank section
	* @return   
	*/
	public AnswerDataSection get_currentDateRankSection() {
		return _currentDateRankSection;
	}
	
	/** 
	* @Title: getCurrentOrDefaultDateRankSection 
	* @Description: get current or default rank section
	* @return   
	*/
	AnswerDataSection getCurrentOrDefaultDateRankSection() {
		return (_currentDateRankSection!=null? _currentDateRankSection: this.AnswerRanks[0]);
	}

	/** 
	* @Title: getDateRankSection 
	* @Description: get a rank section by index
	* @param order
	* @return   
	*/
	public AnswerDataSection getDateRankSection(int order) {
		if( order <=1 )
			return this.AnswerRanks[0];
		else if( order <=2)
			return this.AnswerRanks[1];
		else
			return this.AnswerRanks[2];
	}
	
	
	/** 
	* @Title: set_currentDateRankSection 
	* @Description: set current rank index
	* @param order   
	*/
	public void set_currentDateRankSection(int order) {
		if( order <=1 )
			this._currentDateRankSection = this.AnswerRanks[0];
		else if( order <=2)
			this._currentDateRankSection =  this.AnswerRanks[1];
		else
			this._currentDateRankSection =  this.AnswerRanks[2];
	}


	/** 
	* @Title: GetDefaultQAAnswer 
	* @Description: get QA answer by order
	* @param nOrder
	* @return   
	*/
	public String GetDefaultQAAnswer(int nOrder){
		return this.getCurrentOrDefaultDateRankSection().GetQAAnswer(nOrder);
	}
	
	/** 
	* @Title: getDefaultAnswersWithoutDateInfo 
	* @Description: TODO
	* @return   
	*/
	public List<CAnswerCandidate> getDefaultAnswersWithoutDateInfo(){
		return this.getCurrentOrDefaultDateRankSection().getAnswersWithoutDateInfo();
	}
	
	/** 
	* @Title: getDefaultAnswers 
	* @Description:  get answers in current rank 
	* @return   
	*/
	public List<CAnswerCandidate> getDefaultAnswers(){
		return this.getCurrentOrDefaultDateRankSection().getAnswers();
	}
	
	/** 
	* @Title: getAnswerSize 
	* @Description:  get answers count in current rank 
	* @return   
	*/
	public int getAnswerSize(){
		return this.getCurrentOrDefaultDateRankSection().getAnswers().size();
	}
	
	
	/** 
	* @Title: getDate_in_propertySize 
	* @Description: get data in property in current rank 
	* @return   
	*/
	public int getDate_in_propertySize(){
		return this.getCurrentOrDefaultDateRankSection().getDate_in_property().size();
	}
	
	/** 
	* @Title: HasQASystemAnswer 
	* @Description: check if has QA answer .(not tempo answer)
	* @return   
	*/
	public boolean HasQASystemAnswer(){
		for(AnswerDataSection sec: this.AnswerRanks){
			if(sec != null && sec.HasQASystemAnswer())
				return true;
		}
		return false;
	
	}
	
	/** 
	* @Title: HasDateCandidate 
	* @Description: check if has Date Candidates
	* @return   
	*/
	public boolean HasDateCandidate(){
		for(AnswerDataSection sec: this.AnswerRanks){
			if(sec != null && sec.HasDateCandidate())
				return true;
		}
		return false;
	}
	
	public String getPredicate(){
		return this.getCurrentOrDefaultDateRankSection().getPredicate();
	}
	
	public String getRealtion(){
		return this.getCurrentOrDefaultDateRankSection().getRelation();
	}
	
	
	public String getSparql(){
		return this.getCurrentOrDefaultDateRankSection().SPARQL;
	}
	
	public String getSparqlUpdate(){
		return this.getCurrentOrDefaultDateRankSection().getUpdateSPARQL();
	}
	
	public String getEntity(){
		return this.getCurrentOrDefaultDateRankSection().getEntity();
	}

	/** 
	* @Title: Parse 
	* @Description: parse answer text to CQAResult object
	* @param answerTexts
	* @return   
	*/
	
//	public static AnswerDataSection ParseAnswerSection(String answers, 
//			String sparql, String predicate,
//			String similarity, String data, String dateCVT, 
//			String dataProperty, String entity, String relation, String updateSparql){


	public static CQAResult Parse(String[] answerTexts){
		
		CQAResult qaResult = new CQAResult();
		qaResult.Question = answerTexts[0];
		
//		public static AnswerDataSection ParseAnswerSection(String answers, 
//				String entity, String sparql,  String relation, 
//				String updateSparql, String predicate,
//				String besPredicate, String dataCVT, 
//				String dataProperty){
		
		if(answerTexts.length<8){
			qaResult.AnswerRanks[0] = AnswerDataSection.ParseAnswerSection(answerTexts[1], answerTexts[2],
											answerTexts[3],answerTexts[4],answerTexts[5],answerTexts[6],
											"", "","");	
			
		}else{
			qaResult.AnswerRanks[0] = AnswerDataSection.ParseAnswerSection(answerTexts[1], answerTexts[2],
											answerTexts[3],answerTexts[4],answerTexts[5],answerTexts[6],
											answerTexts[7],	answerTexts[8],answerTexts[9]);		

		
			qaResult.AnswerRanks[1] = AnswerDataSection.ParseAnswerSection(answerTexts[10],answerTexts[11],
										answerTexts[12],answerTexts[13],answerTexts[14],answerTexts[15],
										answerTexts[16],answerTexts[17],answerTexts[18]);		
			
			qaResult.AnswerRanks[2]= AnswerDataSection.ParseAnswerSection(answerTexts[19],answerTexts[20],
								answerTexts[21], answerTexts[22],answerTexts[23],answerTexts[24],
								answerTexts[25], answerTexts[26],answerTexts[27]);
		}
		return qaResult;
	}
	
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("Question=");
		buf.append(Question);
		buf.append("\r\n");
		return buf.toString();
	}


}




