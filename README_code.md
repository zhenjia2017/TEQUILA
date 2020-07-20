# TEQUILA Temporal Question Answering System

This is the code accompanying the submission ["TEQUILA: Temporal Question Answering over Knowledge Bases"](https://www.mpi-inf.mpg.de/departments/databases-and-information-systems/research/question-answering/)

## Requirements

* OS: Linux system 
* Software: Java 8 as well as Python 2.7
* Package: [TreeTagger](http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/#parfiles), [HeidelTime](https://github.com/HeidelTime/heideltime), [Stanford CoreNLP 3.7.0](http://nlp.stanford.edu/software/stanford-corenlp-2016-10-31.zip)
* Underlying KB-QA systems: [AQQU](https://github.com/elmarhaussmann/aqqu) and [QUINT](https://gate.d5.mpi-inf.mpg.de/quint/quint)
* Word vector model: [word2vec-GoogleNews-vectors](https://github.com/mmihaltz/word2vec-GoogleNews-vectors)

## Usage
 
###The system contains two parts: 

* Frontend: it provides user interface and realizes the functions of detecting temporal questions, decomposing and rewriting sub-questions, and reasoning. Frontend is implemented in JAVA.
* Backend: it provides QA service for the frontend, including answering sub-questions and retrieving dates related to candidate answers from knowledge base. Backend is implemented in Python.    

###Backend

The backend QA service is based on the underlying KB-QA systems and as a plug-in for them. 

If you use AQQU as the underlying KB-QA system, you should install [AQQU](https://github.com/ad-freiburg/aqqu/blob/master/QUICKSTART.md) first and then copy the following two files into "data" directory. 

* temporal-predicate-pairs
* stop-words

After installing AQQU, replace the modules with the following files:  

* query\_translator.translator.py
* query\_translator.data.py
* query\_translator.query\_candidate.py

Change the base file path in config.cfg according to your system.

Start backend QA service using the following command:

Python TEQUILA\_AQQU\_backend.py
 

###Frontend

You can use TEQUILA through "QuestionAnswer.java" in "org.tempo.testsample". You can call the function "QuestionAnswer" in the class, with the input of question and parameters. The answers are in a JSON object.
 
The definition of the parameters are in "org.tempo.Util". You should set golbal configurations (CGlobalConfiguration.java) such as the gloabal source folder or the base file path, etc. 

If you want to reproduce the system without installing AQQU underlying KB-QA system, you can set the parameter "qaMode" with "OFFLINE". TEQUILA provides the sub-question answers files (TempQuestions\_allsubquestion\_aqqu.txt and TempQuestions\_allsubquestion\_quint.txt) instead of the underlying QA service to answer questions. But in this option, TEQUILA only can answer the questions in [TempQuestions Dataset](http://qa.mpi-inf.mpg.de/TempQuestions.zip), which is also provided in the source diractory.

The following two files required are provided in "source\dictionary" directory:

* temporal-predicate-pairs: temporal predicate pairs
* event\_dictionary\_only.txt: event dictionary

## Contact us

If you face any issues when using the code please feel free to contact [Zhen Jia](zjia@swjtu.edu.cn), [Abdalghani Abujabal](abujabal@mpi-inf.mpg.de) or [Rishiraj Saha Roy](rishiraj@mpi-inf.mpg.de).







