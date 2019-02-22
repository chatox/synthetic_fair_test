# New code by ChaTo on 2018-07-16

import random
from scipy.stats import binom


def create_ranking_yang_stoyanovich(p, k):
    ''' Create a ranking of 'k' positions in which at each position the
        probability that the candidate is protected is 'p'.
    '''
    ranking = []
    for i in range(k):
        is_protected = (random.random() <= p)
        if is_protected:
            ranking.append(1)
        else:
            ranking.append(0)
    return ranking


def count_protected_in_prefix(k, ranking):
    ''' Count how many protected elements are in a prefix of
        size k of the given ranking
    '''
    count = 0
    for i in range(k):
        if ranking[i] == 1:
            count += 1
    return count


def ranking_satisfies_table_at_every_position(ranking, table):
    ''' Check if a ranking satisfies an mtable at every position
    '''
    for i in range(1, len(table) + 1):
        required = table[i - 1]
        available = count_protected_in_prefix(i, ranking)
        if available < required:
            return (False)
    return (True)


def calculate_protected_needed_at_each_position(p, k, alpha):
    ''' Calculate an mtable for parameters p, k, alpha
    '''
    result = []

    for n in range(1, k + 1):
        numProtCandidates = binom.ppf(alpha, n, p)
        result.append(int(numProtCandidates))

    return result


def proportion_false(results):
    ''' Determine the fraction of results that were false
    '''

    failures = 0
    trials = len(results)
    for result in results:
        if not result:
            failures += 1

    return float(failures)/float(trials)


def perform_simulation_yang_stoyanovich(N, p, k, alpha, quiet=False):
    ''' Perform a simulation by generating N rankings using Yang-Stoyanovich
        method, and then compare them with an mtable.
    '''
    mtable = calculate_protected_needed_at_each_position(p, k, alpha)

    results = []
    for i in range(N):
        ranking = create_ranking_yang_stoyanovich(p, k)
        result = ranking_satisfies_table_at_every_position(ranking, mtable)
        results.append(result)
        if not quiet:
            if (i + 1) % 100 == 0:
                print("trial %d/%d: p=%.2f, k=%d, alpha=%.2f; failure_prob=%.4f" %
                      (i, N, p, k, alpha, proportion_false(results)))

    return proportion_false(results)
