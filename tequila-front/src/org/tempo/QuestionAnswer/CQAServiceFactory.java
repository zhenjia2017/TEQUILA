package org.tempo.QuestionAnswer;

import java.io.File;

/** 
* @ClassName: CQAServiceFactory 
* @Description: the factory is to create QAservice
*  
*/
public class CQAServiceFactory {
	
	public static IQAService CreateQAService(String service){
		
		//if the service string is a http service, create online QAService
		if(service.toLowerCase().contains("http")){
			return createNetService(service);
		}else if( (new File(service)).exists()){ //if the string is a file, create offline service
			return createFileService(service);
		}else{
			return null;
		}
		
	}
	
	/** 
	* @Title: createFileService 
	* @Description: create offline service from file
	* @param serviceFile
	* @return   
	*/
	private static IQAService createFileService(String serviceFile){
		System.out.println("Create QAservice from file:" + serviceFile);
		CQAServiceOffline server = new CQAServiceOffline();
		server.ParseResultsFile(serviceFile);
		return server;
	}
	
	/** 
	* @Title: createNetService 
	* @Description: create online service
	* @param serviceAddr
	* @return   
	*/
	private static IQAService createNetService(String serviceAddr){
		System.out.println("Create QAservice by address:" + serviceAddr);
		CQAServiceOnline service = new CQAServiceOnline(serviceAddr);
		return service;
	}
	
	
}
