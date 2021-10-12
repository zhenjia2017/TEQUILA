package org.tempo.SentenceAnalysis;

import java.util.List;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;

public interface IDocumentContext extends INodeContext {
	Annotation getDocument();
	String getSentence();
	List<CoreLabel> getTextTokens(int senteceOrder);
	List<CoreLabel> getTextTokens(SentenceSectionDescription sec);
	String GetTextOfSection(SentenceSectionDescription sec);

}
