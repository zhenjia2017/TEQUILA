package org.tempo.testsample;

import java.io.OutputStream;
import java.io.PrintStream;

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
import org.tempo.SentenceAnalysis.CProcessStatistic;
import org.tempo.SentenceAnalysis.CReportOutput;
import org.tempo.SentenceAnalysis.CReportOutputJson;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.NLPTool;
import org.tempo.Util.CGlobalConfiguration;
import org.tempo.Util.CMIDInfoProvider;
import org.tempo.Util.COutputToConsole;
import org.tempo.Util.COutputToFile;
import org.tempo.Util.IOutputString;


public class TS_QuestionAnswer {

	public static void main(String[] args) {
		CGlobalConfiguration.TestMode = false;
		
		TestQuestionFromFile(false);
        //testQuestionAnswer(false);
	
	}
	
	
	   static void TestQuestionFromFile(boolean bJsonOutput){
			
			PrintStream err = System.err;  

			// now make all writes to the System.err stream silent 
			System.setErr(new PrintStream(new OutputStream() {
			    public void write(int b) {
			    }
			}));
			
			
			NLPTool.InitStandfordNLP();
			
			String[] questionFiles = new String []{
				CGlobalConfiguration.GloabalSourceFolder +"\\TempQuestions_truecase.txt",
			};
			
			String[] answerFiles = new String []{
				"c:\\temp\\TempQuestions_truecase_answer.txt"
			};
			
			CQuestionFileVisitor printer = new CQuestionFileVisitor(CGlobalConfiguration.PairNameFilePath, bJsonOutput);			
			
			for(int i=0; i< questionFiles.length; i++){
				printer.getWriteFile().OpenFile(answerFiles[i]);
				TextDataFileReader r = new TextDataFileReader();
				
				r.ReadFile(questionFiles[i], "utf-8", printer);
			}
			
			
			System.setErr(err); 	
			System.out.println(CProcessStatistic.getStatistic().getStatisticReport());
			System.out.println("Done");
	   }
	   

	
	static void testQuestionAnswer(boolean jsonOutput){
		String[]  inputString = new String[]{
				"Who was governor of Oregon when Shanghai noon was released?",
				"which city did  Frederick Corder live in when he died"
			};
		
		PrintStream err = System.err;

		// now make all writes to the System.err stream silent 
		System.setErr(new PrintStream(new OutputStream() {
		    public void write(int b) {
		    }
		}));
		
		NLPTool.InitStandfordNLP();
		
		INodeProcessor<CSentenceAnalysisReport> proProcess = RewritePrinter.CreateProProcessHandle();
		
		CReportOutput output = null;
		if( jsonOutput)
			output = new CReportOutputJson(CProcessStatistic.getStatistic() );
		else
			output = new CReportOutput(CProcessStatistic.getStatistic() );
		
		CAnswerQuestionProcess questionProcess = new CAnswerQuestionProcess(
									CGlobalConfiguration.QAServiceAddr_Or_QAFilePath,output);
		
		questionProcess.LoadPairMapFile(CGlobalConfiguration.GloabalSourceFolder +"\\dictionary\\temporal-predicate-pairs");
		
		
		INodeProcessor<CSentenceAnalysisReport> qaProcess = RewritePrinter.CreateSentenceProcessHandle( questionProcess );

		for(String str: inputString){
			output.WriteLine("-------------------------------------------------------------");
			output.WriteLine(str);			
		
			CSentenceAnalysisReport rp = new CSentenceAnalysisReport(str);

			proProcess.Process(rp);
			System.out.println(rp.Text);
			qaProcess.Process(rp);
		}
		
		output.WriteLine("-------------------- statistic  report -------------------------");
		output.WriteLine(CProcessStatistic.getStatistic().getStatisticReport().toString());
		
	}
	
	

	
	static void  PressTest_CQAService(int maximum){
		for(int i = 0; i<maximum; i++){
			System.out.println("Circle to :" +   i);
			TestCQAService();
		}
	}

	static void  TestCQAService(){
		IQAService qa = new CQAServiceOnline(CGlobalConfiguration.QAServiceAddr_Or_QAFilePath);
		CQAResult result = qa.QueryAnswer("what did Germany lost after the Treaty of Versailles?");
		System.out.println(result);	
	}

	
	
	static CQAResult GetQAResultParse(){

    	CQAServiceOnline qa = new CQAServiceOnline("http://example.com/");
		CStringTextVisitor visitor = new CStringTextVisitor();
		
		
		TextDataFileReader r = new TextDataFileReader();
		r.ReadFile(CGlobalConfiguration.GloabalSourceFolder +"\\TEMPO_TEMPO_2.htm", "utf-8", visitor);		
		
		String htmldoc = visitor.GetText();
		
		CQAResult qaR = qa.getAnswerContent(htmldoc);

		return qaR;
	}
	
	static void PressTest_QAResult(int maximum){
		for(int i=0; i< maximum; i++)
			TestQAResult();
	}
	
	
	static void TestQAResult(){
		CQAResult qaR = GetQAResultParse();
		System.out.println(qaR.toString());
	}
	
	
	static void TestQAResultPairNameProcess(){
		
		CAnswerFilterContext context = new CAnswerFilterContext( null);
		context.QuestionAnwser = GetQAResultParse();
	
		
		CPairCandidatesFilter proc = new CPairCandidatesFilter(null);
		proc.LoadPairDictionFile(CGlobalConfiguration.GloabalSourceFolder +"\\dictionary\\temporal-predicate-pairs");
		proc.Process(context);
		for( CPairAnswerCandidate ans :  context.QuestionAnwser.PairCandidates){
			System.out.println(ans);
			
		}
		
		
	}
	
}


class CStringTextVisitor extends CFileVisitorBase{

	StringBuilder buffer = new StringBuilder(102400);
	
	@Override
	public void StartVisitFile() {
		
		buffer.setLength(0);
	}

	
	@Override
	public void Visit(DataFileLineInfo lineInfo) {
		
		buffer.append(lineInfo.Text);
	}
	
	public String GetText(){
		return buffer.toString();
	}
	
}

class CQuestionFileVisitor extends CFileVisitorBase{
	
	INodeProcessor<CSentenceAnalysisReport> proProcess;
	INodeProcessor<CSentenceAnalysisReport> qaProcess;
	CReportOutput outputHandle;
	TextDataFileWriter writeFile;
	
	public TextDataFileWriter getWriteFile() {
		return writeFile;
	}

	public CQuestionFileVisitor(String pairNameFile ,boolean jsonformatOutput){
		
		
		this.writeFile = new TextDataFileWriter();
		this.writeFile.AppendMode = false;
		if( jsonformatOutput)
			this.outputHandle = this.CreateOutputJson(this.writeFile);
		else
			this.outputHandle = this.CreateOutput(this.writeFile);
		this.proProcess = RewritePrinter.CreateProProcessHandle();
	
		CAnswerQuestionProcess questionProcess = new CAnswerQuestionProcess(
									CGlobalConfiguration.QAServiceAddr_Or_QAFilePath, outputHandle);
		
		questionProcess.LoadPairMapFile(pairNameFile);
		this.qaProcess = RewritePrinter.CreateSentenceProcessHandle( questionProcess );		
		
	}
	
	@Override
	public void Visit(DataFileLineInfo lineInfo) {
		
		SentenceInfo s = GetSentenceInfo(lineInfo.Text);
		
		if(s!=null && s.Question != null){		
			this.outputHandle.WriteLine("-------------------------------------------------------------");
			this.outputHandle.WriteLine(String.format("%d|||%s", lineInfo.FileLineNo, s.Question.trim()));			
			CSentenceAnalysisReport rp = new CSentenceAnalysisReport(s.Question);
			rp.AnswerReport.GodenAnswer = s.Answer;
			
			proProcess.Process(rp);
			qaProcess.Process(rp);
			

		}
	}
	
	SentenceInfo GetSentenceInfo(String text) {
		
		String[] strQA = text.trim().split("\t");
		
		if(strQA !=null && strQA.length>1){
			return ( new SentenceInfo(strQA[0], strQA[1]));			
		}
		
		return null;
	}
	
	
	@Override
	public void EndVisitFile() {
		
		if(this.writeFile != null){
			this.outputHandle.WriteLine("-------------------------------------------------------------");
			this.writeFile.Close();
		}
	}
	
	@Override
	public void StartVisitFile() {

	}
	
	CReportOutput CreateOutput(TextDataFileWriter wr){
		
		COutputToFile fileHandle = new COutputToFile(null);
		fileHandle.setFileWriter(wr);
		
		COutputToConsole console = new COutputToConsole(fileHandle);

		CReportOutput output = new CReportOutput( CProcessStatistic.getStatistic());
		output.setOutputHandle(console);		
	
		return output;
	}
	
	CReportOutputJson CreateOutputJson(TextDataFileWriter wr){
		
		COutputToFile fileHandle = new COutputToFile(null);
		fileHandle.setFileWriter(wr);
		
		COutputToConsole console = new COutputToConsole(fileHandle);

		CReportOutputJson output = new CReportOutputJson( CProcessStatistic.getStatistic());
		output.setOutputHandle(console);		
	
		return output;
	}
	
}

class SentenceInfo{
	public String Question;
	public String Answer;
	public SentenceInfo(String text, String answer){
		this.Question = text.trim();
		this.Answer = answer.trim();		
	}
}


class CQuestionOutput{

	IOutputString outputHandle;
	StringBuilder StringBuf;
	
	public void setOutputHandle(IOutputString outputHandle) {
		this.outputHandle = outputHandle;
	
	}

	public CQuestionOutput(){
		this.outputHandle = new COutputToConsole(null);
		this.StringBuf = new StringBuilder();
		
	}
	
	public void Write(String str){
		this.outputHandle.Write(str);
	}
	
	public void WriteLine(String str){
		this.outputHandle.WriteLine(str);
	}
	

	public void PrintReport(CSentenceAnalysisReport rp, SentenceInfo sentence){
		if(rp == null)	return ;
		this.StringBuf.setLength(0);
		
		this.StringBuf.append(sentence.Question);
		this.StringBuf.append("\t");
		this.StringBuf.append(sentence.Answer);
		
		OutputAnswer(rp);
		OutputQASystemAnswer(rp);
		
		this.WriteLine(StringBuf.toString());
	}
	void OutputAnswer(CSentenceAnalysisReport rp){
		StringBuf.append("\t[");		
		
		if(rp.AnswerReport.Answer != null && rp.AnswerReport.Answer.size() > 0){
			for(int i =0; i< rp.AnswerReport.Answer.size(); i++){
				
				StringBuf.append("\"");
				StringBuf.append(rp.AnswerReport.Answer.get(i));
				StringBuf.append("\"");
				
				if( i < (rp.AnswerReport.Answer.size() -1 ))
					StringBuf.append(",");
			}
		
		}
	
		StringBuf.append("]");
	}
	void OutputQASystemAnswer(CSentenceAnalysisReport rp){
		StringBuf.append("\t[");		
		
		if(rp.AnswerReport.QA_Answer != null && rp.AnswerReport.QA_Answer.size() > 0){
			for(int i =0; i< rp.AnswerReport.QA_Answer.size(); i++){
				
				StringBuf.append("\"");
				StringBuf.append(rp.AnswerReport.QA_Answer.get(i));
				StringBuf.append("\"");
				
				if( i < (rp.AnswerReport.QA_Answer.size() -1 ))
					StringBuf.append(",");
			}
		
		}
	
		StringBuf.append("]");
	}
	
	
}





class CCheckRankOutput extends CReportOutput{

	@Override
	protected void PrintReport(CSentenceAnalysisReport rp) {
		if(rp.SubQuestions != null){
			WriteLine("Sq1:" + rp.AnswerReport.SQ1 );
		}else{
			WriteLine("Question:" + rp.AnswerReport.SQ1 );
		}
		
		WriteLine("Gold Answer:" + rp.AnswerReport.GodenAnswer );
		WriteLine("Check Answer:" + rp.AnswerReport.AnswerChecking );
	}

	public CCheckRankOutput(INodeProcessor<CSentenceAnalysisReport> next) {
		super(next);
	}
	
}
