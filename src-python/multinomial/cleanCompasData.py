'''
Created on May 19, 2020

@author: meike
'''
import pandas as pd
import os


def main():
    print(os.getcwd())
    data = pd.read_csv("../../src-java/multinomial/data/COMPAS/compas-scores-two-years.csv", header=0)
    # we do the same data cleaning as is done by ProPublica. See link for details
    # https://github.com/propublica/compas-analysis/blob/master/Compas%20Analysis.ipynb
    data = data[data["days_b_screening_arrest"] <= 30]
    data = data[data["days_b_screening_arrest"] >= -30]
    data = data[data["is_recid"] != -1]
    data = data[data["c_charge_degree"] != "O"]
    data = data[data["score_text"] != "N/A"]

    # drop irrelevant columns
    keep_cols = ["sex", "age_cat", "race", "decile_score", "v_decile_score"]
    data = data[keep_cols]
    data["sex"] = data["sex"].replace({"Male":0,
                                       "Female":1})
    data["age_cat"] = data["age_cat"].replace({"Less than 25":0,
                                               "25 - 45":1,
                                               "Greater than 45":2})
    data["race"] = data["race"].replace({"Caucasian":0,
                                         "African-American":1,
                                         "Hispanic":2,
                                         "Asian":3,
                                         "Native American":4,
                                         "Other":5})
    data.to_csv("../../src-java/multinomial/data/COMPAS/compas_sexAgeRace.csv", header=True, index=False)


if __name__ == '__main__':
    main()
