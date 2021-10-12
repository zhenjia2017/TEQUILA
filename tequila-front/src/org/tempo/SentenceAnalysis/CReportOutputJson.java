package org.tempo.SentenceAnalysis;

import java.util.List;
import java.util.HashMap; 
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.tempo.QuestionAnswer.CDataInProperty;
import org.tempo.SentenceAnalysis.TimeTool.CDateValue;
import org.tempo.Util.CGlobalConfiguration;
import org.tempo.Util.CMIDInfo;
import org.tempo.Util.CMIDInfoProvider;
import org.tempo.Util.COutputToConsole;
import org.tempo.Util.IOutputString;

public class CReportOutputJson extends CReportOutput{

	public JSONObject  outputJson;
	

	public CReportOutputJson(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	
	}
	
	/** 
	* @Title: Write 
	* @Description: write a key,value to json string
	* @param str   
	*/
	void Write(String key, String value){
		if(value != null)
			this.outputJson.put(key, value);
		else
			this.outputJson.put(key, "");
		
	}
	
	void Write(String key, JSONArray value){
		this.outputJson.put(key, value);
	}
	
	void Write(String key, JSONObject value){
		this.outputJson.put(key, value);
		
	}
	

	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		PrintReport(context);
		WriteLine(this.outputJson.toString());
		System.out.println(this.outputJson.toString());
	}
	
	
	/** 
	* @Title: PrintReport 
	* @Description: output report
	* @param rp   
	*/
	protected void PrintReport(CSentenceAnalysisReport rp){
		if(rp == null)	return ;
		this.outputJson = new JSONObject();
	    int rank = rp.SelectedRank ;   //Get rank
		Write("question", rp.Text);
		Write("question_type" ,getQuestionTypeString(rp));
		Write("rank", String.valueOf(rp.SelectedRank));
		Write("ExplicitTemporalText", rp.getExplicitTemporalText().toString());
		Write("Ordinals", rp.getOridnalText().toString() );
		Write("IsTempoQuestion",String.valueOf(rp.IsTempoQuestion()));
		Write("WhenTagsText",rp.getWhenTagText().toString());
		Write("HasSubQuestion",String.valueOf(rp.HasSubQuestion()));
		
		
		if(rp.SubQuestions != null){
			
			Write("subquestion_1" , rp.AnswerReport.SQ1 );
			Write("subquestion_1_entity" ,  this.getEntityJson(rp.AnswerReport.getDefaultRank(rank).SQ1_Entity) );			
			Write("subquestion_1_sparql" ,  rp.AnswerReport.getDefaultRank(rank).SQ1_SparqlQuery );
			Write("subquestion_1_updated_sparql" ,  rp.AnswerReport.getDefaultRank(rank).SQ1_SparqlUpdate );
			Write("subquestion_1_relation" ,  rp.AnswerReport.getDefaultRank(rank).SQ1_Relation );
			Write("subquestion_1_predicate" , rp.AnswerReport.getDefaultRank(rank).SQ1_Predicate );
			Write("subquestion_1_best_date_predicate", rp.AnswerReport.getDefaultRank(rank).SQ1_BestDatePredicate);
			Write("subquestion_1_answer_and_date",  this.getAnswerDateJson(rp.AnswerReport.getDefaultRank(rank).SQ1_DateInPpropertyCVT_List));
			Write("temporal_relation_signal_word", rp.AnswerReport.SignalWord );
			Write("normalized_temporal_constraint", rp.AnswerReport.TimeConstraint);			
			Write("subquestion_1_answer" ,  this.getAnswerJson(rp.AnswerReport.getDefaultRank(rank).SQ1_Answer ));	

			
			if(rp.SubQuestions.SubQuestion != null){
				Write("subquestion_2", rp.AnswerReport.SQ2);
				Write("subquestion_2_entity", this.getEntityJson(rp.AnswerReport.SQ2_Entity));				
				Write("subquestion_2_sparql", rp.AnswerReport.SQ2_Sparql);
				Write("subquestion_2_updated_sparql", rp.AnswerReport.SQ2_SparqlUpdate);
				Write("subquestion_2_relation", rp.AnswerReport.SQ2_Relation);
				Write("subquestion_2_predicate", rp.AnswerReport.SQ2_Predicate);
				Write("subquestion_2_answer", rp.AnswerReport.SQ2_Answer);				
				
			}else{
				Write("subquestion_2", "NULL"); //because front web site need this key word.
				Write("subquestion_2_entity", "NULL");				
				Write("subquestion_2_sparql", "NULL");
				Write("subquestion_2_updated_sparql", "NULL");
				Write("subquestion_2_relation", "NULL");
				Write("subquestion_2_predicate", "NULL");
				Write("subquestion_2_answer", "NULL");		
			}

		}else{
			Write("subquestion_1" , rp.AnswerReport.SQ1 );
			Write("subquestion_1_entity" ,  this.getEntityJson(rp.AnswerReport.getDefaultRank(rank).SQ1_Entity) );			
			Write("subquestion_1_sparql" ,  rp.AnswerReport.getDefaultRank(rank).SQ1_SparqlQuery );
			Write("subquestion_1_updated_sparql" ,  rp.AnswerReport.getDefaultRank(rank).SQ1_SparqlUpdate );
			Write("subquestion_1_relation" ,  rp.AnswerReport.getDefaultRank(rank).SQ1_Relation );
			Write("subquestion_1_predicate" , rp.AnswerReport.getDefaultRank(rank).SQ1_Predicate );
			
			Write("subquestion_1_best_date_predicate", "NULL");
			Write("subquestion_1_answer_and_date",  "NULL");
			Write("temporal_relation_signal_word", "NULL" );
			Write("normalized_temporal_constraint", "NULL");			
            
			Write("subquestion_1_answer" , this.getAnswerJson(rp.AnswerReport.getDefaultRank(rank).SQ1_Answer) );	
			
			
			Write("subquestion_2", "NULL"); //because front web site need this key word.
			Write("subquestion_2_entity", "NULL");				
			Write("subquestion_2_sparql", "NULL");
			Write("subquestion_2_updated_sparql", "NULL");
			Write("subquestion_2_relation", "NULL");
			Write("subquestion_2_predicate", "NULL");
			Write("subquestion_2_answer", "NULL");		
		}
		
		Write("gold_answer" , rp.AnswerReport.GodenAnswer );
		Write("tempo_answer", this.getTempoAnswerJson(rp));
		Write("reasoning_rules" , rp.AnswerReport.Reason );
	}
	
	private JSONArray getAnswerDateJson(List<CDataInProperty> sq1_DateInPpropertyCVT_List) {
		// TODO Auto-generated method stub
		JSONArray buf = new JSONArray();
		
		Map<String,String> DateMap=new HashMap<String,String>();  
		if(sq1_DateInPpropertyCVT_List.size() > 0){
			for(CDataInProperty item: sq1_DateInPpropertyCVT_List){
				
				JSONObject itemjson = new JSONObject();
				itemjson.put("mid", item.MID);
				itemjson.put("name", item.Name);
				itemjson.put("date", item.Date);
				itemjson.put("best_date_predicate", item.TagName);
				buf.put(itemjson);
			}
		}
		
		return buf;
	}
			
	
	/** 
	* @Title: getTempoAnswer 
	* @Description: output tempo answers
	* @param rp
	* @return   
	*/
	protected JSONArray getTempoAnswerJson(CSentenceAnalysisReport rp){
		JSONArray buf = new JSONArray();
		if(rp.AnswerReport.Answer != null && rp.AnswerReport.Answer.size() > 0){
			for(String answer: rp.AnswerReport.Answer){
				String[] entity = getEntity(answer);
				if( entity != null && entity.length>0){
					JSONObject itemjson = getMIDInfoJson(entity[0], entity[1], 4);
					buf.put(itemjson);
				}
			}
		}
		
		return buf;
	}
	
	protected JSONArray getEntityJson(String entityString){
		JSONArray buf = new JSONArray();
		String[] items = entityString.split(" ");
		
		if(items != null && items.length > 0){
			for(String mid: items){
				JSONObject itemjson = getMIDInfoJson(mid, "", 2);
				buf.put(itemjson);
			}
		}
		
		return buf;
	}

	
	protected JSONArray getAnswerJson(String answerString){
		JSONArray buf = new JSONArray();
		String[] items =answerString.split("\\);");
		
		if(items != null && items.length > 0){
			for(String answer: items){
				String[] entity = getEntity(answer);
				if( entity != null && entity.length > 0){
					JSONObject itemjson = getMIDInfoJson(entity[0], entity[1], 3);
					buf.put(itemjson);
				}
			}
		}
		
		return buf;
	}
	
	private String[] getEntity(String answer) {
		// TODO Auto-generated method stub
		int pos1 = answer.lastIndexOf("(");
		int pos2 = answer.lastIndexOf(")");
		
		if( pos1 < 0)
			return null;
		
		if( pos2 < pos1)
			pos2 = answer.length();
		
		String[] item = new String[2];
		item[0] = answer.substring(pos1+1, pos2).trim();
		item[1] = answer.substring(0,pos1).trim();
		
		return item;
	}


	private JSONObject getMIDInfoJson(String mid, String name, int keyNumber) {
		CMIDInfo item = CMIDInfoProvider.getInstance().findMID(mid);
		JSONObject itemjson = new JSONObject();
		if(item != null){
			itemjson.put("mid", mid);
			itemjson.put("name", item.Name);
			
			if( keyNumber >=3)
				itemjson.put("wiki", item.HttpLink);
			if( keyNumber >=4){
				itemjson.put("description", CGlobalConfiguration.TestMode ? "detail" :item.Info);
			}
		}else{
			itemjson.put("mid", mid);
			itemjson.put("name", name);
			if( keyNumber >=3)
				itemjson.put("wiki", "");
			if( keyNumber >=4)
				itemjson.put("description", "");					
		}
		return itemjson;
	}
}
