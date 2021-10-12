package org.tempo.testsample;


import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;
import org.tempo.DataFileReaders.CFileVisitorBase;
import org.tempo.DataFileReaders.DataFileLineInfo;
import org.tempo.DataFileReaders.TextDataFileReader;
import org.tempo.DataFileReaders.TextDataFileWriter;
import org.tempo.QuestionAnswer.CAnswerFilterContext;
import org.tempo.QuestionAnswer.CAnswerQuestionProcess;
import org.tempo.QuestionAnswer.CPairAnswerCandidate;
import org.tempo.QuestionAnswer.CPairCandidatesFilter;
import org.tempo.QuestionAnswer.CQAResult;
import org.tempo.QuestionAnswer.CQAServiceOnline;
import org.tempo.QuestionAnswer.IQAService;
import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.CProcessFixInvalidTags;
import org.tempo.SentenceAnalysis.CProcessMarkDateOrdinalByRule;
import org.tempo.SentenceAnalysis.CProcessMarkEvent;
import org.tempo.SentenceAnalysis.CProcessMarkNEREntity;
import org.tempo.SentenceAnalysis.CProcessMarkTimeTagByHeildeTime;
import org.tempo.SentenceAnalysis.CProcessMarkWhenTag;
import org.tempo.SentenceAnalysis.CProcessNLP;
import org.tempo.SentenceAnalysis.CProcessStatistic;
import org.tempo.SentenceAnalysis.CReportOutput;
import org.tempo.SentenceAnalysis.CReportOutputJson;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.NLPTool;
import org.tempo.SentenceAnalysis.Decompose.CProcessDecomposeSentence;
import org.tempo.SentenceAnalysis.Decompose.CProcessRewriteBySimple4Type_Complex;
import org.tempo.SentenceAnalysis.Decompose.CProcessRewriteBySimple4Type_Simple;
import org.tempo.SentenceAnalysis.Decompose.CSentenceRewriteBase;
import org.tempo.SentenceAnalysis.Dictionary.CTagEntityFromDictionary;
import org.tempo.SentenceAnalysis.Dictionary.DictionaryBuilder;
import org.tempo.Util.AddNoDateAnswersMode;
import org.tempo.Util.CGlobalConfiguration;
import org.tempo.Util.CMIDInfoProvider;
import org.tempo.Util.COutputToConsole;
import org.tempo.Util.COutputToFile;
import org.tempo.Util.IOutputString;
import org.tempo.Util.OnlyLatestOneMode;
import org.tempo.Util.QaMode;
import org.tempo.Util.RankMode;
import org.tempo.Util.RemoveDateMode;
import org.tempo.Util.AddNoDateAnswersMode;

import org.json.JSONArray;
import org.json.JSONObject;


public class QuestionAnswer {
	/** 
	* @Title: QuestionAnswer 
	* @Description: answer input questions
	* @return: json object   
	*/
	
	public static void main(String[] args) {
		CGlobalConfiguration.TestMode = false;
		JSONObject Result;
		
		//initialize
		NLPTool.InitStandfordNLP();
		CMIDInfoProvider.LoadDictionaryFile(CGlobalConfiguration.MidNameDescription);  
        CGlobalConfiguration.entitySearch = CTagEntityFromDictionary.CreateTagTool(CGlobalConfiguration.DictionaryFilePath);

		//example question
		String[] questions = {"Who won best sound editing in 1966?",
				"Who is the President of the European Union this year?",
				"Who is the husband of Julia Roberts today?"};
		/*Question examples
		 * 
		 who was governor of Minnesota when Maathaad Maathaadu Mallige was released?
         What does Dustin diamond do now?
         Who is the President of the European Union 2011?
         What Soviet leader came to power in the 1920s?
         When was the Green Party founded?
		 * 
		 */

		PrintStream err = System.err;
		
		for (String question: questions) {
			
		Result = QuestionAnswer(question,RemoveDateMode.Remove,AddNoDateAnswersMode.NotAdd,OnlyLatestOneMode.NotLatest,RankMode.Rank1,QaMode.AQQU);
		
		//Reading the String
        Set<String> keys = Result.keySet();
        
        System.out.println(keys);
        
        for(String str: keys){
        	System.out.print(str + ": ");
        	System.out.println(Result.get(str));
		}
		}
        
   
        
		
	}
	
			
	static JSONObject QuestionAnswer(String question, RemoveDateMode remoDateMode, AddNoDateAnswersMode addDateMode, OnlyLatestOneMode onlyMode, RankMode rankMode, QaMode qaMode)  {
			
			String QAService_Selection = qaMode.getMode();
			boolean RemoveDateTag = remoDateMode.getMode();
			boolean AlwaysAddNoDateAnswers = addDateMode.getMode();
			boolean OnlyLatestOneByTimeFilter = onlyMode.getMode();
			int SelectedRank = rankMode.getMode();
				
			// now make all writes to the System.err stream silent 
			System.setErr(new PrintStream(new OutputStream() {
			    public void write(int b) {
			    }
			}));
			
			INodeProcessor<CSentenceAnalysisReport> proProcess = RewritePrinter.CreateProProcessHandle();
			
			CReportOutputJson output = null;

			output = new CReportOutputJson(CProcessStatistic.getStatistic() );
			
			
			CAnswerQuestionProcess questionProcess = new CAnswerQuestionProcess(
					QAService_Selection,RemoveDateTag, AlwaysAddNoDateAnswers, OnlyLatestOneByTimeFilter, SelectedRank,output);
			
			
			questionProcess.LoadPairMapFile(CGlobalConfiguration.PairNameFilePath);
			INodeProcessor<CSentenceAnalysisReport> qaProcess = RewritePrinter.CreateSentenceProcessHandle( questionProcess );

			output.WriteLine("-------------------------------------------------------------");
			output.WriteLine(question);			
			
			CSentenceAnalysisReport rp = new CSentenceAnalysisReport(question,RemoveDateTag, AlwaysAddNoDateAnswers, OnlyLatestOneByTimeFilter, SelectedRank);

			proProcess.Process(rp);
			System.out.println(rp.Text);
			qaProcess.Process(rp);
			
			output.WriteLine("-------------------- statistic  report -------------------------");
			output.WriteLine(CProcessStatistic.getStatistic().getStatisticReport().toString());
			
			return output.outputJson;
		}


 

}


