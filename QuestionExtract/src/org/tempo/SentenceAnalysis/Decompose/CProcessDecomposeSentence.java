package org.tempo.SentenceAnalysis.Decompose;

import java.util.List;
import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.IDocumentContext;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.NLPTool;
import org.tempo.SentenceAnalysis.SentenceSectionDescription;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;


/** 
* @ClassName: CProcessDecomposeSentence 
* @Description: Decompose a sentence to a tree.
*  
*/
public class CProcessDecomposeSentence extends CNodeProcessorBase<CSentenceAnalysisReport>{

	/** composeTree: the decomposition tree for question */  
	DecomposeTree  composeTree;
	
	/** Document: context */  
	IDocumentContext Document;


	public CProcessDecomposeSentence(){
		super(null);
	}
	
	public CProcessDecomposeSentence(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);		

	}
	

	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// check if context if valid for this process
		return (context != null && context.Document != null && context.decomposeTree == null);
	}
	
	/**   
	 * <p>Title: NodeProcess</p>   
	 * <p>Description: deal with the context</p>   
	 * @param context   
	 * @see org.tempo.SentenceAnalysis.CNodeProcessorBase#NodeProcess(java.lang.Object)   
	 */ 
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		context.decomposeTree = _parse(context);
	}

	/** 
	* @Title: Parse 
	* @Description: Parse question
	* @param sentence
	* @return   
	*/
	public  DecomposeTree Parse(String sentence){
		Annotation doc = new Annotation(sentence);
		NLPTool.GetStandfordNLPIntance().annotate(doc);
		
		XDocumentContext docContxt = new XDocumentContext(doc);
		return _parse(docContxt);
	}
	
	/** 
	* @Title: _parse 
	* @Description: decompose the question to a composition tree
	* @param document
	* @return   
	*/
	DecomposeTree _parse(IDocumentContext  document){
		this.composeTree = new DecomposeTree(document);
	    this.Document = document;
		SentenceSectionDescription des = new SentenceSectionDescription(0, document.getTextTokens(0).size() );
		this.composeTree.Root.setReleationDescription(DecomposeTree.STR_ROOT);
		this.composeTree.Root.setSection(des);
		
		Decompose(this.composeTree.Root);
		return this.composeTree;	
	}		

	
	/** 
	* @Title: Decompose 
	* @Description: Decompose a tree node
	* @param treeNode   
	*/
	void Decompose(DecomposeTreeNode treeNode){
		
		if( JudgeAsCompleteSentence(treeNode))  // is a complete question?
			DivideSentenceAsComplexComplete(treeNode);
		else if( JudgeAsInCompleteSenteceOrEvent(treeNode)) //is a incomplete question.
			DivideSentenceAsComplexInComplete(treeNode);		
		else{  //simple question
			//DevideSentenceAsAD_TO(treeNode);
			//CreateSectionNode(DecomposeTree.STR_NORMAL, treeNode, treeNode.Section);
		}
	}
	
	/** 
	* @Title: DecomposeChildren 
	* @Description: decompose child nodes under a tree node.
	* @param treeNode   
	*/
	void DecomposeChildren(DecomposeTreeNode treeNode){
		if( treeNode.HasChild()){
			for(DecomposeTreeNode child : treeNode.Children){
				Decompose(child);
			}
		}
	}
	
	
	/** 
	* @Title: JudgeAsCompleteSentence 
	* @Description: check if the tree node is Complex Complete question.
	* @param treeNode
	* @return   
	*/
	boolean JudgeAsCompleteSentence(DecomposeTreeNode treeNode){
		return (this.JudgeBy(CTokenRule.STR_ComplexComplete,this.Document, treeNode.getSection()));
	}

	/** 
	* @Title: JudgeAsInCompleteSenteceOrEvent 
	* @Description: check if the tree node is complex Incomplete question.
	* @param treeNode
	* @return   
	*/
	boolean JudgeAsInCompleteSenteceOrEvent(DecomposeTreeNode treeNode){
		return (this.JudgeBy(CTokenRule.STR_ComplexIncomplete,this.Document, treeNode.getSection()));
	}
	
	
	
	/** 
	* @Title: DivideSentenceAsComplexComplete 
	* @Description: Divide complete Sentence
	* @param treeNode   
	*/
	void DivideSentenceAsComplexComplete(DecomposeTreeNode treeNode){
		
		//mark treenode as complete flag
		treeNode.setReleationDescription(DecomposeTree.STR_TS); 
		
		//devide the sentence to 2 parts
		CQuestionSentenceJudge judge = CQuestionSentenceJudge.GetSentenceClassfication(CTokenRule.STR_ComplexComplete_Divide);
		CSentenceAnalysisReport r = judge.MatchSentence(this.Document.getTextTokens(0));
		
		//mark first and second part.
		if(r.Sections != null && r.Sections.size()>0){
			CreateSectionNode(DecomposeTree.STR_TS_FIRST, treeNode, r.Sections.get(0));
			if(r.Sections.size() > 2)
				SentenceSectionDescription.MergeSection(r.Sections.get(1), r.Sections.subList(2, r.Sections.size()));
			CreateSectionNode(DecomposeTree.STR_TS_SECOND, treeNode, r.Sections.get(1));			
		}
		
		DecomposeChildren(treeNode);
	}
	
	/** 
	* @Title: DivideSentenceAsComplexInComplete 
	* @Description: divide incomplete question.
	* @param treeNode   
	*/
	void DivideSentenceAsComplexInComplete(DecomposeTreeNode treeNode){
		treeNode.setReleationDescription(DecomposeTree.STR_TE); 
		
		CQuestionSentenceJudge judge = CQuestionSentenceJudge.GetSentenceClassfication(CTokenRule.STR_ComplexIncomplete_Divide);
		CSentenceAnalysisReport r = judge.MatchSentence(this.Document.getTextTokens(0));
		
		if(r.Sections != null && r.Sections.size()>0){
			CreateSectionNode(DecomposeTree.STR_TE_FIRST, treeNode, r.Sections.get(0));
			if(r.Sections.size() > 2)
				SentenceSectionDescription.MergeSection(r.Sections.get(1), r.Sections.subList(2, r.Sections.size()));
			CreateSectionNode(DecomposeTree.STR_TE_SECOND, treeNode, r.Sections.get(1));			
		}
		
		DecomposeChildren(treeNode);		
	}
	


	
	/** 
	* @Title: CreateSectionNode 
	* @Description: create child node with words section information. 
	* @param description
	* @param curNode
	* @param section   
	*/
	void CreateSectionNode(String description, DecomposeTreeNode curNode, SentenceSectionDescription section ){
		curNode.CreateChild(description, section);
	}	
	
	/** 
	* @Title: JudgeBy 
	* @Description: check sentence by rules
	* @param classficationName
	* @param document
	* @param section
	* @return   
	*/
	boolean JudgeBy(String classficationName, IDocumentContext document,SentenceSectionDescription section) {
		CQuestionSentenceJudge judge = CQuestionSentenceJudge.GetSentenceClassfication(classficationName);
		if( judge == null)
			return false;

		return  (judge.JudgeSentenceType(document.getTextTokens(section)) != CQuestionSentenceJudge.EnumRuleMatchMode.NotMatch);
	}
	
	
	/** 
	* @Title: GetFirstPart 
	* @Description: get first part of tree.
	* @param tree
	* @return   
	*/
	public static DecomposeTreeNode GetFirstPart(DecomposeTree tree){
		if(tree.Root.IsRelation(DecomposeTree.STR_TE) || tree.Root.IsRelation(DecomposeTree.STR_TS) ){
			for(DecomposeTreeNode child : tree.Root.Children){
				if(child.IsRelation(DecomposeTree.STR_TE_FIRST) || child.IsRelation(DecomposeTree.STR_TS_FIRST))
					return child;
			}
		}
		
		return null;
	}
	
	/** 
	* @Title: GetSecondPart 
	* @Description: get second part of tree.
	* @param tree
	* @return   
	*/
	public static DecomposeTreeNode GetSecondPart(DecomposeTree tree){
		if(tree.Root.IsRelation(DecomposeTree.STR_TE) || tree.Root.IsRelation(DecomposeTree.STR_TS) ){
			for(DecomposeTreeNode child : tree.Root.Children){
				if(child.IsRelation(DecomposeTree.STR_TE_SECOND) || child.IsRelation(DecomposeTree.STR_TS_SECOND))
					return child;
			}
		}
		
		return null;		
	}
	

}

/** 
* @ClassName: XDocumentContext 
* @Description: a internal context class for decompose.
*  
*/
class XDocumentContext implements IDocumentContext {

	@Override
	public List<CoreLabel> getTextTokens(SentenceSectionDescription sec) {
		// TODO Auto-generated method stub
		List<CoreLabel> lst = this.getTextTokens(0);
		return lst.subList(sec.Start, sec.End);
	}

	Annotation document;
	
	XDocumentContext(Annotation doc){
		this.document = doc;
	}
	
	@Override
	public int GetErrorCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String GetErrorInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation getDocument() {
		// TODO Auto-generated method stub
		return this.document;
	}

	@Override
	public String getSentence() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public List<CoreLabel> getTextTokens(int sentenceOrder) {
		// TODO Auto-generated method stub
		if( this.document != null)
			return NLPTool.GetTokens(this.document, sentenceOrder);		

		return null;
	}
	
	@Override
	public String GetTextOfSection(SentenceSectionDescription sec) {
		if(this.document == null)
			return "";
		return (NLPTool.GetTextOfTokens(this.getTextTokens(0), sec.Start, sec.End));
	}

}	

