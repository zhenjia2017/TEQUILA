/**
 * A class that contains the detailed information on all of the command line interface
 * switches entertained by HeidelTimeStandalone.
 */
package org.tempo.Application;

import java.util.Date;



public enum CLISwitch {
	QAServiceOrFile	("QA service onlline or Offline answers package file", "-qa"),
	TreeTaggerHome	("TreeTagger Home directory", "-t"),
	EventDictionary	("Event dictionary file path", "-e"),
	PairNameDictionary	("Pair names dictionary", "-p"),
	QuestionsFileForQA ("Questions for QA System (offline).", "-qf"),
	CurrentDate	("Date for current in question (default:2013-1-1)", "-c"),
	AlwaysAddNoDateAnswer		("get answers without date ", "-nd"),
	OnlyLatestAnswer ("Get laest answer only in time filting.", "-l"),
	AnserInRankLevel	("Find answer of data rank level ", "-r"),
	;
	
	private boolean hasFollowingValue = false;
	private boolean isActive = false;
	private String name;
	private String switchString;
	private Object value = null;
	private Object defaultValue;
	
	/**
	 * Constructor for switches that have a default value
	 * @param name
	 * @param switchString
	 * @param defaultValue
	 */
	CLISwitch(String name, String switchString, Object defaultValue) {
		this.hasFollowingValue = true;
		this.name = name;
		this.switchString = switchString;
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Constructor for switches that don't have a default value
	 * @param name
	 * @param switchString
	 */
	CLISwitch(String name, String switchString) {
		this.hasFollowingValue = false;
		this.name = name;
		this.switchString = switchString;
	}
	
	public static CLISwitch getEnumFromSwitch(String cliSwitch) {
		for(CLISwitch s : CLISwitch.values()) {
			if(s.getSwitchString().equals(cliSwitch)) {
				return s;
			}
		}
		return null;
	}
	
	/*
	 * getters/setters of private attributes
	 */
	
	public void setValue(String val) {
		value = val;
		isActive = true;
	}
	
	/**
	 * if this switch is supposed to have a value after it, spit out the saved value 
	 * or the default value if the value is unset. if it's not supposed to have a value,
	 * return null
	 * @return	String containing a value for the switch
	 */
	public Object getValue() {
		if(hasFollowingValue) {
			if(value != null)
				return value;
			else
				return defaultValue;
		} else {
			return null;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	public String getSwitchString() {
		return switchString;
	}
	
	public boolean getHasFollowingValue() {
		return hasFollowingValue;
	}
	
	public boolean getIsActive() {
		return isActive;
	}
}
