import random
import numpy as np
from scipy.stats import binom
import pandas as pd
from scipy.stats import poisson
import math
import copy

def multinomCDF_log(G, k, p, tau_p):
    s = float(k);
    log_cdf = -poisson.logpmf(k,s);
    gamma1 = 0.0;
    gamma2 = 0.0;
    sum_s2 = 0.0;
    sum_mu = 0.0;
    
    # P(W=k)
    for i in range(0,G):
        sp = s*p[i];
        
        pcdf = poisson.cdf(tau_p[i],sp);
        log_cdf += np.log(pcdf);
        
        mu = sp*(1-poisson.pmf(tau_p[i],sp)/pcdf);
        s2 = mu-(tau_p[i]-mu)*(sp-mu);
        
        mr = tau_p[i];
        mf2 = sp*mu-mr*(sp-mu);
        
        mr *= tau_p[i]-1;
        mf3 = sp*mf2-mr*(sp-mu);
        
        mr *= tau_p[i]-2;
        mf4 = sp*mf3-mr*(sp-mu);
        
        mu2 = mf2+mu*(1-mu);
        mu3 = mf3+mf2*(3-3*mu)+mu*(1+mu*(-3+2*mu));
        mu4 = mf4+mf3*(6-4*mu)+mf2*(7+mu*(-12+6*mu))+mu*(1+mu*(-4+mu*(6-3*mu)));
        
        gamma1 += mu3;
        gamma2 += mu4-3*s2*s2;
        sum_mu += mu;
        sum_s2 += s2; 
    sp = np.sqrt(sum_s2);
    gamma1 /= sum_s2*sp;
    gamma2 /= sum_s2*sum_s2;
    
    x = (k-sum_mu)/sp;
    x2 = x*x;
    
    PWN = (-x2/2
    +np.log(1+gamma1/6*x*(x2-3)+gamma2/24*(x2*x2-6*x2+3)
    +gamma1*gamma1/72*(((x2-15)*x2+45)*x2-15))
    -np.log(2*math.pi)/2 -np.log(sp));
    
    log_cdf += PWN;
    return log_cdf;

def multinomCDF(G, k, p, tau_p):
    return np.exp(multinomCDF_log(G, k, p, tau_p ));


'''
Computes the mtable based on binomial cdf
p: array of p values for each group (including non-protected), which should sum up to 1
'''
def get_minimum_targets_binom(p, alpha, k):
    minimum_targets = []
    p_np = p[1:]

    for i in range(k):
        mt = []
        for p_j in p_np:
            mt.append(binom.ppf(alpha, i+1, p_j))
        minimum_targets.append(mt)   
        
    #output the minimum target number as a table    
#     df = pd.DataFrame(data=(np.array(minimum_targets)).astype(int))
#     df.columns = p_np
#     df.index = np.array(range(k))+1
#     df.to_html("binomial2Multinomial_"+str(p)+".html")
    return minimum_targets 

 
''' Create a ranking of 'k' positions in which at each position the
    probability that the candidate is protected is 'p'.
'''
def create_ranking_yang_stoyanovich(p, k):

    ranking = []
    p = get_cumulative_p(p)    

    for i in range(k):
        rand_p = random.random()
        if rand_p <= p[0]:
            ranking.append(0)
        else:     
            for j in range(1, len(p)):
                if rand_p > p[j-1] and rand_p <= p[j]:
                    ranking.append(j)
    return ranking


'''
Computes the cumulative p values
Example) p = [0.3,0.3,0.2,0.2]
get_cumulative_p(p) = [0.3, 0.6, 0.8, 1.0]
'''    
def get_cumulative_p (p):
    cumulative_p = [p[0]]
    
    for x in range(1, len(p)):
        cumulative_p.append(cumulative_p[x-1]+p[x])
        
    return cumulative_p    

'''
Counts the number of each group in each prefix of the ranking.
Return: 2-D array, G - columns, k - rows
'''
def get_counter(p, ranking):
    counter = []
    curr = [0] * len(p)
    
    for i in ranking:
        for j in range(len(p)):
            if i == j:
                curr[j] = curr[j]+1
        counter.append(copy.copy(curr))    
        
    return counter    
  

'''
performs single test.
'''
def single_test(k, counter, minimum_targets):
    singleTest = np.zeros(k)
    
    for i in range(k):
        if any(counter[i][j+1]<minimum_targets[i][j] for j in range(len(counter[0])-1)) :
            singleTest[i] += 1
    return singleTest

'''
performs multiple test.
'''
def multiple_test(k, counter, minimum_targets):
    multipleTest = 0
    
    for i in range(k):
        if any(counter[i][j+1]<minimum_targets[i][j] for j in range(len(counter[0])-1)) :
            multipleTest += 1 
            break

    return multipleTest

'''
Performs single test with multinomial cdf.
Not complete and still in process.
'''
def single_test_multinom(k, counter, p, alpha):
    singleTest = np.zeros(k)
    
    for i in range(k):
        curr = counter[i]
        curr[0] = i+1
        
        if multinomCDF(len(p), i+1, p, curr) < alpha :
            singleTest[i] += 1
    return singleTest

'''
Performs multiple test with multinomial cdf.
Not complete and still in process
'''
def multiple_test_multinom(k, counter, p, alpha):
    multipleTest = 0
    
    for i in range(k):
        curr = counter[i]
        curr[0] = i+1
        if multinomCDF(len(p), i+1, p, curr) < alpha :
            multipleTest += 1 
            break

    return multipleTest

'''
Saves the k-vector computed from single test (binom) as a table
'''
def single_test_2_table (singleTest, multipleTest, p, N, alpha) :
    df = pd.DataFrame(data=(np.array(singleTest)).astype(int))
    df.columns = ["# failed rankings at k"]
    df.index = np.array(range(k))+1
    df.to_html("singleTest_"+str(p)+"_N"+str(N)+"_alpha"+str(alpha)+"_binom_MT_"
               +str(multipleTest)+".html")

'''
Saves the k-vector computed from single test (multinom) as a table
'''
def single_test_2_table_multinom (singleTest, multipleTest, p, N, alpha) :
    df = pd.DataFrame(data=(np.array(singleTest)).astype(int))
    df.columns = ["# failed rankings at k"]
    df.index = np.array(range(k))+1
    df.to_html("singleTest"+str(p)+"_N"+str(N)+"_alpha"+str(alpha)+"_multinom_MT_"
               +str(multipleTest)+".html")
 
'''
Performs both single and multiple test based on binomial cdf
N : Number of rankings to be tested
k : size of the rankings
p : array of p values of each group (including the unprotected group), which should sum upto 1
alpha : significance level
'''

def perform_tests (p, k, N, alpha):
    singleTest = np.zeros(k)
    multipleTest = 0
    
    # compute the table of minimum target number of each group
    minimum_targets = get_minimum_targets_binom(p, alpha, k)
    
    for i in range(N):
        
        # generate ranking
        ranking = create_ranking_yang_stoyanovich(p, k)
        
        # count the number of each group at each prefix of the ranking
        counter = get_counter(p, ranking)
                
        # perform single test
        singleTest += single_test(k, counter, minimum_targets)
        
        # perform multiple test
        multipleTest += multiple_test(k, counter, minimum_targets)
    
    # save result of the single test as a table
    single_test_2_table (singleTest, multipleTest, p, N, alpha)  
    
    return singleTest, multipleTest

'''
Performs both single and multiple test based on multinom cdf
Not complete. Still being processed
N : Number of rankings to be tested
k : size of the rankings
p : array of p values of each group (including the unprotected group), which should sum upto 1
alpha : significance level
'''    

def perform_tests_multinom (p, k, N, alpha):
    singleTest = np.zeros(k)
    multipleTest = 0
    
    # compute the table of minimum target number of each group
    minimum_targets = get_minimum_targets_binom(p, alpha, k)
    
    for i in range(N):
        
        # generate ranking
        ranking = create_ranking_yang_stoyanovich(p, k)
        
        # count the number of each group at each prefix of the ranking
        counter = get_counter(p, ranking)
        
        # perform single test
        singleTest += single_test_multinom(k, counter, p, alpha)
        
        # perform multiple test
        multipleTest += multiple_test_multinom(k, counter, p, alpha)
    
    # save result of the single test as a table
    single_test_2_table_multinom (singleTest, multipleTest, p, N, alpha)
    
    return singleTest, multipleTest


# Example test
p = [0.5, 0.5]
k = 1000
N = 10000
alpha = 0.01

perform_tests (p, k, N, alpha)    