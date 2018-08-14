
from simulation_yang_stoyanovich import perform_simulation_yang_stoyanovich

import numpy as np

N = 50000
print("N=%d (number of trials)" % N)
print()

for (k, alpha) in (1000, 0.01), (1500, 0.05):
    print(" k=%d, alpha=%.2f" % (k, alpha))
    for p in np.arange(0.1, 1.0, 0.1):
        failure_rate = perform_simulation_yang_stoyanovich(N=N, p=p, k=k, alpha=alpha, quiet=True)
        print("   p=%.2f failure_rate=%.6f" % (p, failure_rate))
    print()
