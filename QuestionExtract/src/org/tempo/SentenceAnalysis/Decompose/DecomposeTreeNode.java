package org.tempo.SentenceAnalysis.Decompose;

import java.util.ArrayList;
import java.util.List;

import org.tempo.SentenceAnalysis.IDocumentContext;
import org.tempo.SentenceAnalysis.SentenceSectionDescription;



/** 
* @ClassName: DecomposeTreeNode 
* @Description: TODO  
*/
public class DecomposeTreeNode {
	
	DecomposeTreeNode Parent;
	
	IDocumentContext Document;
	public List<DecomposeTreeNode> Children;
	
	
	public String ReleationDescription;
	SentenceSectionDescription Section;	
	
	
	public boolean IsRelation(String relation){
		return (this.ReleationDescription.compareTo(relation)==0);
	}
	
	public String getReleationDescription() {
		return ReleationDescription;
	}
	
	public void setReleationDescription(String relation ) {
		this.ReleationDescription = relation;
	}
	
	public void setSection(SentenceSectionDescription description) {
		this.Section = description;

	}

	public SentenceSectionDescription getSection() {
		return Section;
	}


	public List<DecomposeTreeNode> getChildren() {
		return Children;
	}

	public DecomposeTreeNode getParent() {
		return Parent;
	}

	public DecomposeTreeNode(DecomposeTreeNode parent, IDocumentContext doc){
		this.Parent = parent;
		this.Document = doc;
		this.Children = new ArrayList<DecomposeTreeNode>();
	}
	
	
	public DecomposeTreeNode CreateChild(String relation, SentenceSectionDescription section){

		DecomposeTreeNode newChild = new DecomposeTreeNode(this, this.Document);
		newChild.setReleationDescription(relation);
		
		SentenceSectionDescription.OffsetSection(this.Section, section, 0);
		newChild.setSection(section);	
		this.Children.add(newChild);
		return newChild;
	}
	
	public void ClearChildren(){
		this.Children.clear();
	}
	
	public boolean HasChild(){
		return (Children!=null && Children.size()>0);
	}
	
	public int getLevel(){
		int nLevel = 0;
		
		DecomposeTreeNode cur = this;
		while(cur.Parent != null){
			nLevel++;
			cur = cur.Parent;
		}
		
		return nLevel;
	}
	
	public void VisitNode(IVisitTree<DecomposeTreeNode> fun){
		if(this.HasChild()){
			for(DecomposeTreeNode nd: this.Children){
				nd.VisitNode(fun);
			}
		}
		
		fun.Visit(this);		
	}
	
	 public static void deepCopy(DecomposeTreeNode src, DecomposeTreeNode copy)
	 {
		 	src.ReleationDescription = copy.ReleationDescription;
		 	src.setSection(copy.getSection());
		 
		 	for(DecomposeTreeNode nd : copy.Children){
		 		DecomposeTreeNode newNode = new DecomposeTreeNode(src, src.Document);
		 		deepCopy(newNode, nd);
		 		src.Children.add(newNode);
		 	}		 

	 }
	 
	 
}
