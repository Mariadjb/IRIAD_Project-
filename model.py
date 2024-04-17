import pandas as pd
from mlxtend.frequent_patterns import apriori, association_rules
import pickle
import os

# Path to the folder containing CSV files
folder_path = 'training_files'

# List all CSV files in the folder
csv_files = [file for file in os.listdir(folder_path) if file.endswith('.csv')]

# Initialize an empty list to store DataFrames
dfs = []

# Read each CSV file and append its DataFrame to the list
for file in csv_files:
    file_path = os.path.join(folder_path, file)
    df = pd.read_csv(file_path)
    dfs.append(df)

# Concatenate all DataFrames into a single DataFrame
combined_df = pd.concat(dfs, ignore_index=True)



combined_df = combined_df.dropna(subset=['BAC'])

nan_mask = combined_df[['T1', 'T2', 'T3']].isna().any(axis=1)
# Replace ',' with '.' and convert columns to numeric
combined_df['T1'] = combined_df['T1'].str.replace(',', '.').astype(float)
combined_df['T2'] = combined_df['T2'].str.replace(',', '.').astype(float)
combined_df['BAC'] = combined_df['BAC'].str.replace(',', '.').astype(float)

# Calculate the mean of T1 and T2 for each row
combined_df['T3'] = combined_df[['T1', 'T2']].mean(axis=1)

# Define the classification dictionary
classification = {
    'Excellent': (18, 20),
    'Tres bien': (16, 17.99),
    'Bien': (14, 15.99),
    'Assez bien': (12, 13.99),
    'Passable': (10, 11.99),
    'Insuffisant': (6, 9.99),
    'Échec': (0, 5.99)
}

# Function to assign category based on value
def assign_category(value):
    if isinstance(value, str):
        return value  # Ignore non-numeric values
    for category, (lower, upper) in classification.items():
        if lower <= value <= upper:
            return category
    return None


# Apply the function to each element in the DataFrame
combined_df['T1'] = combined_df['T1'].apply(lambda x: assign_category(x))
combined_df['T2'] = combined_df['T2'].apply(lambda x: assign_category(x))
combined_df['T3'] = combined_df['T3'].apply(lambda x: assign_category(x))
combined_df['BAC'] = combined_df['BAC'].apply(lambda x: assign_category(x))

combined_df.to_csv('training_files/categorie_data_combined.csv', index=False)
# Perform one-hot encoding for the categorical variables
df_encoded = pd.get_dummies(combined_df)
# Apply Apriori algorithm
frequent_itemsets = apriori(df_encoded, min_support=0.001, use_colnames=True)
rules = association_rules(frequent_itemsets, metric="confidence", min_threshold=0.5)
# Filter the association rules to only keep consequents starting with 'BAC'
filtered_rules = rules[rules['consequents'].apply(lambda x: any(item.startswith('BAC') for item in x))]

# Save association rules to a file
with open('association_rule.pkl', 'wb') as f:
    pickle.dump(filtered_rules, f)