import pandas as pd
from c45 import C45

data = pd.read_csv("house-votes-84.csv")

c45 = C45(_data = data, _targetAttribute = list(data.columns)[-1])
c45.buildTreeInit(trainingSet = data)

c45.printTree()
