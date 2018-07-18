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


def calculate_protected_needed_at_each_position(p, k, alpha):
    ''' Calculate an mtable for parameters p, k, alpha
    '''
    result = []

    for n in range(1, k + 1):
        numProtCandidates = binom.ppf(alpha, n, p)
        result.append(int(numProtCandidates))

    return result


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
    for i in range(1, len(table)+1):
        required = table[i-1]
        available = count_protected_in_prefix(i, ranking)
        if available < required:
            return(False)
    return(True)


def perform_simulation_yang_stoyanovich(N, p, k, alpha):
    ''' Perform a simulation by generating N rankings using Yang-Stoyanovich
        method, and then compare them with an mtable.
    '''
    mtable = calculate_protected_needed_at_each_position(p, k, alpha)

    success = 0
    for i in range(N):
        ranking = create_ranking_yang_stoyanovich(p, k)
        if ranking_satisfies_table_at_every_position(ranking, mtable):
            success += 1
        if (i+1) % 100 == 0:
            print("trial %d/%d: p=%.2f, k=%d, alpha=%.2f; success=%d, failure_prob=%.4f" %
                  (i, N, p, k, alpha, success, (i-success)/success))


print("Example ranking")
print(create_ranking_yang_stoyanovich(0.2, 10))
print("Example count protected on script")
print(count_protected_in_prefix(2, [0, 1, 1, 1]))
print("Example mtable")
print(calculate_protected_needed_at_each_position(0.6, 12, 0.1))
print("Example ranking satisfies mtable")
print(ranking_satisfies_table_at_every_position([0, 1, 1, 0, 1], [0, 1, 2, 2, 3]))

print("** Starting simulations **")
# Blue curve (top) in Figure 2 of FA*IR
perform_simulation_yang_stoyanovich(N=10000, p=0.5, k=1500, alpha=0.05)
# Red curve (bottom) in Figure 2 of FA*IR
perform_simulation_yang_stoyanovich(N=10000, p=0.5, k=1000, alpha=0.01)
