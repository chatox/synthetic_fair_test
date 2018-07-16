# New code by ChaTo on 2018-07-16

import random
from scipy.stats import binom

def create_ranking_yang_stoyanovich(p, k):
    ranking = []
    for i in range(k):
        is_protected = (random.random() <= p)
        if is_protected:
            ranking.append(1)
        else:
            ranking.append(0)
    return ranking


def calculate_protected_needed_at_each_position(p, k, alpha):
    result = []

    for n in range(1, k + 1):
        numProtCandidates = binom.ppf(alpha, n, p)
        result.append(int(numProtCandidates))

    return result



# In[83]:


def count_protected_in_prefix(k, ranking):
    count = 0
    for i in range(k):
        if ranking[i] == 1:
            count += 1
    return count

print(count_protected_in_prefix(2, [0, 1, 1, 1]))


# In[84]:


def ranking_satisfies_table_at_every_position(ranking, table):
    for i in range(1, len(table)+1):
        required = table[i-1]
        available = count_protected_in_prefix(i, ranking)
        if available < required:
            return(False)
    return(True)


def perform_simulation_yang_stoyanovich(N, p, k, alpha):
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
print("Example mtable")
print(calculate_protected_needed_at_each_position(0.6, 12, 0.1))
print("Example ranking satisfies mtable")
print(ranking_satisfies_table_at_every_position([0, 1, 1, 0, 1], [0, 1, 2, 2, 3]))

print("** Starting simulations **")
perform_simulation_yang_stoyanovich(N=10000, p=0.8, k=1000, alpha=0.01)
perform_simulation_yang_stoyanovich(N=10000, p=0.8, k=1500, alpha=0.05)
