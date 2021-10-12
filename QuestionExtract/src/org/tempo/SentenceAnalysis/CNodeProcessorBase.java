package org.tempo.SentenceAnalysis;

/** 
* @ClassName: CNodeProcessorBase 
* @Description: Process node base. process the context ,and then send the context to next process node. 
* @param <T> 
*/
public abstract class CNodeProcessorBase<T /*extends INodeContext*/> implements INodeProcessor<T> {

	INodeProcessor<T> nextProcessor;
	
	protected T Context;
	
	protected abstract void NodeProcess(T context);
	
	@Override
	public void Process(T context){
		//if this node can process the context, deal with the context
		if(this.CanProcess(context)){
			this.Context = context;
			NodeProcess(context);
		}
		//if no error , send the context to next process node  handle
		if( this.nextProcessor != null && context!=null /*&& context.GetErrorCode()>=0*/){
			this.nextProcessor.Process(context);
		}
	}
	
	/** 
	* <p>Title: </p> 
	* <p>Description: construct the class with next process node handle </p> 
	* @param next 
	*/
	public CNodeProcessorBase(INodeProcessor<T> next){
		this.nextProcessor = next;
	}

	/** 
	* @Title: CanProcess 
	* @Description: check if the process can deal with the context
	* @param context
	* @return   
	*/
	protected  boolean CanProcess(T context){
		return context != null;
	}
}
