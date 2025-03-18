import json
import random
import itertools
from typing import List, Dict

# Base ICD codes and their categories
ICD_CODES = {
    "diabetes": ["E11.9", "E11.8", "E11.65", "E11.21", "E11.311"],
    "hypertension": ["I10", "I11.9", "I11.0", "I12.9", "I13.0"],
    "asthma": ["J45.901", "J45.902", "J45.909", "J45.998", "J45.20"],
    "back_pain": ["M54.5", "M54.6", "M54.4", "M54.16", "M54.17"],
    "gerd": ["K21.9", "K21.0", "K20.0", "K20.8", "K20.9"],
    "depression": ["F32.9", "F32.0", "F32.1", "F32.2", "F33.0"],
    "neuropathy": ["G62.9", "G63", "G60.9", "G61.9", "G62.0"],
    "arthritis": ["M17.9", "M15.0", "M16.0", "M19.90", "M05.79"],
    "infection": ["A41.9", "A49.9", "B34.9", "A49.0", "A49.1"]
}

# Base medications and their NDC codes
MEDICATIONS = {
    "metformin": ["00002-8215-01", "00002-8215-02"],
    "lisinopril": ["00002-8215-03", "00002-8215-04"],
    "albuterol": ["00002-8215-05", "00002-8215-06"],
    "amoxicillin": ["00002-8215-07", "00002-8215-08"],
    "cyclobenzaprine": ["00002-8215-09", "00002-8215-10"],
    "omeprazole": ["00002-8215-11", "00002-8215-12"],
    "sertraline": ["00002-8215-13", "00002-8215-14"],
    "gabapentin": ["00002-8215-15", "00002-8215-16"],
    "hydrochlorothiazide": ["00002-8215-17", "00002-8215-18"],
    "ibuprofen": ["00002-8215-19", "00002-8215-20"]
}

# Monitoring advice templates
MONITORING_ADVICE = [
    "Monitor vital signs regularly.",
    "Check liver function periodically.",
    "Monitor blood pressure weekly.",
    "Check kidney function regularly.",
    "Monitor for allergic reactions.",
    "Regular follow-up recommended.",
    "Monitor blood glucose levels.",
    "Check for drug interactions.",
    "Monitor for side effects.",
    "Regular lab tests recommended.",
    "Monitor electrolyte levels.",
    "Check complete blood count monthly.",
    "Monitor heart rate and rhythm.",
    "Check thyroid function periodically.",
    "Monitor weight changes.",
    "Regular physical examination needed.",
    "Monitor mental health status.",
    "Check for signs of infection.",
    "Monitor pain levels daily.",
    "Regular medication review needed."
]

def generate_rule() -> Dict:
    # Select random category and its ICD codes
    category = random.choice(list(ICD_CODES.keys()))
    icd_codes = random.sample(ICD_CODES[category], random.randint(1, 2))
    
    # Select random medication
    medication_name = random.choice(list(MEDICATIONS.keys()))
    medication_codes = random.sample(MEDICATIONS[medication_name], random.randint(1, 2))
    
    # Generate age range based on condition
    if category in ["asthma"]:
        min_age = random.randint(2, 12)
        max_age = random.randint(min_age + 10, 80)
    elif category in ["depression", "arthritis"]:
        min_age = random.randint(18, 40)
        max_age = random.randint(min_age + 20, 90)
    else:
        min_age = random.randint(0, 50)
        max_age = random.randint(min_age + 15, 100)
    
    # Generate confidence score
    confidence_score = round(random.uniform(0.75, 0.98), 2)
    
    # Generate recommendation
    recommendation = f"Consider prescribing {medication_name.title()} for {category.replace('_', ' ')}. "
    recommendation += random.choice(MONITORING_ADVICE)
    
    return {
        "icdCodes": icd_codes,
        "ndcCodes": medication_codes,
        "minAge": min_age,
        "maxAge": max_age,
        "recommendation": recommendation,
        "confidenceScore": confidence_score,
        "active": True
    }

def generate_rules(count: int = 1000) -> List[Dict]:
    return [generate_rule() for _ in range(count)]

def main():
    rules = generate_rules()
    output = {"rules": rules}
    
    with open('src/main/resources/default-rules.json', 'w') as f:
        json.dump(output, f, indent=2)

if __name__ == "__main__":
    main() 