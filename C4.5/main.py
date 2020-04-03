import pandas as pd
from c45 import C45

df = pd.read_csv('house-votes-84.csv')

c45 = C45(df, list(df.columns)[-1])
c45.buildTreeInit(trainingSet = df)

c45.printTree()
