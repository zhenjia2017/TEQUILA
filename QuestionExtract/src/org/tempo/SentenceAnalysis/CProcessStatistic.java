package org.tempo.SentenceAnalysis;

import java.util.ArrayList;
import java.util.List;

import org.tempo.SentenceAnalysis.CNEREntityTag.EnumNERObjectType;
import org.tempo.SentenceAnalysis.TimeTool.CTempralTag;

/** 
* @ClassName: CProcessStatistic 
* @Description: this process node is to statistic .
*  
*/
public class CProcessStatistic extends CNodeProcessorBase<CSentenceAnalysisReport>{

	CStatisticReport statisticCount;
	public CStatisticReport getStatisticReport(){
		return statisticCount;
	}
	
	//create CProcessSTatistic as single object
	static CProcessStatistic _instance;
	public static CProcessStatistic getStatistic(){
		if(_instance == null)
			_instance = new CProcessStatistic();
		
		return _instance;
	}
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!= null);
	}


	protected CProcessStatistic(){
		super(null);
		this.statisticCount = new CStatisticReport();
	}
	
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		CountReport(context);

	}	
	
	void CountReport(CSentenceAnalysisReport rp){
		if(rp == null)	return ;
		this.statisticCount.SentenceTotal++;
		
//		countQuestion(rp);
		
		countQuestionType(rp);
		countNEREntity(rp.NEREntities);
	    countTempralTag(rp.TimeReports);	
	    countDate(rp.Dates);
	    countOrdinal(rp.Ordinals);
	    countEvent(rp.Events);
		countAnswerFromoRank(rp.AnswerReport.AnswerFromRank);
	}
	
	void countAnswerFromoRank(int rank){
		if(rank > 0)
			this.statisticCount.AnswerFromRankCount[rank-1]++;
	}
	
	
	void countQuestion(CSentenceAnalysisReport rp){

			
	}
	
	/** 
	* @Title: countQuestionType 
	* @Description: count question type
	* @param rp   
	*/
	void countQuestionType(CSentenceAnalysisReport rp){
		boolean hasTag = false;
		if(rp.Dates.size() > 0){
			this.statisticCount.AddTypeMapCount("DATE", statisticCount.SentenceTypeCount);
			hasTag = true;
		}

		if(rp.TimeReports.size()>0){
			this.statisticCount.AddTypeMapCount("TIMEX3", statisticCount.SentenceTypeCount);
			hasTag = true;
		}
		
		if(rp.Ordinals.size()>0){
			this.statisticCount.AddTypeMapCount("ORDINAL", statisticCount.SentenceTypeCount);
			hasTag = true;
		}
		
		if(rp.Events.size()>0){
			this.statisticCount.AddTypeMapCount("EVENT", statisticCount.SentenceTypeCount);	
			hasTag = true;
		}
	
		if(rp.WhenTags.size()>0){
			this.statisticCount.AddTypeMapCount("WHEN", statisticCount.SentenceTypeCount);	
			hasTag = true;
		}

		
		if(rp.SubQuestions != null ){
			this.statisticCount.SentenceRewriteCount++;
			this.statisticCount.AddTypeMapCount(rp.SubQuestions.QuestionType.toString(), statisticCount.SingalTypeCount);
			
			if(rp.SubQuestions.SignalWord != null && rp.SubQuestions.SignalWord.length()>0){
				this.statisticCount.AddTypeMapCount("SIGNAL", statisticCount.SentenceTypeCount);				
				this.statisticCount.AddTypeMapCount(rp.SubQuestions.SignalWord, statisticCount.SingalWordCount);
			}
			
			hasTag = true;
		}
		
		if(hasTag == false){
			this.statisticCount.NoneTypeSentenceCount++;
		}
	
	}
	

	 /** 
	* @Title: countTempralTag 
	* @Description: count tempral tags
	* @param tags   
	*/
	void countTempralTag(List<CTempralTag> tags){
		if(tags.size()<1)
			return;
	
		this.statisticCount.TimeX3SentenceCount++;
		this.statisticCount.TimeX3Count += tags.size();

	}
	
	
	/** 
	* @Title: countNEREntity 
	* @Description: count NER entity tags
	* @param tags   
	*/
	void countNEREntity(List<CNEREntityTag> sourNerTags){
		
		List<CNEREntityTag> tags = this.getValildNEREntity(sourNerTags);
		
		if(tags.size() < 1){
			return;
		}
		
		this.statisticCount.EntitySentenceCount++;
		this.statisticCount.EntityCount += tags.size();
		if(tags.size() == 1)
			this.statisticCount.OneEnitySentenceCount++;
		else if(tags.size() == 2)
			this.statisticCount.TwoEntitySentenceCount++;
		else
			this.statisticCount.MoreEntitySentenceCount++;
			
		for(CNEREntityTag tag:tags){
			this.statisticCount.AddTypeMapCount(tag.NERObjectType.toString(), statisticCount.NEREntityCount);
		}
	}
	
	/** 
	* @Title: getValildEntity 
	* @Description: filter valid ner entity
	* @param tags
	* @return   
	*/
	List<CNEREntityTag> getValildNEREntity(List<CNEREntityTag> tags){
//			NER Entity Type "Money":
//			NER Entity Type "Organization":
//			NER Entity Type "MISC":
//			NER Entity Type "Person":
//			NER Entity Type "Location":
		
		List<CNEREntityTag> results = new ArrayList<CNEREntityTag>();
		for(CNEREntityTag en: tags){
			if(en.NERObjectType == EnumNERObjectType.Money ||
					en.NERObjectType == EnumNERObjectType.Organization ||
					en.NERObjectType == EnumNERObjectType.MISC ||
					en.NERObjectType == EnumNERObjectType.Person ||
					en.NERObjectType == EnumNERObjectType.Location ){
				
				results.add(en);
				
			}
					
		}
		
		return results;
	}
	
	
	/** 
	* @Title: countEvent 
	* @Description: count event tags
	* @param tags   
	*/
	void countEvent(List<? extends ILabelTag> tags){
		if(tags.size() < 1){
			return;
		}
		
		this.statisticCount.EventSentenceCount++;
		this.statisticCount.EventCount += tags.size();
	}

	/** 
	* @Title: countDate 
	* @Description: count Date tags
	* @param tags   
	*/
	void countDate(List<? extends ILabelTag> tags){
		if(tags.size() < 1){
			return;
		}
		
		this.statisticCount.DateTagSentenceCount++;
		this.statisticCount.DateTagCount += tags.size();
	}
	
	/** 
	* @Title: countOrdinal 
	* @Description: count ordinal tags
	* @param tags   
	*/
	void countOrdinal(List<? extends ILabelTag> tags){
		if(tags.size() < 1){
			return;
		}
		
		this.statisticCount.OrdinalSentenceCount++;
		this.statisticCount.OrdinalTagCount += tags.size();
	}	
	
}
