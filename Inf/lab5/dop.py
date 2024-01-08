import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
sns.set_style('darkgrid')
sns.set(font_scale=1.3)

xlsx = pd.ExcelFile('Inf/lab5/lab5inf.xlsx')
df = pd.read_excel(xlsx, 'Лист3').drop(columns=['<TICKER>', '<PER>', '<TIME>', '<VOL>'], axis=1)

df1 = df[df['<DATE>'] == '18.09.2018']
sns.boxplot(data=df1)
plt.yticks(np.arange(108000,112000,500))
plt.show()
df2 = df[df['<DATE>'] == '18.10.2018']
sns.boxplot(data=df2)
plt.yticks(np.arange(112000,116000,500))
plt.show()
df3 = df[df['<DATE>'] == '20.11.2018']
sns.boxplot(data=df3)
plt.yticks(np.arange(109000,114000,500))
plt.show()
df4 = df[df['<DATE>'] == '18.12.2018']
sns.boxplot(data=df4)
plt.yticks(np.arange(108000,111000,500))
plt.show()