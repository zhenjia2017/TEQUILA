package org.tempo.QuestionAnswer;

import java.util.List;



/** 
* @ClassName: CAnswerSelectPolicyBase 
* @Description:  this class is a base class for policy to deal with question
*  
*/
class CAnswerSelectPolicyBase{
	public CQAAnswerFilter  QAService; //qa service
	public CAnswerFilterContext AnswerContext; //context
	
	public CAnswerSelectPolicyBase(CQAAnswerFilter service){
		QAService = service;
	}
	
	/** 
	* @Title: QueryAnswer 
	* @Description: deal with question
	* @param answerContext   
	*/
	public void QueryAnswer(CAnswerFilterContext answerContext){		
			BeforeProcessAnswer(answerContext); // before process
			FilterData(answerContext); // filter result
			AfterProcessAnswer(answerContext); // after process
		
	}	
	
	/** 
	* @Title: BeforeProcessAnswer 
	* @Description: prepare before process.
	* @param answerContext   
	*/
	protected void BeforeProcessAnswer(CAnswerFilterContext answerContext){
		this.AnswerContext = answerContext;
		this.QAService.Process(answerContext);	
		if (answerContext.QuestionAnwser != null) {
			answerContext.QuestionAnwser.set_currentDateRankSection(1);
		}
	}
	
	/** 
	* @Title: AfterProcessAnswer 
	* @Description: do somthing after proess
	* @param answerContext   
	*/
	protected void AfterProcessAnswer(CAnswerFilterContext answerContext){

	}	
	
	/** 
	* @Title: FilterData 
	* @Description: deal with the context
	* @param answerContext   
	*/
	protected void FilterData(CAnswerFilterContext answerContext){
		
	}
	

	
}


/** 
* @ClassName: CAnswerSelectPolicyForNotTempo 
* @Description: Policy for non-tempo question
*/
class CAnswerSelectPolicyForNotTempo extends CAnswerSelectPolicyBase{
	public CAnswerSelectPolicyForNotTempo(CQAAnswerFilter service){
		super(service);
	}
	protected void FilterData(CAnswerFilterContext answerContext){
		
	}
	
	/**   
	 * <p>Title: AfterProcessAnswer</p>   
	 * <p>Description: add QA answer to final answer directly </p>   
	 * @param answerContext   
	 * @see org.tempo.QuestionAnswer.CAnswerSelectPolicyBase#AfterProcessAnswer(org.tempo.QuestionAnswer.CAnswerFilterContext)   
	 */ 
	protected void AfterProcessAnswer(CAnswerFilterContext answerContext){
		if (answerContext.QuestionAnwser != null) {
			for (CAnswerCandidate dt : answerContext.QuestionAnwser.getDefaultAnswers())
				answerContext.AddFinalAnswer(dt.getAnswerString());
		}
	}
	
}

/** 
* @ClassName: CAnswerSelectForTempoPolicy 
* @Description:  Policy for non-tempo question
*/
class CAnswerSelectForTempoPolicy extends  CAnswerSelectPolicyBase {
	CPairCandidatesFilter candidateFilter; //candidates filter handler

	public CAnswerSelectForTempoPolicy(CQAAnswerFilter service){
		super(service);
		
		this.candidateFilter = new CPairCandidatesFilter(
				new CTimeX3AnswerFilter(new COrdinalAnswerFilter(	null )));
		
	}
	
	/** 
	* @Title: LoadPairDictionFile 
	* @Description: load name pair dictionary
	* @param file   
	*/
	public void LoadPairDictionFile(String file){
		System.out.println("load pair-wise file");
		if(this.candidateFilter != null){
			this.candidateFilter.LoadPairDictionFile(file);
		}
	}
	
	/**   
	 * <p>Title: FilterData</p>   
	 * <p>Description: </p>   
	 * @param answerContext   
	 * @see org.tempo.QuestionAnswer.CAnswerSelectPolicyBase#FilterData(org.tempo.QuestionAnswer.CAnswerFilterContext)   
	 */ 
	protected void FilterData(CAnswerFilterContext answerContext){

		//deal with answer in ranks
//		for(int i = 1; i<(CGlobalConfiguration.FilterAnswerLevels+1); i++){
//			if(answerContext.QuestionAnwser.getDateRankSection(i ) != null){
//				answerContext.QuestionAnwser.set_currentDateRankSection(i );//get current rank
//				this.candidateFilter.Process(answerContext); //process answer in rank
//				
//				AfterProcessRankData(answerContext);
//				
//				//if has final answer, set rank mark
//				if(HasFinalAnswer(answerContext)){ 
//					answerContext.SentenceReport.AnswerReport.AnswerFromRank = i;
//					break;
//				}
//			}
//			
//		}
		if(answerContext.QuestionAnwser != null && answerContext.QuestionAnwser.getDateRankSection(answerContext.GetRank() ) != null){
			answerContext.QuestionAnwser.set_currentDateRankSection(answerContext.GetRank());//get current rank
			this.candidateFilter.Process(answerContext); //process answer in rank
			
			AfterProcessRankData(answerContext);
			//if has final answer, set rank mark
			if(HasFinalAnswer(answerContext)){ 
				
				answerContext.SentenceReport.AnswerReport.AnswerFromRank = answerContext.GetRank();
			}
		}
			
		
		
	}	
	
	/** 
	* @Title: HasFinalAnswer 
	* @Description: check final answer 
	* @param answerContext
	* @return   
	*/
	protected boolean HasFinalAnswer(CAnswerFilterContext answerContext){
		return (answerContext.FinalAnswer.size()>0);
	}

	/** 
	* @Title: AfterProcessRankData 
	* @Description: do something after deal with answers in a rank
	* @param answerContext   
	*/
	protected void AfterProcessRankData(CAnswerFilterContext answerContext){
		
		// policy 1;
		//if has filter result
		System.out.println(answerContext.HasFilterResult());
		if(answerContext.HasFilterResult()){
			//add filter candidates to final answers.
			
			for(CPairAnswerCandidate dt: answerContext.QuestionAnwser.FilterCandidates) {
				answerContext.AddFinalAnswer(dt.GetAnswer());
			    }
			
		}else if( answerContext.getAnswersCount() == 1){// if only 1 answer ,add it to final answer without filter
//				System.out.println(answerContext.QuestionAnwser.GetDefaultQAAnswer(0));
				answerContext.AddFinalAnswer(answerContext.QuestionAnwser.GetDefaultQAAnswer(0));
		} 
		
		//deal with no date answers , and if no result after filter.
		if(answerContext.GetAlwaysAddNoDateAnswers() || answerContext.HasFinalAnswer()==false){			
			AnswerDataSection rank = answerContext.QuestionAnwser.get_currentDateRankSection();
			if(rank != null){
				List<CAnswerCandidate> noDateAnswers = rank.getAnswersWithoutDateInfo();	
				System.out.println("Add answer " + noDateAnswers.size() + " candidate without date." );
		
				for(CAnswerCandidate aw: noDateAnswers ){
					answerContext.AddFinalAnswer(aw.getAnswerString());
				}
			
			}
		}
				
		
		//policy 2
//		if(answerContext.HasFilterResult()){
//			for(CPairAnswerCandidate dt: answerContext.QuestionAnwser.FilterCandidates)
//				answerContext.AddFinalAnswer(dt.GetAnswer());
//		} 
		
			
	}
	
	/**   
	 * <p>Title: AfterProcessAnswer</p>   
	 * <p>Description: do someting after the process</p>   
	 * @param answerContext   
	 * @see org.tempo.QuestionAnswer.CAnswerSelectPolicyBase#AfterProcessAnswer(org.tempo.QuestionAnswer.CAnswerFilterContext)   
	 */ 
	protected void AfterProcessAnswer(CAnswerFilterContext answerContext){
		
		
		
		//policy 2:
//		
//		if(CGlobalConfiguration.AlwaysAddNoDateAnswers || answerContext.HasFinalAnswer()==false){			
//			for(int i = 1; i<4; i++){
//				AnswerDataSection rank = answerContext.QuestionAnwser.getDateRankSection(i);
//				if(rank != null){
//					List<CAnswerCandidate> noDateAnswers = rank.getAnswersWithoutDateInfo();	
//					if(noDateAnswers.size()>0){
//					
//						System.out.println("Add answer " + noDateAnswers.size() + " candidate without date." );
//			
//						for(CAnswerCandidate aw: noDateAnswers ){
//							answerContext.FinalAnswer.add(aw.Answer);
//						}
//						
//						return;
//					}
//				}	
//			}
//		}

				
	}
	
}
