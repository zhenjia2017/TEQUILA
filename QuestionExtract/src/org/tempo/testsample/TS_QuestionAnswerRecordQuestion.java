package org.tempo.testsample;

import java.io.OutputStream;
import java.io.PrintStream;

import org.tempo.DataFileReaders.CFileVisitorBase;
import org.tempo.DataFileReaders.DataFileLineInfo;
import org.tempo.DataFileReaders.TextDataFileReader;

import org.tempo.QuestionAnswer.CAnswerQuestionWriteQuestionProcess;
import org.tempo.SentenceAnalysis.CProcessStatistic;
import org.tempo.SentenceAnalysis.CReportOutput;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.NLPTool;
import org.tempo.Util.CGlobalConfiguration;
import org.tempo.Util.COutputToConsole;


public class TS_QuestionAnswerRecordQuestion {

	public static void main(String[] args) {
		
		TestQuestionFromFile();
		

	}
	
	   static void TestQuestionFromFile(){
			
			PrintStream err = System.err;  

			// now make all writes to the System.err stream silent 
			System.setErr(new PrintStream(new OutputStream() {
			    public void write(int b) {
			    }
			}));
			
			
			NLPTool.InitStandfordNLP();
			
			String[] questionFiles = new String []{
				CGlobalConfiguration.GloabalSourceFolder +"\\"
						+ ""
						+ ".txt",
			};
			
			String[] answerFiles = new String []{
				"c:\\temp\\TempQuestions_subquestions.txt",
			};
		
			CQuestionFileRecordVisitor printer = new CQuestionFileRecordVisitor();			
			
			for(int i=0; i< questionFiles.length; i++){
				printer.SetWriteToFile(answerFiles[i]);
				TextDataFileReader r = new TextDataFileReader();
				r.ReadFile(questionFiles[i], "utf-8", printer);
			}
			System.setErr(err); 	
			System.out.println("Done");
	   }
}


class CQuestionFileRecordVisitor extends CFileVisitorBase{
	
	INodeProcessor<CSentenceAnalysisReport> proProcess;
	INodeProcessor<CSentenceAnalysisReport> qaProcess;
	CReportOutput outputHandle;
	String writeToFile;
	CAnswerQuestionWriteQuestionProcess questionProcess;

	public CQuestionFileRecordVisitor(){
		
		this.outputHandle = this.CreateOutput();
		this.proProcess = RewritePrinter.CreateProProcessHandle();
		this.questionProcess = new CAnswerQuestionWriteQuestionProcess(outputHandle);
		this.qaProcess = RewritePrinter.CreateSentenceProcessHandle( questionProcess );		
		
	}
	
	public void SetWriteToFile(String toFile){
		this.writeToFile = toFile;
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
		
		String[] strQA = text.split("\t");
		
		if(strQA !=null && strQA.length>1){
			return ( new SentenceInfo(strQA[0], strQA[1]));			
		}
		
		return null;
	}
	
	
	@Override
	public void EndVisitFile() {
		this.outputHandle.WriteLine("-------------------------------------------------------------");
		this.questionProcess.CloseFile();

	}
	
	@Override
	public void StartVisitFile() {
		this.questionProcess.OpenFile(this.writeToFile);
	}
	
	CReportOutput CreateOutput(){
	
		COutputToConsole console = new COutputToConsole(null);

		CReportOutput output = new CReportOutput( CProcessStatistic.getStatistic());
		output.setOutputHandle(console);		
	
		return output;
	}
	
}
