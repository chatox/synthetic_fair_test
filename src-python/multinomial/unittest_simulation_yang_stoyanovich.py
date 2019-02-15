import unittest

from simulation_yang_stoyanovich import create_ranking_yang_stoyanovich, \
                                        count_protected_in_prefix, \
                                        ranking_satisfies_table_at_every_position, \
                                        calculate_protected_needed_at_each_position, \
                                        proportion_false


class TestSimulation(unittest.TestCase):

    def test_create_ranking(self):
        rank0 = create_ranking_yang_stoyanovich(0, 1)
        self.assertEqual(len(rank0), 1)
        self.assertEqual(rank0[0], 0)

        rank1 = create_ranking_yang_stoyanovich(1, 1)
        self.assertEqual(len(rank1), 1)
        self.assertEqual(rank1[0], 1)

        rank10 = create_ranking_yang_stoyanovich(0.5, 10)
        self.assertEqual(len(rank10), 10)

    def test_count_protected_in_prefix(self):
        rank = [0, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1]
        self.assertEqual(len(rank), 12)
        self.assertEqual(count_protected_in_prefix(1, rank), 0)
        self.assertEqual(count_protected_in_prefix(2, rank), 1)
        self.assertEqual(count_protected_in_prefix(3, rank), 2)
        self.assertEqual(count_protected_in_prefix(4, rank), 3)
        self.assertEqual(count_protected_in_prefix(5, rank), 3)
        self.assertEqual(count_protected_in_prefix(6, rank), 4)
        self.assertEqual(count_protected_in_prefix(7, rank), 5)
        self.assertEqual(count_protected_in_prefix(8, rank), 6)
        self.assertEqual(count_protected_in_prefix(9, rank), 6)
        self.assertEqual(count_protected_in_prefix(10, rank), 6)
        self.assertEqual(count_protected_in_prefix(11, rank), 7)
        self.assertEqual(count_protected_in_prefix(12, rank), 8)

        rank = [0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1]
        self.assertEqual(count_protected_in_prefix(1, rank), 0)
        self.assertEqual(count_protected_in_prefix(2, rank), 0)
        self.assertEqual(count_protected_in_prefix(3, rank), 0)
        self.assertEqual(count_protected_in_prefix(4, rank), 0)
        self.assertEqual(count_protected_in_prefix(5, rank), 0)
        self.assertEqual(count_protected_in_prefix(6, rank), 1)
        self.assertEqual(count_protected_in_prefix(7, rank), 2)
        self.assertEqual(count_protected_in_prefix(8, rank), 3)
        self.assertEqual(count_protected_in_prefix(9, rank), 4)
        self.assertEqual(count_protected_in_prefix(10, rank), 4)
        self.assertEqual(count_protected_in_prefix(11, rank), 5)
        self.assertEqual(count_protected_in_prefix(12, rank), 6)

    def test_ranking_satisfies_table_at_every_position(self):

        def test_with_table_variations(rank, table):
            self.assertTrue(ranking_satisfies_table_at_every_position(rank, table))

            for i in range(len(table)):
                table_copy = table.copy()
                table_copy[i] = table_copy[i] + 1
                self.assertFalse(ranking_satisfies_table_at_every_position(rank, table_copy))

            for i in range(len(rank)):
                rank_copy = rank.copy()
                if rank_copy[i] == 1:
                    rank_copy[i] = 0
                    self.assertFalse(ranking_satisfies_table_at_every_position(rank_copy, table))

        rank1 = [0, 0, 1, 0, 1, 0, 1, 0]
        table1 = [0, 0, 1, 1, 2, 2, 3, 3]
        test_with_table_variations(rank1, table1)

        rank2 = [1, 0, 0, 1, 0, 1, 0, 0, 0, 1]
        table2 = [1, 1, 1, 2, 2, 3, 3, 3, 3, 4]
        test_with_table_variations(rank2, table2)

        rank3 = [0, 0, 0, 0, 1, 1, 1, 1, 1, 0]
        table3 = [0, 0, 0, 0, 1, 2, 3, 4, 5, 5]
        test_with_table_variations(rank3, table3)

        rank4 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
        table4 = [0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
        test_with_table_variations(rank4, table4)

        rank5 = [1, 0, 0, 0, 0, 0, 1, 1, 1, 1]
        table5 = [1, 1, 1, 1, 1, 1, 2, 3, 4, 5]
        test_with_table_variations(rank5, table5)

        # Table that is always satisfied
        table0 = [0, 0, 0, 0, 0, 0, 0, 0]
        for i in range(10):
            rank = create_ranking_yang_stoyanovich(0.5, 8)
            self.assertTrue(ranking_satisfies_table_at_every_position(rank, table0))

        # Table that's impossible to satisfy
        tablei = [2, 3, 4, 5, 6, 7, 8, 9]
        for i in range(10):
            rank = create_ranking_yang_stoyanovich(0.5, 8)
            self.assertFalse(ranking_satisfies_table_at_every_position(rank, tablei))

    def test_calculate_protected_needed_at_each_position(self):
        # Examples from the paper
        alpha = 0.1
        k = 12
        table1 = calculate_protected_needed_at_each_position(0.1, k, alpha)
        self.assertEqual(table1, [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0])
        table2 = calculate_protected_needed_at_each_position(0.2, k, alpha)
        self.assertEqual(table2, [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1])
        table3 = calculate_protected_needed_at_each_position(0.3, k, alpha)
        self.assertEqual(table3, [0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2])
        table4 = calculate_protected_needed_at_each_position(0.4, k, alpha)
        self.assertEqual(table4, [0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 3])
        table5 = calculate_protected_needed_at_each_position(0.5, k, alpha)
        self.assertEqual(table5, [0, 0, 0, 1, 1, 1, 2, 2, 3, 3, 3, 4])
        table6 = calculate_protected_needed_at_each_position(0.6, k, alpha)
        self.assertEqual(table6, [0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5])
        table7 = calculate_protected_needed_at_each_position(0.7, k, alpha)
        self.assertEqual(table7, [0, 1, 1, 2, 2, 3, 3, 4, 5, 5, 6, 6])

        for p in 0.1, 0.3, 0.5, 0.7, 0.9:
            for k in 100, 1000, 2000:
                for alpha in 0.05, 0.1, 0.15, 0.2:
                    # print("p=%.1f, k=%d, alpha=%.2f" % (p, k, alpha))
                    table_down = calculate_protected_needed_at_each_position(p, k, alpha)
                    table_up = calculate_protected_needed_at_each_position(p + 0.05, k, alpha)
                    self.assertEqual(len(table_down), len(table_up))
                    for i in range(len(table_up)):
                        self.assertTrue(table_up[i] >= table_down[i])

    def test_proportion_false(self):
        results2 = [False, False, False, False, True]
        self.assertEqual(proportion_false(results2), 0.8)

        results4 = [False, True, False, False, True]
        self.assertEqual(proportion_false(results4), 0.6)

        results0 = [True, True, True, True, True, True]
        self.assertEqual(proportion_false(results0), 0.0)

        results10 = [False, False, False]
        self.assertEqual(proportion_false(results10), 1.0)

        results1 = [True, True, True, True, True, True, True, False, True, True]
        self.assertEqual(proportion_false(results1), 0.1)


if __name__ == '__main__':
    unittest.main()
