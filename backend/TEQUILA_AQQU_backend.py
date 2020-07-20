"""

This is the question answer service of AQQU which accepts three kinds of questions such as subq1, subq2 and tempo.ans, and 
return answers, dates and other results which are used in reasoning stage or for result analysis.


""" 
 
import os
import redis
import urlparse
from werkzeug.wrappers import Request, Response
from werkzeug.routing import Map, Rule
from werkzeug.exceptions import HTTPException, NotFound
from werkzeug.wsgi import SharedDataMiddleware
from werkzeug.utils import redirect
from jinja2 import Environment, FileSystemLoader

import string
import logging
import globals
import scorer_globals
import sys
from query_translator.translator import QueryTranslator
import backend
import json
import codecs
import collections
from query_translator.data import read_temporal_predicate_from, read_temporal_predicate_to, read_stop_words
import gensim
import numpy
import scipy
import re

import operator


def is_stop_word(word):
    """
    Check if a word is a stop word.
    :param a word
    :return 1 or 0  
    """
    if word in stop_words:
        return 1
    else:
        return 0
    
    

def avg_feature_vector(words, model, num_features):
    """
    Average all words vectors in a given paragraph.
    :param words, model and number of features
    :return average vector of words  
    """
    featureVec = numpy.zeros((num_features,), dtype="float32")
    nwords = 0

    #list containing names of words in the vocabulary
    index2word_set = set(model.index2word) 
    for word in words:
        if word in index2word_set:
            nwords = nwords+1
            featureVec = numpy.add(featureVec, model[word])

    if(nwords>0):
        featureVec = numpy.divide(featureVec, nwords)
    return featureVec

def predicate_to_word(predicate):
    """
    Convert predicate to words.
    :param predicate
    :return word 
    """
    word = predicate.replace('.',' ')
    word = word.replace('_',' ')
    return word

def similarity_predicate(predicate_1,predicate_2):
    """
    Compute the similarity between two predicates using average method.
    :param two predicates
    :return similarity 
    """
    
    sentence_1 = predicate_to_word(predicate_1)  
    sentence_2 = predicate_to_word(predicate_2) 
    
    sentence_1_list = sentence_1.split()
    sentence_2_list = sentence_2.split()
    
    for x in sentence_1_list:
        if is_stop_word(x):
            
            sentence_1_list.remove(x)
            
    for y in sentence_2_list:
        if is_stop_word(y):
            
            sentence_2_list.remove(y) 
    
    sentence_1_avg_vector = avg_feature_vector(sentence_1_list, model=model, num_features=300)


    sentence_2_avg_vector = avg_feature_vector(sentence_2_list, model=model, num_features=300)
    
    pre1_pre2_similarity =  1 - scipy.spatial.distance.cosine(sentence_1_avg_vector,sentence_2_avg_vector)
  
    return pre1_pre2_similarity

def get_question_entity_relation(sparql_query):
    
    question_entity = ''
    relation = ''
    first_predicate = ''
    second_predicate = ''
    s = sparql_query.split('\n')
    predicate_line = s[len(s)-3]  
    p =  predicate_line.split(' ')
        # get the predicate of sparql
    predicate = p[2].replace('fb:','')
    
    if len(s) < 6:
            #non CVT
            #PREFIX fb: <http://rdf.freebase.com/ns/>
            #SELECT DISTINCT ?0 ?0name where {
            #fb:m.0zd6 fb:computer.computer.introduced ?0 .
            #FILTER (?0 != fb:m.0zd6) 
            #} LIMIT 300
            
        question_entity_line = s[len(s)-2].rstrip()
        if question_entity_line.find('FILTER') >= 0:
            match = re.findall("fb:.*",question_entity_line)
            question_entity = match[0].replace('fb:','')
            question_entity = question_entity.replace(')','')
        
            
        relation = predicate
            
    if len(s) == 6:
            #CVT
            #PREFIX fb: <http://rdf.freebase.com/ns/>
            #SELECT DISTINCT ?1 where {
            #fb:m.027ddp fb:sports.pro_athlete.teams ?0 .
            #?0 fb:sports.sports_team_roster.team ?1 .
            #FILTER (?1 != fb:m.027ddp) 
            #} LIMIT 300
        
        question_entity_line = s[len(s)-2].rstrip()
        
        if question_entity_line.find('FILTER') >= 0:
            match = re.findall("fb:.*",question_entity_line)
            question_entity = match[0].replace('fb:','')
            question_entity = question_entity.replace(')','')
        
        
        predicate_line = s[len(s)-4]
        if predicate_line.find('fb:') >= 0:
            p =  predicate_line.split(' ')
        # get the predicate of sparql
            first_predicate = p[2].replace('fb:','')
            relation = first_predicate + ' ' + predicate
        else:    
            relation = predicate
        

    if len(s) == 7:
            #PREFIX fb: <http://rdf.freebase.com/ns/>
            #SELECT DISTINCT ?1 where {
            #fb:m.0c8jw fb:film.film_character.portrayed_in_films ?0 .
            #?0 fb:film.performance.film fb:m.031hcx .
            #?0 fb:film.performance.actor ?1 
            #FILTER (?1 != fb:m.031hcx && ?1 != fb:m.0c8jw) 
            #} LIMIT 300
        
        question_entity_line = s[len(s)-2].rstrip()
        if question_entity_line.find('&&') >= 0:
             filter = question_entity_line.split('&&')
             question_entity_1 = filter[0].split(':')[1].rstrip()
             question_entity_2 = filter[1].split(':')[1].replace(')','')
             
             question_entity = question_entity_1 + ' ' + question_entity_2
        
        
        
        first_predicate_line = s[len(s)-5]  #find the first predicate line of SPARQL
        second_predicate_line = s[len(s)-4] #find the second predicate line of SPARQL
        if first_predicate_line.find('fb:') >= 0:    
            p =  first_predicate_line.split(' ')
        # get the predicate of sparql
            first_predicate = p[2].replace('fb:','')
        
        if second_predicate_line.find('fb:') >= 0:
            p =  second_predicate_line.split(' ')
        # get the predicate of sparql
            second_predicate = p[2].replace('fb:','') 
        
        if len(first_predicate) > 0:
            if len(second_predicate) > 0: 
                if predicate.find(second_predicate) < 0:
                    relation = first_predicate + ' ' + second_predicate + ' ' + predicate
                else: 
                    relation = first_predicate + ' ' + predicate
        elif len(second_predicate) > 0:
            if predicate.find(second_predicate) < 0:
                relation = second_predicate + ' ' + predicate
            else:
                relation = predicate
        

    return {'question_entity':question_entity,'predicate':predicate,'relation':relation}

def aqqu_answer(query):
    """
    Answer questions using AQQU directly. If a question is a simple question or not a temporal question, it is answered by
    AQQU directly.
    :param utterance
    :return a list of rank 1 candidate results from AQQU including SPARQL query, candidate answer and predicate 
    """
    
    answer_dic = {}
    answer_dic_list = []
    results = translator.translate_and_execute_query(query)
    
    if len(results) > 0:
        result = []
        best_candidate = results[0].query_candidate
        includename = True
        sparql_query = best_candidate.to_sparql_query()
        update_sparql_query = best_candidate.to_sparql_query(include_name=True)

        result_rows = results[0].query_result_rows
       
        entity_relation = get_question_entity_relation(sparql_query)
        question_entity = entity_relation.get('question_entity')
        predicate = entity_relation.get('predicate')
        relation = entity_relation.get('relation')
        
        
        for r in result_rows:
            best_an = ''
            if len(r) > 1:
                best_an =  r[1] + ' (' +   r[0] + ')'
                
            else:
                best_an = r[0] + ' (' + ')'
               
            result.append(best_an)
        
    
        
        if len(result)  > 0:
            r = ''
            
            for t in result:
                r = r + t + ';'
            r = r.strip(';')    
            
        answer_dic['question_entity'] = question_entity
        answer_dic['relation'] = relation
        answer_dic['query'] = query
        answer_dic['sparql_query'] = sparql_query
        answer_dic['update_sparql_query'] = update_sparql_query
        answer_dic['candidate_answer'] = r
        answer_dic['predicate'] = predicate
        
        answer_dic_list.append(answer_dic)
                
    return answer_dic_list



def subq1_answer(query,number_of_candidate):
    """
    Answer subquestion1 using AQQU. 
    :param utterance, number of candidate, if number of candidates is 3, it returns rank 1,2,3 candidates from AQQU
    :return a list of results returned from AQQU including SPARQL query, candidates and date. 
    """

    results = translator.translate_and_execute_query_sq1(query)
    
    candidate = []
    result = {}
    
    answer_dic_list = []
    i = 0
    
    if len(results) > 0 and len(results) >= number_of_candidate:
        while i < number_of_candidate:
            answer_dic = {}
            best_candidate = results[i].query_candidate
            result_rows = results[i].query_result_rows
            sparql_query = best_candidate.to_sparql_query()
            update_sparql_query = best_candidate.to_sparql_query_sq1(include_name=True)
            result["rank"] = i+1
            result["sparql_query"] = sparql_query
            result["update_sparql_query"] = update_sparql_query
            result["result_rows"] = result_rows
       
            answer_dic = temporal_predicate_sim(query,result)
            answer_dic_list.append(answer_dic)
            i = i + 1
    
    elif len(results) > 0 and len(results) < number_of_candidate:
        while i < len(results):
            answer_dic = {}
            best_candidate = results[i].query_candidate
            result_rows = results[i].query_result_rows
            sparql_query = best_candidate.to_sparql_query()
            update_sparql_query = best_candidate.to_sparql_query_sq1(include_name=True)
            
            result["rank"] = i+1
            result["sparql_query"] = sparql_query
            result["update_sparql_query"] = update_sparql_query
            result["result_rows"] = result_rows
            
            answer_dic = temporal_predicate_sim(query,result)
            answer_dic_list.append(answer_dic)
            i = i + 1
    
    
    return answer_dic_list

       
def temporal_predicate_sim(query,candidate):
     
    """
    Select best temporal predicate through computing similarity between temporal predicate and predicate
    :param query, candidate answers
    :return utterance, sparql_query, candidate_answer, date, sorted similarity, and best temporal predicate 
    """
    
    rank = candidate.get("rank")
    sparql_query = candidate.get("sparql_query")
    update_sparql_query = candidate.get("update_sparql_query")
    result_rows = candidate.get("result_rows")
    entity_relation = get_question_entity_relation(sparql_query)
    question_entity = entity_relation.get('question_entity')
    relation = entity_relation.get('relation')
    sparql_list = sparql_query.split('\n')
    predicate = entity_relation.get('predicate')
    
    # initial temporal predicate of answer
    temp_predicate = []
    # initial temporal predicate of cvt
    temp_cvt = []
    answer_dic = {}   
    result = []
    result_a_date = []
    result_c_date = []
    sim = {}  
            
    # Usually we get a name + mid.
    for r in result_rows:
        best_an = ''
            
        if len(sparql_list) >= 6: 
            #cvt
              
            if len(r) == 4 :    
                date_cvt_dic = {}
                
                tempc = r[2]
                
                if r[2].find('..') >=0:
                    tempc = r[2].split('..')[1]
                
                date_cvt_dic['mid'] = r[0]
                date_cvt_dic['name'] = r[1]
                date_cvt_dic['temp_pre'] = tempc
                date_cvt_dic['date'] = r[3] 
                best_an =  r[1] + ' (' +   r[0] + ')'
                print r[1]
                print r[0]
                
                if tempc not in temp_predicate:
                    temp_predicate.append(tempc)
                    sim[tempc] = similarity_predicate(predicate,tempc) 
             
                date_cvt_dic['similarity'] = sim.get(tempc)
                        
                if date_cvt_dic not in result_c_date:
                    result_c_date.append(date_cvt_dic)
            
            elif len(r) == 3:
            
                best_an =  r[1] + ' (' +   r[0] + ')'
                
            elif len(r) == 2:
            
                best_an =  r[1] + ' (' +   r[0] + ')'
            
            elif len(r) < 2:
            
                best_an =  r[0] + ' (' + ')'
            #the answer is not from CVT   
        if len(sparql_list) < 6: 
            #not cvt
            if len(r) == 4 :  #perhaps contains date 
                
                tempa = r[2]
                
                date_an_dic = {}
                
                if r[2].find('..') >= 0:
                    tempa = r[2].split('..')[1]
                
                
                date_an_dic['mid'] = r[0]
                date_an_dic['name'] = r[1]
                date_an_dic['temp_pre'] = tempa
                date_an_dic['date'] = r[3]
                best_an =  r[1] + ' (' +   r[0] + ')'
                            
                    
                if tempa not in temp_predicate:
                    temp_predicate.append(tempa)
                    sim[tempa] = similarity_predicate(predicate,tempa)
                
           
                date_an_dic['similarity'] = sim.get(tempa)
                        
                if date_an_dic not in result_a_date:
                    result_a_date.append(date_an_dic)
                            
                      
            elif len(r) == 3:
            
                best_an =  r[1] + ' (' +   r[0] + ')'
                 
            
            elif len(r) == 2:
            
                best_an =  r[1] + ' (' +   r[0] + ')'
                        
            elif len(r) < 2:
                best_an = r[0] + ' (' + ')'
            
        if best_an not in result:
            result.append(best_an)
                       
    r = ''
    r_c = ''
    r_a = ''
    r_s = ''
    tp_sim = {}
    top_tp = ''
    top_tp_sim = ''
        
    if len(result)  > 0:
            
        for t in result:
            r = r + t + ';'
        r = r.strip(';')    
            
        
    if len(sim) > 0:
            
        sorted_x = sorted(sim.iteritems(), key=operator.itemgetter(1),reverse=True)
            # extract the top-1 similar temporal predicate
   
        top = sorted_x[0]
            
        top_tp = top[0]
        top_tp_sim = str(top[1])
            
        r_s = top_tp + "(" + top_tp_sim + ")" 
            
        top_tp_syn = get_predicate_syn(top_tp)
            
            
        for xx in sorted_x:
            if len(top_tp_syn) > 0 and xx[0].find(top_tp_syn) >= 0:
                
                top_tp_syn_sim = str(xx[1])
                r_s = r_s + ";" + top_tp_syn + "(" + top_tp_syn_sim + ")" 
             
        if len(result_c_date) >0 :
            cvt = sorted(result_c_date, key=lambda d: (-d['similarity']))
            cvt_date_list = []
            for y in cvt:
                temp_predicate = y.get('temp_pre')
                if top_tp.find(temp_predicate) >=0 or top_tp_syn.find(temp_predicate) >=0:
                    c_re = '[mid:(' + y.get('mid')  + ') name:(' +  y.get('name') + ') tp:(' +  y.get('temp_pre')  + ') date:(' +  y.get('date')  + ')];'
                    if c_re not in cvt_date_list:
                        cvt_date_list.append(c_re)
                        r_c = r_c + c_re
                            
  
        if len(result_a_date) >0 :    
                        
            atp = sorted(result_a_date, key=lambda d: (-d['similarity']))
            a_date_list = []      
            for z in atp:
                temp_predicate = z.get('temp_pre')
                if top_tp.find(temp_predicate) >=0 or top_tp_syn.find(temp_predicate) >=0:
                        
                    a_re = '[mid:(' + z.get('mid')  + ') name:(' +  z.get('name') + ') tp:(' +  z.get('temp_pre')  + ') date:(' +  z.get('date')  + ')];'
                    if a_re not in a_date_list:
                        a_date_list.append(a_re)
                        r_a = r_a + a_re
        
    
    answer_dic['question_entity'] = question_entity
    answer_dic['relation'] = relation
    answer_dic['query'] = query     
    answer_dic['sparql_query'] = sparql_query
    answer_dic['update_sparql_query'] = update_sparql_query
    answer_dic['candidate_answer'] = r
    answer_dic['date_in_CVT'] = r_c 
    answer_dic['date_in_pro'] = r_a
    answer_dic['sorted_sim'] = r_s
    answer_dic['predicate'] = predicate
                  
    return answer_dic
            
            
def subq2_answer(query):
    
    """
    Answer subquestion2 using AQQU. 
    :param utterance of subquestion 2
    :return a list of results returned from AQQU including SPARQL query, candidates and date etc. 
    """

    answer_dic = {}
    answer_dic_list = []
    havedate = 0  
    results = translator.translate_and_execute_query_sq2(query)
    if len(results) > 0:# Add the dataType of answer in SPARQL
        i = 0
        while i < len(results) :
#                       
            best_candidate = results[i].query_candidate
            sparql_query = best_candidate.to_sparql_query()
            update_sparql_query = best_candidate.to_sparql_query_sq2(include_name=True)
            result_rows = results[i].query_result_rows
            result = []
            
            # Usually we get a name + mid.
            for r in result_rows:
                if len(r) >3 :
                    result.append("<%s> <%s> <%s> <%s>" % (r[0], r[1] ,r[2],r[3]))
                elif len(r) >2:  
                    result.append("<%s> <%s> <%s>" % (r[0], r[1], r[2]))
                elif len(r) > 1:
                    result.append("<%s> <%s>" % (r[0], r[1]))
                else:
                    result.append("<%s>" % r[0])
                        
            
            
            r= " ".join(result)
            if r.find('http://www.w3.org/2001/XMLSchema#datetime') >= 0: #find date answer
                i = len(results)
                t = ''
                havedate = 1
                logger.info("SPARQL query: %s" % sparql_query)
                logger.info("Result: %s " % " ".join(result))
                
                for x in result:
                    x = x.replace('<http://www.w3.org/2001/XMLSchema#datetime>','')
                    x = x.replace('> <',',')
                    t = t + x + ';'
                t = t.strip(';')
                
                entity_relation = get_question_entity_relation(sparql_query)
                question_entity = entity_relation.get('question_entity')
                relation = entity_relation.get('relation')
                predicate = entity_relation.get('predicate')
                answer_dic['question_entity'] = question_entity
                answer_dic['relation'] = relation
                
                answer_dic['query'] = query
                answer_dic['sparql_query'] = sparql_query
                answer_dic['update_sparql_query'] = update_sparql_query
                answer_dic['candidate_answer'] = t
                answer_dic['predicate'] = predicate
                
                answer_dic_list.append(answer_dic)
            i = i+1            
    
    return answer_dic_list     

def get_predicate_syn(predicate):
    """
    Get the predicate in pair dictionary. 
    :param  predicate
    :return predicate in pair dictionary. 
    """
    syn_predicate = ''
    if temporal_predicate_from.has_key(predicate):
            syn_predicate = temporal_predicate_from.get(predicate)
    
    if temporal_predicate_to.has_key(predicate):
            syn_predicate = temporal_predicate_to.get(predicate)
            
    return syn_predicate
  
def is_valid_query(query):
    """
    Check if a query is valid and the type of query such as subquesteion 1, subquestion 2 or simple when question. 
    :param query
    :return valid or not, the type of question. 
    """
    
    flag = 0
    
    type = ''
    

    
    #if a query is subq2, it starts with '|||'
    if query.startswith('|||'):
        query = query.replace('|||','')
        print query
        type = 'subq2'
    
    #if a query is subq1, it starts with '%%%'
    if query.startswith('%%%'):
        query = query.replace('%%%','')
        type = 'subq1'
    #if a query is when question, it starts with '###'
    if query.startswith('###'):
        query = query.replace('###','')
        type = 'when'
    #if a query is normal question, it starts with no symbol
    if len(query) > 0:
        s = query[0]
        if s.isalpha():
            flag =1 
            #is_query = [flag,query,type]
    
    return {'flag':flag,'query':query,'type':type}



class Service(object):
    
    
    
    def __init__(self, config):
        self.redis = redis.Redis(config['redis_host'], config['redis_port'])
        
        template_path = os.path.join(os.path.dirname(__file__), 'templates')
        self.jinja_env = Environment(loader=FileSystemLoader(template_path),
                                 autoescape=True)
        
        
        self.url_map = Map([
            Rule('/', endpoint='new_url'),
            
        ])
        
        
    def on_new_query(self, request):
        import logging
        error = None
        
        sortsim_1 = None
        cvt_date_1 = None
        pro_date_1 = None
                
        
        answer_dic = {}
        answer_dic_list = []
   #     answer_dic = {}
        
        query = ''
        if request.method == 'POST':
            query = request.form['query']
            query = query.strip()
            print 'query:'
            print query
            is_query = is_valid_query(query)
            flag = is_query.get('flag')
            query = is_query.get('query')
            type = is_query.get('type')
            print "flag:"
            print flag
            print "query:"
            print query
            print 'type:'
            print type
            if flag == 1:
                if type.find('subq1') >= 0:
                    
                    answer_dic_list = subq1_answer(query,3)
                    print 'length of answer_dic_list:'
                    print len(answer_dic_list)
                    if len(answer_dic_list) < 3:
                        
                        answer_dic['query'] = ''     
                        answer_dic['question_entity'] = ''
                        answer_dic['relation'] = ''
                        answer_dic['sparql_query'] = ''
                        answer_dic['update_sparql_query'] = ''
                        answer_dic['candidate_answer'] = ''
                        answer_dic['predicate'] = ''
                        answer_dic['date_in_CVT'] = '' 
                        answer_dic['date_in_pro'] = ''
                        answer_dic['sorted_sim'] = ''
                        if len(answer_dic_list) == 2:
                            answer_dic_list.append(answer_dic)
                        elif len(answer_dic_list) == 1:
                            answer_dic_list.append(answer_dic)
                            answer_dic_list.append(answer_dic)
                        else:
                            answer_dic_list.append(answer_dic)
                            answer_dic_list.append(answer_dic)
                            answer_dic_list.append(answer_dic)
                    
                    if len(answer_dic_list) == 3:       
                        question = answer_dic_list[0]['query']
                        SPARQL_1 = answer_dic_list[0]['sparql_query'] 
                        question_entity_1 = answer_dic_list[0]['question_entity']
                        relation_1 = answer_dic_list[0]['relation']
                        SPARQL_update_1 = answer_dic_list[0]['update_sparql_query']
                        answer_1 = answer_dic_list[0]['candidate_answer']
                        predicate_1 = answer_dic_list[0]['predicate']
                        sortsim_1 = answer_dic_list[0]['sorted_sim']
                        cvt_date_1 = answer_dic_list[0]['date_in_CVT']
                        pro_date_1 = answer_dic_list[0]['date_in_pro']
                
                
                        SPARQL_2 = answer_dic_list[1]['sparql_query'] 
                        question_entity_2 = answer_dic_list[1]['question_entity']
                        relation_2 = answer_dic_list[1]['relation']
                        SPARQL_update_2 = answer_dic_list[1]['update_sparql_query']
                        answer_2 = answer_dic_list[1]['candidate_answer']
                        predicate_2 = answer_dic_list[1]['predicate']
                        sortsim_2 = answer_dic_list[1]['sorted_sim']
                        cvt_date_2 = answer_dic_list[1]['date_in_CVT']
                        pro_date_2 = answer_dic_list[1]['date_in_pro']
                
                        SPARQL_3 = answer_dic_list[2]['sparql_query'] 
                        question_entity_3 = answer_dic_list[2]['question_entity']
                        relation_3 = answer_dic_list[2]['relation']
                        SPARQL_update_3 = answer_dic_list[2]['update_sparql_query']
                        answer_3 = answer_dic_list[2]['candidate_answer']
                        predicate_3 = answer_dic_list[2]['predicate']
                        sortsim_3 = answer_dic_list[2]['sorted_sim']
                        cvt_date_3 = answer_dic_list[2]['date_in_CVT']
                        pro_date_3 = answer_dic_list[2]['date_in_pro']
                   
                        return self.render_template('subq1_answer_rank123.html',
            
            question = question,
            question_entity_1 = question_entity_1,
            relation_1 = relation_1,
            SPARQL_update_1 = SPARQL_update_1,
            SPARQL_1 = SPARQL_1,
            predicate_1 = predicate_1, 
            answer_1 = answer_1,
            sim_1 = sortsim_1,
            cvt_date_1 = cvt_date_1,
            pro_date_1 = pro_date_1,
            
            question_entity_2 = question_entity_2,
            relation_2 = relation_2,
            SPARQL_update_2 = SPARQL_update_2,
            SPARQL_2 = SPARQL_2,
            predicate_2 = predicate_2, 
            answer_2 = answer_2,
            sim_2 = sortsim_2,
            cvt_date_2 = cvt_date_2,
            pro_date_2 = pro_date_2,
            
            question_entity_3 = question_entity_3,
            relation_3 = relation_3,
            SPARQL_update_3 = SPARQL_update_3,
            SPARQL_3 = SPARQL_3,
            predicate_3 = predicate_3, 
            answer_3 = answer_3,
            sim_3 = sortsim_3,
            cvt_date_3 = cvt_date_3,
            pro_date_3 = pro_date_3,
            )
                
                elif type.find('subq2') >= 0:
                    answer_dic_list = subq2_answer(query)
                else:
                    answer_dic_list = aqqu_answer(query)    
                
                if len(answer_dic_list)>0:
                    SPARQL = answer_dic_list[0]['sparql_query'] 
                    question_entity = answer_dic_list[0]['question_entity']
                    relation = answer_dic_list[0]['relation']
                    SPARQL_update = answer_dic_list[0]['update_sparql_query']
                    question = answer_dic_list[0]['query']
                    
                    answer = answer_dic_list[0]['candidate_answer']
                    predicate = answer_dic_list[0]['predicate']
                   
                    return self.render_template('subq2_answer.html',
            
            question = question,
            SPARQL = SPARQL,
            predicate = predicate, 
            answer = answer,
            question_entity = question_entity,
            relation = relation,
            SPARQL_update = SPARQL_update,
            
            )
                    
                
                    
                    
            else:
                error = 'Not a valid question'
                    
            
            
            
        return self.render_template('new_query.html', error=error, query=query)
     
    
    def error_404(self):
        response = self.render_template('404.html')
        response.status_code = 404
        return response
    
    def dispatch_request(self, request):
        
        adapter = self.url_map.bind_to_environ(request.environ)
        try:
            endpoint, values = adapter.match()
            return getattr(self, 'on_new_query')(request, **values)
        except NotFound, e:
            return self.error_404()
        except HTTPException, e:
            return e

    
    def add_template(self, template_name, context_dic):
        return self.render_template(template_name,
            question_entity = context_dic['question_entity'],
            SPARQL=context_dic['sparql_query'],
            SPARQL_update=context_dic['update_sparql_query'],
            relation=context_dic['relation'],
            answer=context_dic['candidate_answer'],
            predicate=context_dic['predicate']
        )
        
        question_entity = context_dic['question_entity'],
        SPARQL = context_dic['sparql_query']
        SPARQL_update=context_dic['update_sparql_query'],
        relation=context_dic['relation'],
        answer = context_dic['candidate_answer']
        predicate = context_dic['predicate']
        if 'date_in_CVT' in context_dic:
            cvt_date = context_dic['date_in_CVT']
        if 'date_in_pro' in context_dic:
            pro_date = context_dic['date_in_pro']
       
        return Response(template_name, mimetype='text/html') 
    
    def render_template(self, template_name, **context):
        t = self.jinja_env.get_template(template_name)
        return Response(t.render(context), mimetype='text/html')
  

    def wsgi_app(self, environ, start_response):
        request = Request(environ)
        response = self.dispatch_request(request)
        return response(environ, start_response)

    def __call__(self, environ, start_response):
        return self.wsgi_app(environ, start_response)


def create_app(redis_host='localhost', redis_port=6379, with_static=True):
    
    app = Service({
        'redis_host':       redis_host,
        'redis_port':       redis_port
    })
    if with_static:
        app.wsgi_app = SharedDataMiddleware(app.wsgi_app, {
            '/static':  os.path.join(os.path.dirname(__file__), 'static')
        })
    
    return app


if __name__ == '__main__':
    

    logging.basicConfig(format="%(asctime)s : %(levelname)s "
                           ": %(module)s : %(message)s",
                    level=logging.INFO)
   
    logger = logging.getLogger(__name__)
   
    globals.read_configuration("config.cfg")
    ranker = scorer_globals.scorers_dict["WQ_Ranker"]
    translator = QueryTranslator.init_from_config()
    translator.set_scorer(ranker)   
    config_options = globals.config
    
    word2vec_model = config_options.get('Word2VecModel',
                                                     'model')
    model = gensim.models.KeyedVectors.load_word2vec_format(word2vec_model, binary=True) 

    
    temporal_predicate_pairs_file = config_options.get('SparqlUpdate',
                                                     'temporal-predicate-pairs')
        
    stop_words_file = config_options.get('SparqlUpdate', 'stop-words')    
    temporal_predicate_from = read_temporal_predicate_from(temporal_predicate_pairs_file)
    temporal_predicate_to = read_temporal_predicate_to(temporal_predicate_pairs_file)
    stop_words = read_stop_words(stop_words_file)
    
    from werkzeug.serving import run_simple
    app = create_app()
    #change AQQU KBQA server and port number according to your AQQU server configuration
    run_simple('AQQU KBQA server', 8995, app, use_debugger=True, use_reloader=True)
    
    
        