package org.tempo.SentenceAnalysis.TimeTool;

import java.util.*;

import de.unihd.dbs.heideltime.standalone.*;

import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.XMLInputSource;
import org.tempo.Util.CGlobalConfiguration;

import de.unihd.dbs.heideltime.standalone.components.JCasFactory;
import de.unihd.dbs.heideltime.standalone.components.ResultFormatter;
import de.unihd.dbs.heideltime.standalone.components.PartOfSpeechTagger;
import de.unihd.dbs.heideltime.standalone.components.impl.AllLanguagesTokenizerWrapper;
import de.unihd.dbs.heideltime.standalone.components.impl.HunPosTaggerWrapper;
import de.unihd.dbs.heideltime.standalone.components.impl.IntervalTaggerWrapper;
import de.unihd.dbs.heideltime.standalone.components.impl.JCasFactoryImpl;
import de.unihd.dbs.heideltime.standalone.components.impl.JVnTextProWrapper;
import de.unihd.dbs.heideltime.standalone.components.impl.StanfordPOSTaggerWrapper;
import de.unihd.dbs.heideltime.standalone.components.impl.TimeMLResultFormatter;
import de.unihd.dbs.heideltime.standalone.components.impl.TreeTaggerWrapper;
import de.unihd.dbs.heideltime.standalone.components.impl.UimaContextImpl;
import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.HeidelTime;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import de.unihd.dbs.uima.annotator.intervaltagger.IntervalTagger;
import de.unihd.dbs.uima.types.heideltime.Dct;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import de.unihd.dbs.uima.types.heideltime.Timex3Interval;

/** 
* @ClassName: HeildeTimeWrapper 
* @Description: Wrapper heildetime instance.
*  
*/
public class HeildeTimeWrapper extends TimeTagProcessBase{

	/**
	 * Used document type
	 */
	private DocumentType documentType = DocumentType.NEWS;

	/**
	 * HeidelTime instance
	 */
	private HeidelTime heidelTime ;

	/**
	 * Type system description of HeidelTime
	 */
	private JCasFactory jcasFactory;

	/**
	 * Used language
	 */
	private Language language = Language.ENGLISH;

	/**
	 * output format
	 */
	private OutputType outputType = OutputType.TIMEML;

	/**
	 * POS tagger
	 */
	private POSTagger posTagger = POSTagger.TREETAGGER;

	/**
	 * Whether or not to do Interval Tagging
	 */
	private Boolean doIntervalTagging = false;

	/**
	 * Logging engine
	 */
	private static Logger logger = Logger.getLogger("HeidelTimeWrapper");

	

	

	/**
	 * Method that initializes all vital prerequisites, including POS Tagger
	 * 
	 * @param language	Language to be processed with this copy of HeidelTime
	 * @param typeToProcess	Domain type to be processed
	 * @param outputType	Output type
	 * @param configPath	Path to the configuration file for HeidelTimeStandalone
	 * @param posTagger		POS Tagger to use for preprocessing
	 * @param doIntervalTagging	Whether or not to invoke the IntervalTagger
	 */
	
	private void initializeHeideTime() {	
	logger.log(Level.INFO, "HeidelTimeStandalone initialized with language " + this.language.getName());
		this.language = Language.ENGLISH;
		this.documentType = DocumentType.NEWS;
		this.outputType = OutputType.TIMEML;
		this.posTagger = POSTagger.TREETAGGER;

		
		try {
			heidelTime = new HeidelTime();
//			heidelTime.initialize(new UimaContextImpl(language, typeToProcess, CLISwitch.VERBOSITY2.getIsActive()));
			heidelTime.initialize(new UimaContextImpl(Language.ENGLISH, this.documentType, false));
			logger.log(Level.INFO, "HeidelTime initialized");
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "HeidelTime could not be initialized");
		}

		// Initialize JCas factory -------------
		logger.log(Level.FINE, "Initializing JCas factory...");
		try {
			java.net.URL url= this.getClass().getClassLoader().getResource(Config.get(Config.TYPESYSTEMHOME));
			XMLInputSource ar = new XMLInputSource(url);
			
			TypeSystemDescription[] descriptions = new TypeSystemDescription[] {
					UIMAFramework.getXMLParser().parseTypeSystemDescription(ar) };
			jcasFactory = new JCasFactoryImpl(descriptions);
			logger.log(Level.INFO, "JCas factory initialized");
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "JCas factory could not be initialized");
		}
	}
	
	private void InitConfig(){
		Properties properties = new Properties();
		properties.setProperty(ConfigWrapper.TYPESYSTEMHOME, "desc/type/HeidelTime_TypeSystem.xml");
		properties.setProperty(ConfigWrapper.TYPESYSTEMHOME_DKPRO, "desc/type/DKPro_TypeSystem.xml");		
		
		properties.setProperty(ConfigWrapper.UIMAVAR_DATE, "Date");
		properties.setProperty(ConfigWrapper.UIMAVAR_DURATION, "Duration");
		properties.setProperty(ConfigWrapper.UIMAVAR_LANGUAGE, "Language");
		properties.setProperty(ConfigWrapper.UIMAVAR_SET, "Set");
		properties.setProperty(ConfigWrapper.UIMAVAR_TIME, "Time");
		properties.setProperty(ConfigWrapper.UIMAVAR_TEMPONYM, "Temponym");
		properties.setProperty(ConfigWrapper.UIMAVAR_TYPETOPROCESS, "Type");
		properties.setProperty(ConfigWrapper.CONSIDER_DATE, "true");
		properties.setProperty(ConfigWrapper.CONSIDER_DURATION, "true");
		properties.setProperty(ConfigWrapper.CONSIDER_SET, "true");
		properties.setProperty(ConfigWrapper.CONSIDER_TIME, "true");
		properties.setProperty(ConfigWrapper.CONSIDER_TEMPONYM, "true");
		properties.setProperty(ConfigWrapper.TREETAGGERHOME, CGlobalConfiguration.HeilderTime_TREETAGGERHOME); //
		//properties.setProperty(ConfigWrapper.STANFORDPOSTAGGER_MODEL_PATH, "D://TimeIdentity//QuestionExtract//lib//stanford-postagger-2016-10-31//models//english-bidirectional-distsim.tagger");
		properties.setProperty(ConfigWrapper.STANFORDPOSTAGGER_CONFIG_PATH, "");	
		properties.setProperty(ConfigWrapper.STANFORDPOSTAGGER_MODEL_PATH, "");
	
		Config.setProps(properties);
		
	}
	
	public void Initialize(){
		InitConfig();
		initializeHeideTime();
		
	}
	
	
	/**
	 * Runs the IntervalTagger on the JCAS object.
	 * @param jcas jcas object
	 */
	private void runIntervalTagger(JCas jcas) {
		logger.log(Level.FINEST, "Running Interval Tagger...");
		Integer beforeAnnotations = jcas.getAnnotationIndex().size();
		
		// Prepare the options for IntervalTagger's execution
		Properties settings = new Properties();
		settings.put(IntervalTagger.PARAM_LANGUAGE, language.getResourceFolder());
		settings.put(IntervalTagger.PARAM_INTERVALS, true);
		settings.put(IntervalTagger.PARAM_INTERVAL_CANDIDATES, false);
		
		// Instantiate and process with IntervalTagger
		IntervalTaggerWrapper iTagger = new IntervalTaggerWrapper();
		iTagger.initialize(settings);
		iTagger.process(jcas);
		
		// debug output
		Integer afterAnnotations = jcas.getAnnotationIndex().size();
		logger.log(Level.FINEST, "Annotation delta: " + (afterAnnotations - beforeAnnotations));
	}

	/**
	 * Provides jcas object with document creation time if
	 * <code>documentCreationTime</code> is not null.
	 * 
	 * @param jcas
	 * @param documentCreationTime
	 * @throws DocumentCreationTimeMissingException
	 *             If document creation time is missing when processing a
	 *             document of type {@link DocumentType#NEWS}.
	 */
	private void provideDocumentCreationTime(JCas jcas,
			Date documentCreationTime)
			throws DocumentCreationTimeMissingException {
		if (documentCreationTime == null) {
			// Document creation time is missing
			if (documentType == DocumentType.NEWS) {
				// But should be provided in case of news-document
				throw new DocumentCreationTimeMissingException();
			}
			if (documentType == DocumentType.COLLOQUIAL) {
				// But should be provided in case of colloquial-document
				throw new DocumentCreationTimeMissingException();
			}
		} else {
			// Document creation time provided
			// Translate it to expected string format
			SimpleDateFormat dateFormatter = new SimpleDateFormat(
					"yyyy.MM.dd'T'HH:mm");
			String formattedDCT = dateFormatter.format(documentCreationTime);

			// Create dct object for jcas
			Dct dct = new Dct(jcas);
			dct.setValue(formattedDCT);

			dct.addToIndexes();
		}
	}

	/**
	 * Establishes preconditions for jcas to be processed by HeidelTime
	 * 
	 * @param jcas
	 */
	private void establishHeidelTimePreconditions(JCas jcas) {
		// Token information & sentence structure
		establishPartOfSpeechInformation(jcas);
	}

	/**
	 * Establishes part of speech information for cas object.
	 * 
	 * @param jcas
	 */
	private void establishPartOfSpeechInformation(JCas jcas) {
		logger.log(Level.FINEST, "Establishing part of speech information...");

		PartOfSpeechTagger partOfSpeechTagger = null;
		Properties settings = new Properties();
		switch (language) {
			case ARABIC:
				if(POSTagger.NO.equals(posTagger)) {
					partOfSpeechTagger = new AllLanguagesTokenizerWrapper();
					logger.log(Level.INFO, "Be aware that you use the AllLanguagesTokenizer instead of specific preprocessing for Arabic. "
							+ "Thus, tagging results might be very different (and worse).");
				} else {
					partOfSpeechTagger = new StanfordPOSTaggerWrapper();
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_ANNOTATE_TOKENS, true);
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_ANNOTATE_SENTENCES, true);
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_ANNOTATE_POS, true);
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_MODEL_PATH, Config.get(ConfigWrapper.STANFORDPOSTAGGER_MODEL_PATH));
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_CONFIG_PATH, Config.get(ConfigWrapper.STANFORDPOSTAGGER_CONFIG_PATH));
				}
				break;
			case VIETNAMESE:
				if(POSTagger.NO.equals(posTagger)) {
					partOfSpeechTagger = new AllLanguagesTokenizerWrapper();
					logger.log(Level.INFO, "Be aware that you use the AllLanguagesTokenizer instead of specific preprocessing for Vietnamese. "
							+ "Thus, tagging results might be very different (and worse).");
				} else {
					partOfSpeechTagger = new JVnTextProWrapper();
					settings.put(PartOfSpeechTagger.JVNTEXTPRO_ANNOTATE_TOKENS, true);
					settings.put(PartOfSpeechTagger.JVNTEXTPRO_ANNOTATE_SENTENCES, true);
					settings.put(PartOfSpeechTagger.JVNTEXTPRO_ANNOTATE_POS, true);
					settings.put(PartOfSpeechTagger.JVNTEXTPRO_WORD_MODEL_PATH, Config.get(ConfigWrapper.JVNTEXTPRO_WORD_MODEL_PATH));
					settings.put(PartOfSpeechTagger.JVNTEXTPRO_SENT_MODEL_PATH, Config.get(ConfigWrapper.JVNTEXTPRO_SENT_MODEL_PATH));
					settings.put(PartOfSpeechTagger.JVNTEXTPRO_POS_MODEL_PATH, Config.get(ConfigWrapper.JVNTEXTPRO_POS_MODEL_PATH));
				}
				break;
			case CROATIAN:
				if(POSTagger.NO.equals(posTagger)) {
					partOfSpeechTagger = new AllLanguagesTokenizerWrapper();
					logger.log(Level.INFO, "Be aware that you use the AllLanguagesTokenizer instead of specific preprocessing for Croatian. "
							+ "Thus, tagging results might be very different (and worse).");
				} else {
					partOfSpeechTagger = new HunPosTaggerWrapper();
					settings.put(PartOfSpeechTagger.HUNPOS_LANGUAGE, language);
					settings.put(PartOfSpeechTagger.HUNPOS_ANNOTATE_TOKENS, true);
					settings.put(PartOfSpeechTagger.HUNPOS_ANNOTATE_POS, true);
					settings.put(PartOfSpeechTagger.HUNPOS_ANNOTATE_SENTENCES, true);
					settings.put(PartOfSpeechTagger.HUNPOS_MODEL_PATH, Config.get(ConfigWrapper.HUNPOS_MODEL_PATH));
				}
				break;
			default:
				if(POSTagger.STANFORDPOSTAGGER.equals(posTagger)) {
					partOfSpeechTagger = new StanfordPOSTaggerWrapper();
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_ANNOTATE_TOKENS, true);
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_ANNOTATE_SENTENCES, true);
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_ANNOTATE_POS, true);
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_MODEL_PATH, Config.get(ConfigWrapper.STANFORDPOSTAGGER_MODEL_PATH));
					settings.put(PartOfSpeechTagger.STANFORDPOSTAGGER_CONFIG_PATH, Config.get(ConfigWrapper.STANFORDPOSTAGGER_CONFIG_PATH));
				} else if(POSTagger.TREETAGGER.equals(posTagger)) {
					partOfSpeechTagger = new TreeTaggerWrapper();
					settings.put(PartOfSpeechTagger.TREETAGGER_LANGUAGE, language);
					settings.put(PartOfSpeechTagger.TREETAGGER_ANNOTATE_TOKENS, true);
					settings.put(PartOfSpeechTagger.TREETAGGER_ANNOTATE_SENTENCES, true);
					settings.put(PartOfSpeechTagger.TREETAGGER_ANNOTATE_POS, true);
					//settings.put(PartOfSpeechTagger.TREETAGGER_IMPROVE_GERMAN_SENTENCES, (language == Language.GERMAN));
					//settings.put(PartOfSpeechTagger.TREETAGGER_CHINESE_TOKENIZER_PATH, Config.get(ConfigWrapper.CHINESE_TOKENIZER_PATH));
				} else if(POSTagger.HUNPOS.equals(posTagger)) {
					partOfSpeechTagger = new HunPosTaggerWrapper();
					settings.put(PartOfSpeechTagger.HUNPOS_LANGUAGE, language);
					settings.put(PartOfSpeechTagger.HUNPOS_ANNOTATE_TOKENS, true);
					settings.put(PartOfSpeechTagger.HUNPOS_ANNOTATE_POS, true);
					settings.put(PartOfSpeechTagger.HUNPOS_ANNOTATE_SENTENCES, true);
					settings.put(PartOfSpeechTagger.HUNPOS_MODEL_PATH, Config.get(ConfigWrapper.HUNPOS_MODEL_PATH));
				} else if(POSTagger.NO.equals(posTagger)) {
					partOfSpeechTagger = new AllLanguagesTokenizerWrapper();
					logger.log(Level.INFO, "Be aware that you use the AllLanguagesTokenizer instead of specific preprocessing for the selected language. "
							+ "If proper preprocessing for the specified language (." + language.getName() + ") is available, this might results in better "
									+ "temporal tagging quality.");
				} else {
					logger.log(Level.FINEST, "Sorry, but you can't use that tagger.");
				}
		}
		partOfSpeechTagger.initialize(settings);
		partOfSpeechTagger.process(jcas);
		partOfSpeechTagger.reset();

		logger.log(Level.FINEST, "Part of speech information established");
	}

	private ResultFormatter getFormatter() {
//		if (outputType.toString().equals("xmi")){
//			return new XMIResultFormatter();
//		} else {
//			return new TimeMLResultFormatter();
//		}
	
		return new TimeMLResultFormatter();
	}
	
	

	/**
	 * Processes document with HeidelTime
	 * 
	 * @param document
	 * @param documentCreationTime
	 *            Date when document was created - especially important if
	 *            document is of type {@link DocumentType#NEWS}
	 * @return Annotated document
	 * @throws DocumentCreationTimeMissingException
	 *             If document creation time is missing when processing a
	 *             document of type {@link DocumentType#NEWS}
	 */
	public String Process(String document){
	    Date documentCreationTime = this.getBaselineDate();
		ResultFormatter resultFormatter = this.getFormatter();
			
		logger.log(Level.INFO, "Processing started");

		// Generate jcas object ----------
		logger.log(Level.FINE, "Generate CAS object");
		JCas jcas = null;
		try {
			jcas = jcasFactory.createJCas();
			jcas.setDocumentText(document);
			logger.log(Level.FINE, "CAS object generated");
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "Cas object could not be generated");
		}

		// Process jcas object -----------
		try {
			logger.log(Level.FINER, "Establishing preconditions...");
			provideDocumentCreationTime(jcas, documentCreationTime);
			establishHeidelTimePreconditions(jcas);
			logger.log(Level.FINER, "Preconditions established");

			heidelTime.process(jcas);

			logger.log(Level.INFO, "Processing finished");
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "Processing aborted due to errors");
		}

		// process interval tagging ---
		if(doIntervalTagging)
			runIntervalTagger(jcas);
		
		// Process results ---------------
		logger.log(Level.FINE, "Formatting result...");
		// PrintAnnotations.printAnnotations(jcas.getCas(), System.out);
		String result = null;
		try {
			result = resultFormatter.format(jcas);
			GenerateTimeTag(jcas,document);
			logger.log(Level.INFO, "Result formatted");
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "Result could not be formatted");
		}

		return result;
	}
	
	private void GenerateTimeTag(JCas jcas,String document){
		this.TempralTags.clear();
		
		FSIterator iterIntervals = jcas.getAnnotationIndex(Timex3Interval.type).iterator();
		while(iterIntervals.hasNext()) {
			Timex3Interval t = (Timex3Interval) iterIntervals.next();
			//System.out.println("!!!!!!" + t.getTypeIndexID() + " ; " + t.getTimexType()); 
			if(t.getTypeIndexID() != 31) //not TEMPONYM
				this.TempralTags.add(CTempralTag.ConvertToTempralTag(t, document));
		}
		
		//because interval tag always contains a timex tag, retrieve tag information from the timex tag
		FSIterator iterTimex = jcas.getAnnotationIndex(Timex3.type).iterator();
		
		while(iterTimex.hasNext()) {
			Timex3 t = (Timex3) iterTimex.next();
			//System.out.println("!!!!!!" + t.getTypeIndexID() + " ; " + t.getTimexType()); 
			CTempralTag tag = IsOverlapTag(t,this.TempralTags);
			if(  tag == null){
				if(t.getTypeIndexID() != 31) //not TEMPONYM
					this.TempralTags.add(CTempralTag.ConvertToTempralTag(t, document));
			}else{
				tag.SubType = t.getTimexType();
				if(tag.BeginTime.length() < 1 || tag.EndTime.length() < 1) //if event occur in only one day.
					tag.Time = t.getTimexValue();
			}
		}
}

CTempralTag IsOverlapTag(Timex3 srcTag, List<CTempralTag> taglist){
	if( taglist.size() < 1) return null;
	int bPos= srcTag.getBegin();
	int ePos = srcTag.getEnd();
	
	for(CTempralTag tag : taglist){
		if( bPos >= tag.StartChar && ePos<=tag.EndChar)
			return tag;
	}
	return null;
}
	
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
	}

	public final POSTagger getPosTagger() {
		return posTagger;
	}

	public final void setPosTagger(POSTagger posTagger) {
		this.posTagger = posTagger;
	}

	
	
	
	/**
	 * test the heildetime wrapper
	 * @param args
	 */
	public static void main(String[] args) {
		
			// Run HeidelTime

		try {
			
			// double-newstring should not be necessary, but without this, it's not running on Windows (?)
			String []exLines = {"before he was elected president in the Summer of 1912?",
								"When did you get the doc in last month. ",
								"who is the president of USA in 1997",
								"How will you know during Al-Wadiah War!",
								"If the game start at 9:00PM?",
								"Start now or next Sunday."
			};			
			HeildeTimeWrapper standalone = new HeildeTimeWrapper();
			standalone.Initialize();
			
			logger.log(Level.INFO, "start to analyze the sentences.....");
			
			for(String inString:exLines){
				String outInfo = standalone.Process(inString);
				System.out.println(outInfo);
				
				logger.log(Level.INFO, "print tag.....");
				for(CTempralTag tag: standalone.TempralTags){
					System.out.println(tag.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
