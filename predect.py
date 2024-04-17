
import os
import pickle
import pandas as pd
import sys 

if len(sys.argv) != 2:
    print("Usage: python predict_bac.py <path_to_grades_csv>")
    sys.exit(1)
#read the association rules
with open('association_rule.pkl', 'rb') as f:
    rules = pickle.load(f)

# Define the grade ranges for each exam type
grade_mapping = {
    'T1': {
        'Excellent': (18, 20),
        'Tres bien': (16, 17.99),
        'Bien': (14, 15.99),
        'Assez bien': (12, 13.99),
        'Passable': (10, 11.99),
        'Insuffisant': (6, 9.99),
        'Echec': (0, 5.99)
    },
    'T2': {
        'Excellent': (18, 20),
        'Tres bien': (16, 17.99),
        'Bien': (14, 15.99),
        'Assez bien': (12, 13.99),
        'Passable': (10, 11.99),
        'Insuffisant': (6, 9.99),
        'Echec': (0, 5.99)
    },
    'T3': {
        'Excellent': (18, 20),
        'Tres bien': (16, 17.99),
        'Bien': (14, 15.99),
        'Assez bien': (12, 13.99),
        'Passable': (10, 11.99),
        'Insuffisant': (6, 9.99),
        'Echec': (0, 5.99)
    }
}

# Function to map numerical values to grade categories
def numerical_to_grade(exam_type, numerical_grade):
    for grade, (lower, upper) in grade_mapping[exam_type].items():
        if lower <= numerical_grade <= upper:
            return grade
    return None


csv_path = sys.argv[1]
#read the grades that were transferred from the clients
student_df = pd.read_csv(csv_path)

# Convert columns to numeric
student_df['T1'] = pd.to_numeric(student_df['T1'], errors='coerce')
student_df['T2'] = pd.to_numeric(student_df['T2'], errors='coerce')
student_df['T3'] = pd.to_numeric(student_df['T3'], errors='coerce')
student_df.fillna(0.0, inplace=True)
#add a copy of the student dataframe to save the prediction later
students = student_df
# Apply grade mapping to each column in the DataFrame
for index, student_row in student_df.iterrows():
    for exam_type in ("T1", "T2", "T3"):
        grade_category = numerical_to_grade(exam_type, student_row[exam_type])
        if grade_category:
            student_df.at[index, exam_type] = f'{exam_type}_{grade_category}'
#sort the rules by confidence to get the highest scoring rules first
rules_sorted = rules.sort_values(by='confidence', ascending=False)

# Function to convert a row into the desired dictionary format
def row_to_dict(row):
    result = {}
    for col_name in row.index:
        key = f"{row[col_name]}"
        result[key] = 1
    return result

# Iterate over each row in the dataframe
print(student_df)
for index, student_row in student_df.iterrows():
    # Initialize predicted BAC and considered rules for each student
    predicted_bac = None
    considered_rules = []
    new_student_categorical = row_to_dict(student_row)
    
    # Check if any of the association rules match the items in the student's row
    for _, rule in rules_sorted.iterrows():
        antecedents = set(rule['antecedents'])
        if all(item in new_student_categorical for item in antecedents):
            considered_rules.append(rule)
            predicted_bac = rule['consequents']

    # Append the predicted BAC for the current student to the predictions list
    if predicted_bac:
        students.at[index,'bac_prediction'] = ','.join([item for item in predicted_bac if item.startswith('BAC_')])
    else:
        students.at[index,'bac_prediction'] = None
    
# Add the predictions list as a new column 'bac_prediction' to the dataframe

file_name = os.path.basename(csv_path)
file_name_without_extension = file_name.split('.')[0]
new_file_path = f"last_files/{file_name_without_extension}_predicted.csv"
print(f"DataFrame saved to: {new_file_path}")
students.to_csv(new_file_path, index=False)

