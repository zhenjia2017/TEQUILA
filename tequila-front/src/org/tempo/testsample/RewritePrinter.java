package org.tempo.testsample;

import java.io.OutputStream;

import java.io.PrintStream;

import org.tempo.DataFileReaders.*;
import org.tempo.SentenceAnalysis.CProcessFixInvalidTags;
import org.tempo.SentenceAnalysis.CProcessMarkDateOrdinalByRule;
import org.tempo.SentenceAnalysis.CProcessMarkEvent;
import org.tempo.SentenceAnalysis.CProcessMarkNEREntity;
import org.tempo.SentenceAnalysis.CProcessMarkTimeTagByHeildeTime;
import org.tempo.SentenceAnalysis.CProcessMarkWhenTag;
import org.tempo.SentenceAnalysis.CProcessNLP;
import org.tempo.SentenceAnalysis.CProcessShapeSentence;
import org.tempo.SentenceAnalysis.CProcessStatistic;
import org.tempo.SentenceAnalysis.CReportOutput;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.NLPTool;

import org.tempo.SentenceAnalysis.Decompose.CProcessDecomposeSentence;
import org.tempo.SentenceAnalysis.Decompose.CProcessRewriteBySimple4Type_Complex;
import org.tempo.SentenceAnalysis.Decompose.CProcessRewriteBySimple4Type_Simple;
import org.tempo.SentenceAnalysis.Decompose.CSentenceRewriteBase;
import org.tempo.SentenceAnalysis.Dictionary.CTagEntityFromDictionary;

import org.tempo.Util.CGlobalConfiguration;
import org.tempo.Util.COutputToConsole;
import org.tempo.Util.COutputToFile;


public class RewritePrinter {
		
		INodeProcessor<CSentenceAnalysisReport> proProcess;
		INodeProcessor<CSentenceAnalysisReport> qaProcess;
	    
		public static INodeProcessor<CSentenceAnalysisReport> CreateSentenceProcessHandle(INodeProcessor<CSentenceAnalysisReport> rpOut){
			CSentenceRewriteBase hIncomplete = new CProcessRewriteBySimple4Type_Complex(rpOut);
			CSentenceRewriteBase hSimple = new CProcessRewriteBySimple4Type_Simple(hIncomplete);
			CProcessFixInvalidTags hFixTags = new CProcessFixInvalidTags(hSimple);
			CProcessMarkDateOrdinalByRule hDate= new CProcessMarkDateOrdinalByRule(hFixTags);
			CProcessMarkWhenTag hWhen= new CProcessMarkWhenTag(hDate);
			CProcessDecomposeSentence hDecompose = new CProcessDecomposeSentence(hWhen);
			return hDecompose;
		}


		public static INodeProcessor<CSentenceAnalysisReport> CreateProProcessHandle(){	
			
			CProcessMarkTimeTagByHeildeTime heildertime = new CProcessMarkTimeTagByHeildeTime(null);
			heildertime.setHeilderTime(NLPTool.GetHeilderTimeIntance());
			CProcessMarkEvent eventHandle = new CProcessMarkEvent(heildertime);
			eventHandle.setEntityProcess(CGlobalConfiguration.entitySearch);
			CProcessMarkNEREntity  nerhandle = new CProcessMarkNEREntity(eventHandle);
			CProcessNLP nlp = new CProcessNLP(nerhandle);
			return nlp;
		}
		


	
}

