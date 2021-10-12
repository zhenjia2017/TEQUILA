package org.tempo.SentenceAnalysis.Decompose;
import org.tempo.SentenceAnalysis.IDocumentContext;


/** 
* @ClassName: DecomposeTree 
* @Description: decompose tree
*  
*/
public class DecomposeTree {
	public final static String STR_AD="WHEN";
	
	public final static String STR_TD="DATE";
	public final static String STR_TE="COMPLEX_EVENT_INCOMPLETE";
	public final static String STR_TE_FIRST="EVENT_INCOMPLETE_FIRST";
	public final static String STR_TE_SECOND="EVENT_INCOMPLETE_SECOND";
	public final static String STR_TO="ORDINAL";
	
	public final static String STR_TS="COMPLEX_COMPLETE";
	public final static String STR_TS_FIRST="COMPLEX_COMPLETE_FIRST";
	public final static String STR_TS_SECOND="COMPLEX_COMPLETE_SECOND";
	public final static String STR_ROOT="";
	public final static String STR_NORMAL="NORMAL";	
	IDocumentContext Document;

	public IDocumentContext getDocument() {
		return Document;
	}

	DecomposeTreeNode Root;
	
//	public DecomposeTree(String sentence){
//		this.Sentence = sentence;
//		this.Root = new DecomposeTreeNode(null);
//	}
	
	public DecomposeTreeNode getRoot() {
		return Root;
	}


	public DecomposeTree(IDocumentContext doc){
		this.Document = doc;
		this.Root = new DecomposeTreeNode(null, doc);
	}	
	
	
	public void Clear(){
		if( this.Root != null)
			this.Root.ClearChildren();
	}
	
	public static DecomposeTree Copy(DecomposeTree src){
		DecomposeTree newTree= new DecomposeTree(src.Document);
		DecomposeTreeNode.deepCopy(newTree.Root, src.Root);
		
		return newTree;		
	}
	
	
	


}

