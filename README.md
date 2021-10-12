# TEQUILA + TempQuestions

Description
------
This repository contains the code and data for our CIKM'18 short paper. In this paper, we present TEQUILA, an enabler method
for temporal QA that can run on top of any KB-QA engine. TEQUILA has four stages. It detects if a question has temporal intent. It decomposes and rewrites the question into non-temporal sub-questions
and temporal constraints. Answers to sub-questions are then retrieved from the underlying KB-QA engine. Finally, TEQUILA uses
constraint reasoning on temporal intervals to compute final answers to the full question. Comparisons against state-of-the-art
baselines show the viability of our method. 

<center><img src="example.png"  alt="example" width=60%  /></center>

*An example of TEQUILA pipeline including the steps of "decompose and rewrite", "answer sub-questions", and "reason on time intervals".*

For more details see our paper: [TEQUILA: Temporal Question Answering over Knowledge Bases](https://arxiv.org/abs/1908.03650) and visit our project website: https://tequila.mpi-inf.mpg.de.

If you use this code, please cite:
```bibtex
@inproceedings{jia:18b,
 author = {Jia, Zhen and Abujabal, Abdalghani and Saha Roy, Rishiraj and Str\"{o}tgen, Jannik and Weikum, Gerhard},
 title = {{TEQUILA: Temporal Question Answering over Knowledge Bases}},
 booktitle = {Proceedings of the 27th ACM International Conference on Information and Knowledge Management},
 series = {CIKM '18},
 year = {2018},
 isbn = {978-1-4503-6014-2},
 location = {Torino, Italy},
 pages = {1807--1810},
 numpages = {4},
 url = {http://doi.acm.org/10.1145/3269206.3269247},
 doi = {10.1145/3269206.3269247},
 acmid = {3269247},
 publisher = {ACM},
 address = {New York, NY, USA},
 keywords = {question answering, question decomposition, temporal questions},
}
```
## Requirements

* OS: Linux system 
* Software: Java 8 as well as Python 2.7
* Package: [TreeTagger](http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/#parfiles), [HeidelTime](https://github.com/HeidelTime/heideltime), [Stanford CoreNLP 3.7.0](http://nlp.stanford.edu/software/stanford-corenlp-2016-10-31.zip)
* Underlying KB-QA systems: [AQQU](https://github.com/elmarhaussmann/aqqu) and [QUINT](https://quint.mpi-inf.mpg.de)
* Word vector model: [word2vec-GoogleNews-vectors](https://github.com/mmihaltz/word2vec-GoogleNews-vectors)

## Usage
 
The system contains two parts:
------

* Frontend: it provides an user interface and realizes the functions of detecting temporal questions, decomposing and rewriting sub-questions, and reasoning. Frontend is implemented in JAVA.
* Backend: it provides the underlying KBQA service and a plug-in for the service to answer sub-questions and retrieve dates related to candidate answers. Backend is implemented in Python.    

Backend
------
We use AQQU as the underlying KB-QA system, you should install [AQQU](https://github.com/elmarhaussmann/aqqu) first and then copy the following two files into the "data" directory. 

* temporal-predicate-pairs
* stop-words

After installing AQQU, please replace the modules with the following files:  

* query\_translator.translator.py
* query\_translator.data.py
* query\_translator.query\_candidate.py

Change the base file path in config.cfg according to your system.

Start the underlying QA service using the following command:

    python TEQUILA\_AQQU\_backend.py
 

Frontend
------
The function "QuestionAnswer" of the file "QuestionAnswer.java" in "org.tempo.testsample" provides an example to take the question with parameters as the input, access the underlying QA service, return the results including answers and other intermediated results in a JSON object.
 
The definition of the parameters are in "org.tempo.Util". You should set global configurations (CGlobalConfiguration.java) such as the gloabal source folder or the base file path, etc. according to your system. 

If you want to reproduce the system without installing AQQU, you can set the parameter "qaMode" with "OFFLINE". TEQUILA provides the sub-question answers files (TempQuestions\_allsubquestion\_aqqu.txt and TempQuestions\_allsubquestion\_quint.txt) for the questions in [TempQuestions Dataset](http://qa.mpi-inf.mpg.de/TempQuestions.zip).

The following two files required are provided in "source\dictionary" directory:

* temporal-predicate-pairs: temporal predicate pairs
* event\_dictionary\_only.txt: event dictionary
 
If you face any issues when using the code please feel free to contact Zhen Jia (zjia@swjtu.edu.cn), Rishiraj Saha Roy (rishiraj@mpi-inf.mpg.de) or Gerhard Weikum (weikum@mpi-inf.mpg.de).
