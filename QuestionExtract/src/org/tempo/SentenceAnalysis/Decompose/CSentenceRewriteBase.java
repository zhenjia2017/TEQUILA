package org.tempo.SentenceAnalysis.Decompose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.tempo.SentenceAnalysis.CCorefString;
import org.tempo.SentenceAnalysis.CEventTag;
import org.tempo.SentenceAnalysis.CLabelTag;
import org.tempo.SentenceAnalysis.CNEREntityTag;
import org.tempo.SentenceAnalysis.CNodeProcessorBase;
import org.tempo.SentenceAnalysis.CSentenceAnalysisReport;
import org.tempo.SentenceAnalysis.ILabelTag;
import org.tempo.SentenceAnalysis.INodeProcessor;
import org.tempo.SentenceAnalysis.NLPTool;
import org.tempo.Util.CGlobalConfiguration;
import org.tempo.Util.CStringUtil;

import java.util.HashSet;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;

/** 
* @ClassName: CSentenceRewriteBase2 
* @Description: rewrite question base class
*  
*/
public class CSentenceRewriteBase extends CNodeProcessorBase<CSentenceAnalysisReport> {
	public static String STR_RLN_COMPOUND = "compound";
	public static String STR_RLN_VBNMOD = "nmod.*";
	public static String STR_RLN_AUX = "aux.*";	
	public static String STR_RLN_NSUBJ = ".?subj.*";
	
	public static String STR_TAG_NN = "NN.*";
	public static String STR_TAG_VB = "VB.*";
	public static String STR_TAG_WTD = "W.*";
	public static String STR_SYMPLE = "~!@#$%^&*()_+{}|:<>?,./;[]\\=-'\"";
	
	public static String WordSplitString = " ";
	
	
	protected CSentenceRewriteBase(){
		super(null);
		
	}	
	
	public CSentenceRewriteBase(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
	
	}
	
	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		
	}
	
	/** 
	* @Title: getTagBefore 
	* @Description: get Tags before a position.
	* @param entities
	* @param pos
	* @return   
	*/
	ILabelTag getTagBefore(List<? extends ILabelTag> entities, int pos){
		for(ILabelTag tag: entities){
			if(tag.getEnd() <= pos)
				return tag;
		}
		
		return null;
	}
	
	/** 
	* @Title: getTagAfter 
	* @Description: get tags after a position.
	* @param entities
	* @param pos
	* @return   
	*/
	ILabelTag  getTagAfter(List<? extends ILabelTag> entities, int pos){
		for(ILabelTag tag: entities){
			if(tag.getStart() > pos)
				return tag;
		}
		
		return null;
	}

	
	/** 
	* @Title: BasicParse 
	* @Description: Parse a question structure.
	* @param tree
	* @param text
	* @return   
	*/
	protected SentenceParseInfo BasicParse(DecomposeTree tree, String text){
		SentenceParseInfo parseInfo = new SentenceParseInfo();
		parseInfo.tree = tree;
		parseInfo.Text = text;
		parseInfo.sentence = NLPTool.GetSentence(tree.Document.getDocument(), 0);	
		parseInfo.tokens = parseInfo.sentence.get(TokensAnnotation.class);
		parseInfo.dependenciesGraph = parseInfo.sentence.get(BasicDependenciesAnnotation.class);
		parseInfo.signalIndexStart = this.getSignalWordIndex(parseInfo.dependenciesGraph);
		parseInfo.signalWordLength = this.getSignalWordLength(parseInfo);
		parseInfo.signalWordString = this.getSignalWordString(parseInfo);
		parseInfo.SubjectEdge = this.getSubjectEdge(parseInfo.signalIndexStart, parseInfo.dependenciesGraph);
		parseInfo.SubjectFullString = this.GetFullSubjectString(parseInfo);
		parseInfo.Coref = this.GetSubQuestionCorefString(parseInfo);
		
		return parseInfo;
	}
	
	/** 
	* @Title: getSignalWordLength 
	* @Description: get signal words length
	* @param parseInfo
	* @return   
	*/
	int getSignalWordLength(SentenceParseInfo parseInfo){
		if(parseInfo == null || parseInfo.signalIndexStart == null)
			return 0;
		
		String signalFirstWord = parseInfo.signalIndexStart.lemma();
		String keyword = CTokenRule.getMatchSignalWord(signalFirstWord);
		
		if(keyword== null || keyword.length()<1)
			return 0;
		return (CStringUtil.CountInString(keyword, ' ') + 1);
	}
	
	/** 
	* @Title: RemoveDateTagString 
	* @Description: remove all date & ordinal tags in question.
	* @param text
	* @return   
	*/
//	protected String RemoveDateTagString(String text){
//		if(CGlobalConfiguration.RemoveDateTag){
//			text = this.RemoveTagText(text, this.Context.Dates);
//			text = this.RemoveTagText(text, this.Context.TimeReports);
//		}
//				
//		return text;
//	}
	
	/** 
	* @Title: RemoveDateOrdinalTagString 
	* @Description: TODO
	* @param text
	* @return   
	*/
	protected String RemoveDateOrdinalTagString(String text){
		if(CGlobalConfiguration.RemoveDateTag){
			text = this.RemoveTagText(text, this.Context.Dates);		
			text = this.RemoveTagText(text, this.Context.TimeReports);
			text = this.RemoveTagText(text, this.Context.Ordinals);
		}
		return text;
	}
	
	/** 
	* @Title: RemoveTagText 
	* @Description: remove tag's text in sentence.
	* @param text
	* @param tags
	* @return   
	*/
	String RemoveTagText(String text , List<? extends CLabelTag> tags ){
		for( CLabelTag tag: tags){
			text = text.replaceFirst(tag.Text, "");
		}
		return text;
	}
	
	/** 
	* @Title: GetFullSubjectString 
	* @Description: get subject string of question by syntax tree.
	* @param parseInfo
	* @return   
	*/
	protected String GetFullSubjectString(SentenceParseInfo parseInfo){
		if(parseInfo.SubjectEdge == null)
			return "";
		
		List<Integer> lstTree = GetTreeNodesOrdered(parseInfo.SubjectEdge.getDependent(),  parseInfo.dependenciesGraph);
		
		//remove invalid id;
		List<Integer> lst = GetValidTreeNodes(lstTree, parseInfo.signalIndexStart.index());
		
		ILabelTag tag = (CNEREntityTag)GetTagIfPartOfEntity(this.Context.NEREntities, lst);
		
		if(tag != null){
			
			return parseInfo.Text.substring(tag.getBeginPos(), tag.getEndPos()).trim();
			
		}else{
		
			return GetNodesText(lst, parseInfo.tokens);
		}
			
	}
	
	/** 
	* @Title: GetValidTreeNodes 
	* @Description: get token indexes after signal word
	* @param lstree
	* @param signalIndex
	* @return   
	*/
	List<Integer>   GetValidTreeNodes(List<Integer> lstree, int signalIndex){
		List<Integer>  lst = new ArrayList<Integer>();		
		boolean direction = lstree.get(0) < signalIndex ? true : false;
		
		for(Integer id:lstree){
			if( (direction && (id<signalIndex) ) ||  (!direction && (id>signalIndex)) ){
				lst.add(id);
			}
		}
		
		return lst;
	}
	
	
	/** 
	* @Title: GetTagIfPartOfEntity 
	* @Description: get Tag if it's in a token list
	* @param tags
	* @param lstNodes
	* @return   
	*/
	ILabelTag GetTagIfPartOfEntity(List<? extends ILabelTag> tags, List<Integer> lstNodes){
		for(ILabelTag tag : tags){
			int startIndex = lstNodes.get(0) -1;
			int endIndex = lstNodes.get(lstNodes.size() -1) -1;
			
			if(  startIndex >= tag.getStart() &&  endIndex<= tag.getEnd() )
				return tag;
		}
		
		return null;
	}
	
	
	
	/** 
	* @Title: ConnectString 
	* @Description: joint 2 string , removing ? in the first part.
	* @param orgText
	* @param addPart
	* @return   
	*/
	protected String ConnectString(String orgText, String addPart){
		orgText = orgText.replace("?", "");
		
		if(addPart != null && addPart.length() > 0){			
			return (orgText + WordSplitString + addPart);
		}else{
			return orgText;
		}
	}
	
	
	/** 
	* @Title: CreateRewriteQuestions 
	* @Description: create RewriteQuestion object
	* @param qestionType
	* @param subQuestion
	* @param parseInfo
	* @return   
	*/
	CRewriteQuestionResult CreateRewriteQuestions(String qestionType, String subQuestion,
												SentenceParseInfo parseInfo) {
		CRewriteQuestionResult rq = new CRewriteQuestionResult();
		rq.QuestionType = qestionType;
		rq.SignalWord =  parseInfo.signalWordString;  
		rq.SignalPos = parseInfo.signalIndexStart.beginPosition();
		
		if(subQuestion.length() > 0){
			//add "?" at the end of question.
			rq.FirstQuestion = parseInfo.Text.substring(0, parseInfo.signalIndexStart.beginPosition())+ "?";
			
		}else{
			rq.FirstQuestion = parseInfo.Text;
		}
		
		rq.FirstQuestion = this.RemoveDateOrdinalTagString(rq.FirstQuestion);
		
		if(subQuestion.length()>0 && subQuestion.endsWith("?") == false){
			rq.SubQuestion = subQuestion + "?";
		}else{
			rq.SubQuestion = subQuestion;
		}
		
		//remove date , ordinal text in question.
//		if(rq.SubQuestion != null)
//			rq.SubQuestion  = this.RemoveDateOrdinalTagString(rq.SubQuestion);
		
		//Coref replace
		if(parseInfo.Coref != null){
			rq.SubQuestion = rq.SubQuestion.replace(" " + parseInfo.Coref.Source + " ", " " + parseInfo.Coref.CorefString + " ");
		}
		return rq;
	}	
	
		
	
	/** 
	* @Title: getSubjectEdge 
	* @Description: get subject edge in semantic graph
	* @param vertex
	* @param graph
	* @return   
	*/
	SemanticGraphEdge  getSubjectEdge(IndexedWord vertex, SemanticGraph graph){
		//get ".subj.* subject which is nearby the signal word;
		List<SemanticGraphEdge> dges = this.FindEdgeAfter(STR_RLN_NSUBJ, graph, vertex.index());
		
		if( dges !=null && dges.size() > 0){
			return dges.get(0);	
		}else {
			dges =  this.FindEdgeBefore(STR_RLN_NSUBJ,  graph, vertex.index());
			
			if( dges != null && dges.size() > 0)
				return dges.get(dges.size() -1);
		}
		
		return null;
	}
	
	
	/** 
	* @Title: GetChildren 
	* @Description: get  children of a vertex
	* @param vertex
	* @param childTag
	* @param relation
	* @param graph
	* @return   
	*/
	protected List<IndexedWord> GetChildren(IndexedWord vertex, String childTag, String relation, SemanticGraph graph) {
		if (graph.containsVertex(vertex) == false) {
			throw new IllegalArgumentException();
		}

		List<IndexedWord> lstNodes = new ArrayList<IndexedWord>();
		for (SemanticGraphEdge edge : graph.getOutEdgesSorted(vertex)) {

			if (Pattern.matches(relation, edge.getRelation().getShortName()) && 
								Pattern.matches(childTag, edge.getTarget().tag())) {
				lstNodes.add(edge.getTarget());
			}
		}

		return lstNodes;
	 }
	
	
	/** 
	* @Title: VisitTreeNode 
	* @Description: traverse a vertex iin graph
	* @param nodeSet
	* @param vertex
	* @param graph   
	*/
	static void VisitTreeNode(HashSet<Integer> nodeSet, IndexedWord vertex, SemanticGraph graph){
		List<IndexedWord> children = graph.getChildList(vertex);
		if(children != null && children.size() > 0){
			for(IndexedWord child: children){
				if(nodeSet.contains(child.index()) == false){
					nodeSet.add(child.index());
					VisitTreeNode(nodeSet, child,graph);
				}
			}
		}
	}
	
//	protected String GetTreeText(IndexedWord vertex, SemanticGraph graph, List<CoreLabel> tokens){
//		List<Integer> lst = GetTreeNodesOrdered(vertex,  graph);
//		return GetNodesText(lst, tokens);
//	}
//	
	
	/** 
	* @Title: GetTreeNodesOrdered 
	* @Description: order children of a vertex in graph
	* @param vertex
	* @param graph
	* @return   
	*/
	protected List<Integer> GetTreeNodesOrdered(IndexedWord vertex, SemanticGraph graph) {
	
		HashSet<Integer> nodes = new HashSet<Integer>();
		nodes.add(vertex.index());
		VisitTreeNode(nodes, vertex, graph);
		
		List<Integer> lstNodes = new ArrayList<Integer>(nodes);
		
		Collections.sort(lstNodes, new Comparator<Integer>(){  
            public int compare(Integer arg0, Integer arg1) {  
                return (( arg0 < arg1)? -1:1);
             }});
		
		return lstNodes;
	}
	
	/** 
	* @Title: GetNodesText 
	* @Description: get tokens' text.
	* @param indexedList
	* @param tokens
	* @return   
	*/
	protected String GetNodesText(List<Integer> indexedList, List<CoreLabel> tokens){
	
		StringBuilder  buf = new StringBuilder();
		
		for(Integer index: indexedList){
			buf.append(tokens.get(index-1).word());
			buf.append(WordSplitString);
		}
		
		return (buf.toString().trim());
	}
	
	/** 
	* @Title: GetTextAfterWord 
	* @Description: get text after signal word.
	* @param wordIndex
	* @param tokens
	* @param sentenceText
	* @return   
	*/
	protected String GetTextAfterWord(int wordIndex, List<CoreLabel> tokens, String sentenceText){
		CoreLabel tk = tokens.get(wordIndex);
		String str = sentenceText.substring(tk.endPosition()).trim();
		
		if(str.length() < 1)
			return "";
		
		String lastWord = str.substring(str.length() -1);
		if(STR_SYMPLE.contains(lastWord)){
			return (str.substring(0, str.length() - 1).trim());
		}else {
			return str.trim();
		}
	}
	
	/** 
	* @Title: GetTextBetweenWord 
	* @Description: get text between two tokens.
	* @param startIndex
	* @param endIndex
	* @param tokens
	* @param sentenceText
	* @return   
	*/
	protected String GetTextBetweenWord(int startIndex, int endIndex, List<CoreLabel> tokens, String sentenceText){
		CoreLabel tkStart = tokens.get(startIndex);
		CoreLabel tkEnd = tokens.get(endIndex);
		String str = sentenceText.substring(tkStart.endPosition(), tkEnd.beginPosition()).trim();

		return str.trim();
	}	
	
	
	/** 
	* @Title: GetChildren 
	* @Description: get chidren of a vertex.
	* @param vertex
	* @param childTag
	* @param graph
	* @return   
	*/
	protected List<IndexedWord> GetChildren(IndexedWord vertex, String childTag,  SemanticGraph graph) {
		if (graph.containsVertex(vertex) == false) {
			throw new IllegalArgumentException();
		}

		List<IndexedWord> lstNodes = new ArrayList<IndexedWord>();
		for (SemanticGraphEdge edge : graph.getOutEdgesSorted(vertex)) {

			if (Pattern.matches(childTag, edge.getTarget().tag())) {
				lstNodes.add(edge.getTarget());
			}
		}

		return lstNodes;
	}
	
	/** 
	* @Title: getParents 
	* @Description: get parents of a vertex.
	* @param vertex
	* @param parentTag
	* @param graph
	* @return   
	*/
	protected List<IndexedWord> getParents(IndexedWord vertex, String parentTag, SemanticGraph graph ) {
		if (graph.containsVertex(vertex) == false) {
			throw new IllegalArgumentException();
		}

		List<IndexedWord> lstNodes = new ArrayList<IndexedWord>();

		for (IndexedWord v : graph.getPathToRoot(vertex)) {
			if (v.index() > 0 && Pattern.matches(parentTag, v.tag())) {
				lstNodes.add(v);
			}
		}

		return lstNodes;
	}
	
	
	/** 
	* @Title: FindRelationEdges 
	* @Description: find edges related to a vertex.
	* @param vertex
	* @param relationName
	* @param graph
	* @return   
	*/
	protected List<SemanticGraphEdge> FindRelationEdges( IndexedWord vertex, String relationName, SemanticGraph graph){
	    if( graph.containsVertex(vertex) == false){
		      throw new IllegalArgumentException();
		}
		
		List<SemanticGraphEdge>  lstNodes = new ArrayList<SemanticGraphEdge>();
		for(SemanticGraphEdge e: graph.getOutEdgesSorted(vertex)){
			String edgShortName = e.getRelation().getShortName();
			if(Pattern.matches(relationName, edgShortName )){
				lstNodes.add(e);
			}			
		}
		
		return lstNodes;
		
	}

	
	/** 
	* @Title: getSignalWordIndex 
	* @Description: get signal wordindex in graph
	* @param graph
	* @return   
	*/
	protected IndexedWord getSignalWordIndex(SemanticGraph graph){
		DecomposeTreeNode secondPart = CProcessDecomposeSentence.GetSecondPart(this.Context.decomposeTree);
		if( secondPart == null || secondPart.Section == null)
			return null;
		
		return   graph.getNodeByIndex(secondPart.Section.Start+1);
	}
	
	
	/** 
	* @Title: GetVerbs 
	* @Description: get verbs 
	* @param tokens
	* @param graph
	* @return   
	*/
	List<VerbAuxInfo> GetVerbs(List<CoreLabel> tokens , SemanticGraph graph){

		List<VerbAuxInfo> results = new  ArrayList<VerbAuxInfo>();
		HashSet<Integer> indexedMap = new HashSet<Integer>();
				
		List<SemanticGraphEdge> auxEdges= this.FindEdge( STR_RLN_AUX, STR_TAG_VB,  STR_TAG_VB, graph);
		for(SemanticGraphEdge e: auxEdges){
			results.add( new VerbAuxInfo(this.CoreLabelToWordInfo(e.getDependent().backingLabel()),
									  this.CoreLabelToWordInfo(e.getGovernor().backingLabel()) ));
			
			SaveIndexedtoSet(indexedMap, e.getDependent() );
			SaveIndexedtoSet(indexedMap, e.getGovernor()  );
		}
	
		for(CoreLabel tk: tokens){
			//System.out.println(tk.index());
			if( Pattern.matches(STR_TAG_VB, tk.tag()) && ! indexedMap.contains(tk.index()) ){
				results.add(new VerbAuxInfo(null,  this.CoreLabelToWordInfo(tk) ));					
			}
		}
		
		return results;
	}
	
	/** 
	* @Title: SaveIndexedtoSet 
	* @Description: store a indexed word
	* @param set
	* @param v   
	*/
	void SaveIndexedtoSet(HashSet<Integer> set, IndexedWord v){
		if( set.contains(v.index()) == false)
			set.add(v.index());
	}

	
	/** 
	* @Title: CoreLabelToWordInfo 
	* @Description: CoreLabel to WordInfo
	* @param token
	* @return   
	*/
	WordInfo  CoreLabelToWordInfo(CoreLabel token){
		return ( new WordInfo( token.index(), token.word(), token.tag() ,token.lemma() )); 
	}

	
	/** 
	* @Title: FindEdge 
	* @Description: find edges as a relation name on graph.
	* @param relationName
	* @param graph
	* @return   
	*/
	protected List<SemanticGraphEdge>  FindEdge(String relationName, SemanticGraph graph ){
		String pattern = relationName;
		List<SemanticGraphEdge> results = new ArrayList<SemanticGraphEdge>();
        for (SemanticGraphEdge e : graph.edgeIterable()) {
        	String edgShortName = e.getRelation().getShortName();
        	//System.out.println(String.format("%s-r:%s->%s", e.getGovernor().word(), edgShortName, e.getDependent().word()));
        	if(Pattern.matches(pattern, edgShortName))
        		results.add(e);
        }
        
        return results;
	}
	
	/** 
	* @Title: FindEdge 
	* @Description: find edges match pattern.
	* @param relationName
	* @param depTag
	* @param govTag
	* @param graph
	* @return   
	*/
	protected List<SemanticGraphEdge>  FindEdge(String relationName, String depTag,
													String govTag, SemanticGraph graph ){
		String pattern = relationName;
		List<SemanticGraphEdge> results = new ArrayList<SemanticGraphEdge>();
        for (SemanticGraphEdge e : graph.edgeIterable()) {
        	String edgShortName = e.getRelation().getShortName();
        	if(Pattern.matches(pattern, edgShortName) && Pattern.matches(depTag,e.getDependent().tag()) &&
        			Pattern.matches(govTag, e.getGovernor().tag() )	)
        		results.add(e);
        }
        
        return results;
	}	
	
	/** 
	* @Title: FindEdgeAfter 
	* @Description: find relation edges after a index.
	* @param relationName
	* @param graph
	* @param beginIndexed
	* @return   
	*/
	protected List<SemanticGraphEdge>  FindEdgeAfter(String relationName, SemanticGraph graph, int beginIndexed){
		List<SemanticGraphEdge> allEdge = FindEdge(relationName, graph);		

		List<SemanticGraphEdge> results =  new ArrayList<SemanticGraphEdge>();	  
        for (SemanticGraphEdge e : allEdge) {
          	if((e.getDependent().index()) >= beginIndexed && (e.getGovernor().index())>=beginIndexed)
        		results.add(e);
        }
        
        return results;
	}	
	
	/** 
	* @Title: FindEdgeBefore 
	* @Description: find relation edges before a index.
	* @param relationName
	* @param graph
	* @param beginIndexed
	* @return   
	*/
	protected List<SemanticGraphEdge>  FindEdgeBefore(String relationName, SemanticGraph graph, int beginIndexed){
		List<SemanticGraphEdge> allEdge = FindEdge(relationName, graph);		

		List<SemanticGraphEdge> results =  new ArrayList<SemanticGraphEdge>();	  
        for (SemanticGraphEdge e : allEdge) {
          	if( (e.getDependent().index()) < beginIndexed && (e.getGovernor().index()) < beginIndexed)
        		results.add(e);
        }
        
        return results;
	}	
	
	
	/** 
	* @Title: GetVerbBefore 
	* @Description: get verbs before a word index.
	* @param verbs
	* @param pos
	* @return   
	*/
	VerbAuxInfo GetVerbBefore(List<VerbAuxInfo>  verbs, int pos){
		VerbAuxInfo result = null;
		for(VerbAuxInfo vb: verbs){
			if( (vb.auxWord != null && vb.auxWord.WordIndex >=pos) ||
					(vb.verbWord != null && vb.verbWord.WordIndex >= pos) )
				continue;
			
			if( vb.auxWord != null ){
				result = vb;
				break;
			}else if(result != null ){
				if( vb.verbWord.WordIndex < result.verbWord.WordIndex)
					result = vb;
			}else{
				result = vb;
			}
		}
		return result;
	}

	/** 
	* @Title: GetVerbAfter 
	* @Description: get verbs after a word index.
	* @param verbs
	* @param pos
	* @return   
	*/
	VerbAuxInfo GetVerbAfter(List<VerbAuxInfo>  verbs, int pos){
		VerbAuxInfo result = null;
		for(VerbAuxInfo vb: verbs){
			if( (vb.auxWord != null && vb.auxWord.WordIndex <pos) ||
					(vb.verbWord != null && vb.verbWord.WordIndex < pos) )
				continue;
			
			if( vb.auxWord != null ){
				result = vb;
				break;
			}else if(result != null ){
				if( vb.verbWord.WordIndex < result.verbWord.WordIndex)
					result = vb;
			}else{
				result = vb;
			}
		}
		return result;		
	}
	
	/** 
	* @Title: GetSubQuestionCorefString 
	* @Description: get coref .
	* @param senInfo
	* @return   
	*/
	CCorefString GetSubQuestionCorefString(SentenceParseInfo senInfo){
		CoreLabel corefToken = null;
		for(int i = senInfo.signalIndexStart.index(); i < senInfo.tokens.size(); i++){
			CoreLabel tk = senInfo.tokens.get(i);
			if(tk.tag().startsWith("PRP")){
				corefToken = tk;
				break;
			}
		}
		
		if(corefToken == null) 
			return null;
		

		String coref =  GetCorefTokenString(corefToken, senInfo.Text);
		if(coref == null)
			return null;
		
		return (new CCorefString(corefToken.word(), coref)) ;
		
	}
	
	/** 
	* @Title: GetCorefTokenString 
	* @Description: Get coref by named entities.
	* @param corefToken
	* @param sentenceText
	* @return   
	*/
	String GetCorefTokenString(CoreLabel corefToken, String sentenceText){
		
//		if(this.Context.CorefList !=null && this.Context.CorefList.size()>0){
//			for(List<CorefChain.CorefMention> f: this.Context.CorefList){
//				for(CorefChain.CorefMention cf: f){
//					
//				}
//			}
//		}		
		
		if( this.Context.NEREntities.size() < 1)
			return null;
		
		String  refSource = corefToken.word();
		String corefString = null;
		
		for(int i = this.Context.NEREntities.size() -1 ; i >= 0; i--){
			CNEREntityTag tag = this.Context.NEREntities.get(i);
			if(refSource.toLowerCase().startsWith("it")&& (tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Location ||
					 			tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Organization) ){
				
				corefString = sentenceText.substring(tag.getBeginPos(), tag.getEndPos());
				
			}else if(tag.NERObjectType == CNEREntityTag.EnumNERObjectType.Person ){
				corefString = sentenceText.substring(tag.getBeginPos(), tag.getEndPos());
			}
		}
		
		return corefString;		
	}
	
	/** 
	* @Title: IsTagPartAfterSignal 
	* @Description: check if a tag is part of text.
	* @param tags
	* @param percent
	* @param parseInfo
	* @return   
	*/
	boolean IsTagPartAfterSignal(List<? extends ILabelTag> tags, float percent, SentenceParseInfo parseInfo){
		if(tags.size() <= 0)
			return false;
		int signalChar = parseInfo.signalIndexStart.endPosition() + 1;
		int lastChar= parseInfo.Text.length();
		
		for(ILabelTag tag: tags){
			if( tag.getBeginPos() >= signalChar  && tag.getEndPos() <= lastChar ) { 
					return true;
			}
		}
		
		return false;
	}
	
	int getSignalEndPos(SentenceParseInfo parseInfo){
		return (parseInfo.signalIndexStart.beginPosition() + parseInfo.signalWordString.length() );
	}
	
	String getSignalWordString(SentenceParseInfo parseInfo){
		if( parseInfo.signalWordLength == 0)
				return parseInfo.signalIndexStart.word();
		
		int startIndex = parseInfo.signalIndexStart.index()-1;
		int endIndex = startIndex + parseInfo.signalWordLength;
		
		
		CoreLabel tkStart = parseInfo.tokens.get(startIndex);
		CoreLabel tkEnd = parseInfo.tokens.get(endIndex);
		
		return (parseInfo.Text.substring(tkStart.beginPosition(), tkEnd.beginPosition()).trim());
		
	}
	

}

/** 
* @ClassName: EnumSentenceSubQAType 
* @Description: question type.
*  
*/
enum EnumSentenceSubQAType{Unkown, A_When_Verb1_N1_N0, B_When_N0_Verb1_N1, C_When_N0_Verb1_N1,
							D_When_N1_Verb0_N0, E_When_Verb0_N0_N1, F_When_N0_Verb0_N1 ,
							G_When_Event_Occur, H_Date_Only_Keep};

/** 
* @ClassName: SentenceParseInfo2 
* @Description: sentence parse information
*  
*/
class SentenceParseInfo{
	public EnumSentenceSubQAType SubQAType;
	public String Text;
	public IndexedWord signalIndexStart;
	public int signalWordLength;
	public String signalWordString;
	public List<CoreLabel> tokens;
	public SemanticGraph dependenciesGraph;
	public CoreMap sentence;
	public DecomposeTree tree;
	public SemanticGraphEdge SubjectEdge;
	
	public VerbAuxInfo VerbBeforeSignal;
	public VerbAuxInfo VerbAfterSignal;
	public CEventTag secondPartEvent;
	public CNEREntityTag firstPartEntity;
	public CNEREntityTag secondPartEntity;
	public String SecondPartfullString;	
	public String SubjectFullString;
	
	public CCorefString Coref;
	
	public SentenceParseInfo(){
		
	}
	
}


/** 
* @ClassName: EnumVerbStructure 
* @Description: verb type
*  
*/
enum EnumVerbStructure{Unkown, AuxVerb, Be, Do, Does, Did};

/** 
* @ClassName: SentenceParseInfoBase 
* @Description: TODO
*  
*/
class SentenceParseInfoBase{
	int SentenceType;
	int signalIndex;
	SemanticGraphEdge  LemmaEdge;
	VerbAuxInfo Verb;
	CoreMap sentence;
	DecomposeTree tree;
}



/** 
* @ClassName: WordInfo 
* @Description: word information.
*  
*/
class WordInfo{
	int WordIndex;
	String Word;
	String Lemma;
	String Pos;
	
	public WordInfo(int index, String word, String pos, String lemma ){
		this.WordIndex = index;
		this.Word = word;
		this.Lemma = lemma;
		this.Pos = pos;
	}
	
	

}

/** 
* @ClassName: VerbAuxInfo 
* @Description: verbs information in sentence
*  
*/
class VerbAuxInfo {
	private EnumVerbStructure verbStruct;
	
	WordInfo auxWord;
	WordInfo verbWord;

	EnumVerbStructure getVerbStruct(){
		return this.verbStruct;
	}
	

	public VerbAuxInfo(WordInfo aux, WordInfo verb){		
		this.auxWord = aux;
		this.verbWord = verb;
		this.setVerbStruct(aux, verb);
	}

	private void setVerbStruct(WordInfo aux, WordInfo verb){
		if( aux != null){
			this.verbStruct = EnumVerbStructure.AuxVerb;
		}else if(verb.Lemma.compareTo("be") == 0){
			this.verbStruct = EnumVerbStructure.Be;			
		}else if( verb.Pos.compareTo("VB") == 0 ){
			this.verbStruct = EnumVerbStructure.Do;		
		}else if( verb.Pos.compareTo("VBZ") == 0){
			this.verbStruct = EnumVerbStructure.Does;		
		}else{ //vbD
			this.verbStruct = EnumVerbStructure.Did;		
		}
	}
	
	int getVerbFirstIndex(){
		if(this.auxWord != null)
			return this.auxWord.WordIndex;
		else
			return this.verbWord.WordIndex;
	}
	
	/** 
	* @Title: getQuestionFirstVerb 
	* @Description: get first verb word
	* @return   
	*/
	String getQuestionFirstVerb(){
		if( this.verbStruct == EnumVerbStructure.AuxVerb ){
			return this.auxWord.Word;
		}else if(this.verbStruct == EnumVerbStructure.Be){
			return  this.verbWord.Word;			
		}else if( this.verbStruct == EnumVerbStructure.Do ){
				return "do";
		}else if( this.verbStruct == EnumVerbStructure.Does){
			return "does";
		}else{ //vbD
			return "did";
		}
	}
	
	/** 
	* @Title: getQuestionSecondVerb 
	* @Description: get second verb of question.
	* @param tokens
	* @param text
	* @return   
	*/
	String getQuestionSecondVerb(List<CoreLabel> tokens, String text){
		if( this.verbStruct == EnumVerbStructure.AuxVerb ){
			
			return NLPTool.GetTextBetweenWord(this.auxWord.WordIndex, this.verbWord.WordIndex, tokens, text);
		
		}else if(this.verbStruct == EnumVerbStructure.Be){
			return  "";			
		}else {
			return this.verbWord.Lemma;
		}
	}

	
}


