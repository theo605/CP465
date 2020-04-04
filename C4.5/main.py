import pandas as pd
from c45 import C45

# Read data in from .csv using pandas
data = pd.read_csv("house-votes-84.csv")
# Call C45
c45 = C45(_data = data, _targetAttribute = list(data.columns)[-1])
c45.buildTreeInit(trainingSet = data)
# Print decision tree
c45.printTree()
