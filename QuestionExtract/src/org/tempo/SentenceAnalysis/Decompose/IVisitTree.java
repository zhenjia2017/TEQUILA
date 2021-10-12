package org.tempo.SentenceAnalysis.Decompose;

public interface IVisitTree<TreeNode extends Object> {
	void Visit(TreeNode node);
}
