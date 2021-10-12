package org.tempo.QuestionAnswer;

import java.util.HashMap;

import org.tempo.SentenceAnalysis.INodeProcessor;

/** 
* @ClassName: CPairCandidatesFilter 
* @Description: filter to create pair candidates  
*/
public class CPairCandidatesFilter extends CAnswerFilterBase{
	CTimePairNameMap pairMap;
	
	public CTimePairNameMap getPairMap() {
		return pairMap;
	}

	public void setPairMap(CTimePairNameMap pairMap) {
		this.pairMap = pairMap;
	}

	public CPairCandidatesFilter(INodeProcessor<CAnswerFilterContext> next){
		super(next);
		
	}
	
	/** 
	* @Title: LoadPairDictionFile 
	* @Description: Load name pair dictionary
	* @param file
	* @return   
	*/
	public boolean LoadPairDictionFile(String file){
		System.out.println("Load name pair dictionary");
		this.pairMap = CTimePairNameMap.CreateFromPairFile(file);
		
		return (this.pairMap != null);
	}

	boolean IsMatchTypeQuestion(CAnswerFilterContext context){
		return (context.getAnswersCount()>0 && context.HasDateInfo());
	}	
	
	
	/**   
	 * <p>Title: NodeProcess</p>   
	 * <p>Description: process template </p>   
	 * @param context   
	 * @see org.tempo.QuestionAnswer.CAnswerFilterBase#NodeProcess(org.tempo.QuestionAnswer.CAnswerFilterContext)   
	 */ 
	@Override
	protected void NodeProcess(CAnswerFilterContext context) {
		if(context.QuestionAnwser.get_currentDateRankSection().getDate_in_property().size()> 0){
			context.QuestionAnwser.PairCandidates.clear();
			PairAnswer(context.QuestionAnwser);
			PrepareFilterCandidates();
		}
		
	}
	
	
	
	/** 
	* @Title: PairAnswer 
	* @Description: pair answer candidates
	* @param answer   
	*/
	void PairAnswer(CQAResult answer){
		HashMap<String, Integer> pairKey = new HashMap<String,Integer>();
		CDataInProperty second=null;
		int compareResult = 0;
		//if two answer cadidates has same MID, they are pair answer 
		for(int i = 0; i <  answer.get_currentDateRankSection().getDate_in_property().size()-1; i++){
			 
			CDataInProperty first =  answer.get_currentDateRankSection().getDate_in_property().get(i);
			if( pairKey.containsKey(getItemKey(first))) 
				 continue;	
			 
			 second = null;
			 compareResult = -2;
			 
			 for(int j = i + 1; j < answer.get_currentDateRankSection().getDate_in_property().size() ; j++){
				 CDataInProperty nextCandidate =  answer.get_currentDateRankSection().getDate_in_property().get(j);
				 if(pairKey.containsKey(getItemKey(nextCandidate)))
						 continue;
				 //check if the name is in pair table
				 if ((first.MID.compareTo(nextCandidate.MID) == 0)) {
					int ncmp = this.pairMap.IsPairName(first.TagName, nextCandidate.TagName);
					
					if (ncmp != 0) {
						second = nextCandidate;
						compareResult = ncmp;
						break;
					}
				}
				 
			 }
			 //create pairt candidates
			 if (compareResult == 0 || compareResult > 0) {
				answer.PairCandidates.add(new CPairAnswerCandidate(first, second));
			} else if(compareResult == -1) {
				answer.PairCandidates.add(new CPairAnswerCandidate(second, first));
			} else{ //not found pair item
				int nPair = this.pairMap.IsPairName(first.TagName); 				
				if(nPair>0)
					answer.PairCandidates.add(new CPairAnswerCandidate(first,null));
				else if(nPair < 0)
					answer.PairCandidates.add(new CPairAnswerCandidate(null, first));
				else
					answer.PairCandidates.add(new CPairAnswerCandidate(first));
			}

			pairKey.put(getItemKey(first), 1);
			if (second != null) {
				pairKey.put(getItemKey(second), 1);
			}
		}
		
		//deal with the last answer
		CDataInProperty last =  answer.get_currentDateRankSection().getDate_in_property().get(answer.get_currentDateRankSection().getDate_in_property().size() -1);
		if(pairKey.containsKey(getItemKey(last)) == false ){
			int nPair = this.pairMap.IsPairName(last.TagName); 				
			if(nPair>0)
				answer.PairCandidates.add(new CPairAnswerCandidate(last,null));
			else if(nPair < 0)
				answer.PairCandidates.add(new CPairAnswerCandidate(null, last));
			else
				answer.PairCandidates.add(new CPairAnswerCandidate(last));			
		}
		
	}
	
	/** 
	* @Title: getItemKey 
	* @Description: get name id;
	* @param dateItem
	* @return   
	*/
	String getItemKey(CDataInProperty dateItem){
		return (dateItem.MID + "$$" + dateItem.TagName + (dateItem.Date != null ? dateItem.Date.toString():""));
	}

}
