
import pandas as pd
from mlxtend.frequent_patterns import apriori, association_rules
import pickle

# Load the original dataset
df = pd.read_csv('categorie_data.csv')
# Perform one-hot encoding for the categorical variables
df_encoded = pd.get_dummies(df)
# Apply Apriori algorithm
frequent_itemsets = apriori(df_encoded, min_support=0.001, use_colnames=True)
rules = association_rules(frequent_itemsets, metric="confidence", min_threshold=0.5)
# Filter the association rules to only keep consequents starting with 'BAC'
filtered_rules = rules[rules['consequents'].apply(lambda x: any(item.startswith('BAC') for item in x))]

# Save association rules to a file
with open('association_rule.pkl', 'wb') as f:
    pickle.dump(filtered_rules, f)

