package org.tempo.SentenceAnalysis;

import java.util.HashMap;


/** 
* @ClassName: CNEREntityTag 
* @Description: NLP NER Entitiy Tag
*  
*/
public class CNEREntityTag extends CLabelTag{

	public enum EnumNERObjectType{Unkown, Location, Person,Organization, Money, Percent, Date, Time, MISC};

	public EnumNERObjectType NERObjectType;
	
	/** _enumValueMap: this map is used to convert string to enum variable */  
	private static HashMap<String, EnumNERObjectType> _enumValueMap;
	
	public CNEREntityTag(){
		super(EnumTagType.NEREntity);
	}
	
	/** 
	* @Title: CreateValueMap 
	* @Description: save all enum name string to a map
	*/
	private static void CreateValueMap(){
		if(_enumValueMap == null){
			_enumValueMap = new HashMap<String, EnumNERObjectType>();
			
			for(EnumNERObjectType v: EnumNERObjectType.values()){
				if(v != EnumNERObjectType.Unkown){
					_enumValueMap.put(v.toString().toLowerCase(), v);
				}
			}
		}
	}
	
	/** 
	* @Title: getNERObjectType 
	* @Description: get the corresponding Enum value from name string
	* @param name
	* @return   
	*/
	public static  EnumNERObjectType getNERObjectType(String name){
		if( name == null || name.length() < 1)
			return EnumNERObjectType.Unkown;
		
		if(_enumValueMap == null)
			CreateValueMap();
		
		
		String strLowcaseName = name.trim().toLowerCase();
		return _enumValueMap.getOrDefault(strLowcaseName, EnumNERObjectType.Unkown);
		
	}


}
