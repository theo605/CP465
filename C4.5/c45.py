import numpy as np
from math import log2
import pandas as pd


class Node:
    def __init__(self, attr, isleaf):
        self.value = attr
        self.children = []
        self.isleaf = isleaf


class C45:
    def __init__(self, df):
        self.data = df
        self.classes = self.__get_classes(df)
        self.attributes = self.__get_attributes(df)
        self.root = Node('root', False)


    def __get_classes(self, df):
        return np.unique(df['class'])


    def __get_attributes(self, df):
        attrs = list(df.columns)
        attrs.remove('class')
        return attrs


    """
    Calcualtes Entropy for a given set of labels.
    @param: labels(array-like)
    @return: ent(float)
    """
    def entropy(self, labels):
        ent = 0
        n_labels = len(labels)
        # If only one or no data in the dataset, return 0
        if n_labels <= 1:
            return ent

        # list of count of unique values in labels
        values, counts = np.unique(labels, return_counts=True)
        # If only one unique value in dataset, return 0
        # print('counts', counts)
        if len(values) == 1:
            return ent

        # list of probability of each class
        probs = counts / n_labels
        # print('probs', probs)
        # calculate entropy
        for p in probs:
            ent -= p * log2(p)

        return ent

    """
    Calcualtes rem for a given df.
    @param: subset(pandas DataFrame)
    @return: rem(float)
    """
    def remainder(self, attr, subset):
        rem = 0
        attr_col = subset[attr]
        unique_vals, counts = np.unique(attr_col, return_counts=True)
        probs = counts / len(attr_col)
        for i in range(len(unique_vals)):
            # temp df to store filtered values
            temp_df = subset[subset[attr]==unique_vals[i]]
            local_ent = self.entropy(temp_df['class'])
            rem += local_ent * probs[i]
        return rem


    """
    Calcualtes information gain for a given df
    @param: subset(DataFrame)
    @return: float
    """
    def info_gain(self, attr, subset):
        ent = self.entropy(subset['class'])
        rem = self.remainder(attr, subset)
        return ent - rem


    """
    Calculates information gain ratio for a given df
    @param: subset(DataFrame with only 2 columns)
    @return: gain_ratio(float)
    """
    def gain_ratio(self, attr, subset):
        den = self.entropy(subset[attr])
        if den == 0:
            return 1
        num = self.info_gain(attr, subset)
        gr = num / den
        # print('num: {} den: {}'.format(num, den))
        return gr


    def pick_best_attr(self, df):
        max_gr = -1
        best_attr = None

        for attr in self.attributes:
            subset = df.loc[:, [attr, 'class']]
            gr = self.gain_ratio(attr, subset)
            if gr > max_gr:
                max_gr = gr
                best_attr = attr
        return best_attr


    def split_data_on_attribute(self, df):

        best_attr = self.pick_best_attr(df)

        attr_col = df[best_attr]
        unique_vals = np.unique(attr_col)
        children_datasets = {}
        for val in unique_vals:
            children_datasets[val] = df[df[best_attr]==val]
        return best_attr, children_datasets


    def is_label_classified(self, subset):
        values = np.unique(subset['class'])
        if len(values) == 1:
            print(values, 'all labels classified')
            return '[{}]'.format(values[0])
        else:
            return False
    

    def get_majority(self, labels):
        values, counts = np.unique(labels, return_counts=True)
        marj = values[np.where(counts == max(counts))][0]
        marj = '[{}]'.format(marj)
        print('majority is', marj)
        return marj


    def plant_tree(self):
        tree = Node('Root', False)
        usable_attrs = self.attributes
        # df = self.data
        if not self.data.empty:
            tree.children = self.grow_tree(self.data, usable_attrs)
        self.tree = tree


    def grow_tree(self, df, usable_attrs):

        children_nodes = []
        print(usable_attrs)
        # If df is empty
        if df.empty:
            return children_nodes

        # If all labels have the same value
        classified = self.is_label_classified(df)
        if classified is not False:
            children_nodes.append(Node(classified, True))

        # If no more attributes to split
        elif len(usable_attrs) == 0:
            children_nodes.append(Node(self.get_majority(df['class']), True))

        # If dataset can be split further
        else:
            best_attr, children_datasets = self.split_data_on_attribute(df)
            usable_attrs.remove(best_attr)
            print('BEST:', best_attr)
            for key, subset in children_datasets.items():
                node_attr = ':'.join([best_attr, key])
                node = Node(node_attr, False)
                node.children = self.grow_tree(subset, usable_attrs)
                children_nodes.append(node)

        return children_nodes


    def print_tree(self):
        self.__print_node(self.tree)


    def __print_node(self, node, _prefix="", _last=True):
        print(_prefix, "`- " if _last else "|- ", node.value, sep="")
        _prefix += "   " if _last else "|  "
        child_count = len(node.children)
        for i, child in enumerate(node.children):
            _last = i == (child_count - 1)
            self.__print_node(child, _prefix, _last)
