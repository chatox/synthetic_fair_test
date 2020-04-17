'''
Created on Apr 17, 2020

@author: meike
'''
import matplotlib as mpl
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker

import pandas as pd
import numpy as np

mpl.rcParams.update({'font.size': 22, 'lines.linewidth': 3, 'lines.markersize': 15, 'font.family': 'Times New Roman'})
    # avoid type 3 (i.e. bitmap) fonts in figures
mpl.rcParams['ps.useafm'] = True
mpl.rcParams['pdf.use14corefonts'] = True
mpl.rcParams['text.usetex'] = True

data = pd.read_csv('data_030303_01_2')
x = data['k']
y = data['adjustedAlpha']

_, ax = plt.subplots()
ax.plot(x, y, 'o', c='C1', markersize=5)
# plt.ticklabel_format(axis='y', scilimits=(0, 0), useOffset=True)

plt.xlabel('k')
plt.ylabel('alpha')
plt.legend(('p1=p2=p3=1/3',), loc="best", prop={'size': 15})
plt.tight_layout()
plt.savefig('alpha_030303_01.png', dpi=300, bbox_inches='tight', pad_inches=0)

#########################################################################################
# COMPUTATION TIME PLOT
#########################################################################################
data = pd.read_csv('computationTimeRegression.csv')
x1 = data['k']
y1 = data['time']

data2 = pd.read_csv('computationTimeBinary.csv')
x2 = data2['k']
y2 = data2['time']

_, ax = plt.subplots()
ax.plot(x1, y1, c='C1', markersize=5)
ax.plot(x2, y2, c='C2', markersize=5)
# ax.xaxis.set_major_locator(ticker.MultipleLocator(3))
plt.xlabel('k')
plt.ylabel('seconds')
plt.legend(('regression', 'binary search'), loc="best", prop={'size': 15})
plt.tight_layout()
plt.savefig('computationTime.png', dpi=300, bbox_inches='tight', pad_inches=0)

###########################################################################################
# FAILPROB BINOMIAL
##########################################################################################

###########################################################################################
# FAILPROB MULTINOMIAL
##########################################################################################
