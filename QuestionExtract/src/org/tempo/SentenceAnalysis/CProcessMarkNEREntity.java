package org.tempo.SentenceAnalysis;

import java.util.List;
import java.util.Stack;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;



/** 
* @ClassName: CProcessMarkNEREntity 
* @Description: get NER entity tags.
*  
*/
public class CProcessMarkNEREntity extends CNodeProcessorBase<CSentenceAnalysisReport>{

	//INodeProcessor<CSentenceAnalysisReport>  FixSectionHandle;
	Stack<CoreLabel> tokenBuf; 
	
	CNEREntityTag.EnumNERObjectType curBufType;
	
	public CProcessMarkNEREntity(INodeProcessor<CSentenceAnalysisReport> next){
		super(next);
		this.tokenBuf = new Stack<CoreLabel>();
	}
	
	@Override
	protected boolean CanProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub
		return (context!=null && context.Document != null);
		//return false;
	}

	@Override
	protected void NodeProcess(CSentenceAnalysisReport context) {
		// TODO Auto-generated method stub		
		this.tokenBuf.clear();
		
		//get NLP tokens for the sentence.
		List<CoreLabel> tokens = context.getTextTokens(0);
		if( tokens == null || tokens.size()< 1)
			return ;
		
		//traverse tokens to get tag
		CoreLabel curtoken = tokens.get(0);
		CNEREntityTag.EnumNERObjectType curTkType = this.getTokenNERObjectType(curtoken);

		this.tokenBuf.push(curtoken);
		this.curBufType = curTkType;
		
		//if the next token is part of the current tag, keep it.
		for(int i = 1; i < tokens.size(); i++ ){
			curtoken = tokens.get(i);
			//String nerName = curtoken.get(NamedEntityTagAnnotation.class);
			curTkType = this.getTokenNERObjectType(curtoken);
			
			//if meet a new tag's token, save current tag.
			if( curTkType != this.curBufType){
				SaveAndClearTokenBuf(context);
				this.tokenBuf.push(curtoken);
				this.curBufType = curTkType;
			}else{
				//save current token 
				this.tokenBuf.push(curtoken);
			}
			
		}
		
		if( this.tokenBuf.size() > 0)
			SaveAndClearTokenBuf(this.Context);
		
		//update tag's positions
		ILabelTag.UpdateTagsWordIndex(this.Context.NEREntities, tokens);
	}
	
	/** 
	* @Title: getTokenNERObjectType 
	* @Description: get the NLP NER entity type name of the token
	* @param curtoken
	* @return   
	*/
	CNEREntityTag.EnumNERObjectType getTokenNERObjectType(CoreLabel curtoken){
		CNEREntityTag.EnumNERObjectType curTkType = CNEREntityTag.getNERObjectType(curtoken.get(NamedEntityTagAnnotation.class));
		
		if(curTkType != CNEREntityTag.EnumNERObjectType.Unkown)
			return curTkType;
		else
			return  CNEREntityTag.getNERObjectType(curtoken.get(NormalizedNamedEntityTagAnnotation.class));		
	}
	

	/** 
	* @Title: SaveAndClearTokenBuf 
	* @Description: create a tag for tokens in the buffer.
	* @param context   
	*/
	void SaveAndClearTokenBuf(CSentenceAnalysisReport context){
		if( this.tokenBuf.size() < 1)
			return;
		
		if(this.curBufType == CNEREntityTag.EnumNERObjectType.Unkown){
			this.tokenBuf.clear();
			return;
		}
		//get tag  from tokens
		CNEREntityTag tag = new CNEREntityTag();
		tag.NERObjectType = this.curBufType;
		
		CoreLabel tk = this.tokenBuf.pop();
		tag.End = tk.index();
		tag.EndChar = tk.endPosition();
		
		
		while(this.tokenBuf.empty() == false){
			tk = this.tokenBuf.pop();
		}
			
		tag.Start = tk.index()-1;
		tag.StartChar = tk.beginPosition();		
		
		tag.Text = NLPTool.GetTextOfTokens(this.Context.getTextTokens(0), tag.Start, tag.End);
		
		
		context.NEREntities.add(tag);
	}

}
