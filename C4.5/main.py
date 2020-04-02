from sklearn.model_selection import train_test_split
import pandas as pd
from c45 import C45

df = pd.read_csv('house-votes-84.csv')
# df = df.iloc[0:101, :]

c45 = C45(df)
c45.plant_tree()

c45.print_tree()