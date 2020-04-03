import numpy as np
import math
import pandas as pd
import operator as op

class Node:
    def __init__(self,
                _parent = None,
                _children = [],
                _attribute = None,
                _valuesTaken = {}):
        self.parent = _parent
        self.children = _children
        self.attribute = _attribute
        self.valuesTaken = _valuesTaken

    def addChild(self, _node = None):
        if(_node == None):
            _node = self.__init__()
        self.children.append(_node)
        return _node

    def isLeaf(self):
        return not self.children

    def _printTree(self, space):
        if(self.isLeaf()):
            print(str(space*' ') + '<' +str(self.valuesTaken[self.parent.attribute]) + '>')
            print(str( (4+space)*' ') + '(' +str(self.valuesTaken[self.attribute]) + ')')
            return
        else:
            print(str(space*' ') + '<' +str(self.valuesTaken[self.parent.attribute]) + '>')
            print(str((4+space)*' ') + str(self.attribute)) 
            for child in self.children:
                child._printTree(space+4)

class C45:
    def __init__(self, _data, _targetAttribute = None):
        self.data = _data
        self.attributes = self.__get_attributes(_data)
        self.targetAttribute = _targetAttribute
        self.root = Node()

    def __get_attributes(self, df): # insert attributes into array, minus the last one which is what we're focusing on
        attrs = list(df.columns)
        del attrs[-1]
        return attrs

    def getValuesInAttribute(self, data, attr): # find all values under a single attribute
        return list(set(data.loc[:, attr]))

    def getValueInstance(self, data, attr, targetValue):
        count = 0
        for value in data.loc[:,attr]:
            if(targetValue == value):
                count +=1
        return count

    def filterDataFrame(self, data, attr, value):
        filteredData = data # set filteredData to remaining dataset
        filteredData = filteredData[filteredData[attr] == value]
        return filteredData

    def entropy(self, data):
        valueSet = self.getValuesInAttribute(data, self.targetAttribute)
        valueMap = dict.fromkeys(valueSet, 0)
        instances = len(data)

        for value in data.loc[:,self.targetAttribute]:
            valueMap[value]+=1

        entropy = 0
        for value in valueSet:
            entropy += -valueMap[value]/instances * math.log(valueMap[value]/instances,2)
        return entropy


    def info_gain(self, data, attr):
        gain = self.entropy(data)
        instances = len(data)
        for value in self.getValuesInAttribute(data, attr):
            gain = gain - (self.getValueInstance(data, attr, value)/instances) * self.entropy(self.filterDataFrame(data, attr, value))
        return gain

    def split_info(self, data, attr):
        valueSet = self.getValuesInAttribute(data, attr)
        valueMap = dict.fromkeys(valueSet, 0)
        instances = self.getManyInstances(data)

        for value in data.loc[:,attr]:
            valueMap[value] += 1

        splitInfoAttr = 0
        for value in self.getValuesInAttribute(data, attr):
            splitInfoAttr -= valueMap[value]/instances * math.log(valueMap[value]/instances,2)

        return splitInfoAttr


    def gain_ratio(self, data, attr):
        return self.info_gain(data, attr) / self.split_info(data, attr)


    def buildTreeInit(self, trainingSet = None):
        self.buildTree(self.root, trainingSet, self.attributes)

    def buildTree(  self,
                    curr_node = None,
                    trainingSet = None,
                    attr_set = None
                    ):

        dataset = trainingSet
        # handle pruning
        if self.entropy(dataset) == 0.0 : # case in which we find leaf node
            curr_node.attribute = self.targetAttribute # attribute to target
            try:
                curr_node.valuesTaken[curr_node.attribute] = self.getValuesInAttribute(dataset, curr_node.attribute)[0]
            except:
                print("List index out of range! BECAUSE NO SUCH KIND OF ")
                print("Dataset", dataset)
                print("values taken", curr_node.valuesTaken.items())
                print("curr_node", curr_node.attribute)
                print(self.getValuesInAttribute(dataset, curr_node.attribute))
                return
            curr_node.children = []
            return
        elif not attr_set: # found outlier
            print("Entropy not 0 but already ran out attributes")
            curr_node.attribute = self.targetAttribute
            curr_node.valuesTaken[curr_node.attribute] = self.mostValue(data, curr_node.attribute)
            curr_node.children = []
            return

        # we have a non-leaf node and a non-empty attribute set, which means at this point we should split

        best_node = (None, -999) # best node -> (attr, info gain val)
        for attr in attr_set:
            candidateIG = self.info_gain(dataset, attr)
            if (candidateIG > best_node[1]):
                best_node = (attr, candidateIG)
        # find best attribute to split on
        curr_node.attribute = best_node[0]
        vals_set = self.getValuesInAttribute(self.data, best_node[0])
        for value in vals_set:
            temp = dict(curr_node.valuesTaken)
            temp[best_node[0]] = value

            dataset = self.filterDataFrame(trainingSet, curr_node.attribute, value) # filter dataset
            if(dataset.empty):
                continue
            temp_attr_set = attr_set.copy()
            temp_attr_set.remove(curr_node.attribute) # remove the attribute we just split on

            next_node = curr_node.addChild(_node = Node(_parent = curr_node,
                                    _children = [],
                                    _valuesTaken = temp
                                    )) # add a child to the current node because we know that this is a split to a new branch

            self.buildTree(next_node, dataset, set(temp_attr_set)) # recursively move down and generate subtrees


    def mostValue(self, data, attr):
        valueSet = self.getValuesInAttribute(data, attr)
        valueMap = dict.fromkeys(valueSet, 0)
        instances = len(data)

        for value in data.loc[:,attr]:
            valueMap[value] += 1

        return max(valueMap.items(), key=op.itemgetter(1))[0]

    def printTree(self):
        print(">" + str(self.root.attribute))
        for child in self.root.children:
            child._printTree(space = 4)

    def isNan(self, value):
        return math.isnan(value)
