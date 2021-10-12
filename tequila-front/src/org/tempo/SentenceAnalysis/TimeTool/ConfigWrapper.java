package org.tempo.SentenceAnalysis.TimeTool;

import java.util.Iterator;
import java.util.Properties;

import org.tempo.Util.CGlobalConfiguration;

import java.util.Map.Entry;



/** 
* @ClassName: ConfigWrapper 
* @Description: config wrapper for heideltime instance configuration.
*  
*/
public class ConfigWrapper {
	/**
	 * 
	 */
	private static Properties properties;

	/*
	 * Constants to organize consistent access to config parameters
	 */
	public static final String DEBUG = "Debugging";
	
	public static final String CONSIDER_DATE = "considerDate";
	public static final String CONSIDER_DURATION = "considerDuration";
	public static final String CONSIDER_SET = "considerSet";
	public static final String CONSIDER_TIME = "considerTime";
	public static final String CONSIDER_TEMPONYM = "considerTemponym";
	public static final String TREETAGGERHOME = "treeTaggerHome";
	public static final String CHINESE_TOKENIZER_PATH = "chineseTokenizerPath";
	
	public static final String JVNTEXTPRO_WORD_MODEL_PATH = "word_model_path";
	public static final String JVNTEXTPRO_SENT_MODEL_PATH = "sent_model_path";
	public static final String JVNTEXTPRO_POS_MODEL_PATH = "pos_model_path";
	
	public static final String STANFORDPOSTAGGER_MODEL_PATH = "model_path";
	public static final String STANFORDPOSTAGGER_CONFIG_PATH = "config_path";
	
	public static final String HUNPOS_PATH = "hunpos_path";
	public static final String HUNPOS_MODEL_PATH = "hunpos_model_name";
	
	public static final String TYPESYSTEMHOME = "typeSystemHome";
	public static final String TYPESYSTEMHOME_DKPRO = "typeSystemHome_DKPro";
	
	public static final String UIMAVAR_DATE = "uimaVarDate";
	public static final String UIMAVAR_DURATION = "uimaVarDuration";
	public static final String UIMAVAR_LANGUAGE = "uimaVarLanguage";
	public static final String UIMAVAR_SET = "uimaVarSet";
	public static final String UIMAVAR_TEMPONYM = "uimaVarTemponym";
	public static final String UIMAVAR_TIME = "uimaVarTime";
	public static final String UIMAVAR_TYPETOPROCESS = "uimaVarTypeToProcess";
	public static final String UIMAVAR_CONVERTDURATIONS = "ConvertDurations";


	/**
	 * 
	 */
	private ConfigWrapper() {
	}

	/**
	 * Gets config parameter identified by <code>key</code>
	 * 
	 * @param key
	 *            Identifier of config parameter
	 * @return Config paramter
	 */
	public static String get(String key) {
		if (properties == null) {
			return null;
		}

		return properties.getProperty(key);
	}
	
	/**
	 * Checks whether config was already initialized
	 * 
	 * @return
	 */
	public static boolean isInitialized() {
		return properties != null;
	}

	/**
	 * Sets properties once
	 * 
	 * @param prop
	 *            Properties
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public static void setProps(Properties prop) {
		properties = prop;
		
		Iterator propIt = properties.entrySet().iterator();
		while(propIt.hasNext()) {
			Entry<String, String> entry = (Entry<String, String>) propIt.next();
			
			properties.setProperty(entry.getKey(), entry.getValue().trim());
		}
	}
	
	
//	private static void InitConfig(){
//		//Config.in
//		properties.setProperty(ConfigWrapper.TYPESYSTEMHOME, "desc//type//HeidelTime_TypeSystem.xml");
//		properties.setProperty(ConfigWrapper.TYPESYSTEMHOME_DKPRO, "desc//type//DKPro_TypeSystem.xml");
//		properties.setProperty(ConfigWrapper.UIMAVAR_DATE, "Date");
//		properties.setProperty(ConfigWrapper.UIMAVAR_DURATION, "Duration");
//		properties.setProperty(ConfigWrapper.UIMAVAR_LANGUAGE, "Language");
//		properties.setProperty(ConfigWrapper.UIMAVAR_SET, "Set");
//		properties.setProperty(ConfigWrapper.UIMAVAR_TIME, "Time");
//		properties.setProperty(ConfigWrapper.UIMAVAR_TEMPONYM, "Temponym");
//		properties.setProperty(ConfigWrapper.UIMAVAR_TYPETOPROCESS, "Type");
//		properties.setProperty(ConfigWrapper.CONSIDER_DATE, "true");
//		properties.setProperty(ConfigWrapper.CONSIDER_DURATION, "true");
//		properties.setProperty(ConfigWrapper.CONSIDER_SET, "true");
//		properties.setProperty(ConfigWrapper.CONSIDER_TIME, "true");
//		properties.setProperty(ConfigWrapper.CONSIDER_TEMPONYM, "true");
//		
//		 //the default is for windows. if use on linux,please change the TreeTagger path.
//		properties.setProperty(ConfigWrapper.TREETAGGERHOME, CGlobalConfiguration.HeilderTime_TREETAGGERHOME); 
//		properties.setProperty(ConfigWrapper.STANFORDPOSTAGGER_MODEL_PATH, "");
//		properties.setProperty(ConfigWrapper.STANFORDPOSTAGGER_CONFIG_PATH, "");	
//		properties.setProperty(ConfigWrapper.STANFORDPOSTAGGER_CONFIG_PATH, "");
//
//	}
	
}