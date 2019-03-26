# Code from Tom Suehr's

import csv

from simulation_yang_stoyanovich import calculate_protected_needed_at_each_position, create_ranking_yang_stoyanovich

BASEDIR = "C:\\Users\\TSuehr\\Documents\\synthetic_fair_test\\csvFiles\\csv-python\\"


def writeMTableToCSV(k, p, alpha):
    mtable = calculate_protected_needed_at_each_position(p, k, alpha)
    filename = "mtable_" + repr(k) + "_" + repr(p) + "_" + repr(alpha) + ".csv"
    with open(BASEDIR + filename, "w") as csv_file:
        writer = csv.writer(csv_file, delimiter=';')
        writer.writerow(mtable)


def writeRankingToCSV(k, p, alpha):
    ranking = create_ranking_yang_stoyanovich(p, k)
    filename = "ranking_" + repr(k) + "_" + repr(p) + "_" + repr(alpha) + ".csv"
    with open(BASEDIR + filename,
              "w") as csv_file:
        writer = csv.writer(csv_file, delimiter=';')
        writer.writerow(ranking)


def getProportionFromCSVFile(k, p, alpha):
    filename = "ranking_" + repr(k) + "_" + repr(p) + "_" + repr(alpha) + ".csv"
    with open(BASEDIR + filename,
              "r") as csv_file:
        reader = csv.reader(csv_file, delimiter=';')
        counter = 0
        for row in reader:
            for i in row:
                counter += int(i)
    return counter/k


k = 1500
alpha = 0.05
for p in [0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9]:
    # writeMTableToCSV(k, p, alpha)
    # writeRankingToCSV(k, p, alpha)
    print(getProportionFromCSVFile(k, p, alpha))


# Old code follows
# print("Example ranking")
# print(create_ranking_yang_stoyanovich(0.2, 10))
# print("Example count protected on script")
# print(count_protected_in_prefix(2, [0, 1, 1, 1]))
# print("Example mtable")
# print(calculate_protected_needed_at_each_position(0.6, 12, 0.1))
# print("Example ranking satisfies mtable")
# print(ranking_satisfies_table_at_every_position([0, 1, 1, 0, 1], [0, 1, 2, 2, 3]))

# print("** Starting simulations **")
# Blue curve (top) in Figure 2 of FA*IR
# perform_simulation_yang_stoyanovich(N=10000, p=0.5, k=1500, alpha=0.05)
# Red curve (bottom) in Figure 2 of FA*IR
# perform_simulation_yang_stoyanovich(N=10000, p=0.5, k=1000, alpha=0.01)
