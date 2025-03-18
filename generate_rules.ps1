# Base ICD codes and their categories
$ICD_CODES = @{
    "diabetes" = @("E11.9", "E11.8", "E11.65", "E11.21", "E11.311")
    "hypertension" = @("I10", "I11.9", "I11.0", "I12.9", "I13.0")
    "asthma" = @("J45.901", "J45.902", "J45.909", "J45.998", "J45.20")
    "back_pain" = @("M54.5", "M54.6", "M54.4", "M54.16", "M54.17")
    "gerd" = @("K21.9", "K21.0", "K20.0", "K20.8", "K20.9")
    "depression" = @("F32.9", "F32.0", "F32.1", "F32.2", "F33.0")
    "neuropathy" = @("G62.9", "G63", "G60.9", "G61.9", "G62.0")
    "arthritis" = @("M17.9", "M15.0", "M16.0", "M19.90", "M05.79")
    "infection" = @("A41.9", "A49.9", "B34.9", "A49.0", "A49.1")
}

# Base medications and their NDC codes
$MEDICATIONS = @{
    "metformin" = @("00002-8215-01", "00002-8215-02")
    "lisinopril" = @("00002-8215-03", "00002-8215-04")
    "albuterol" = @("00002-8215-05", "00002-8215-06")
    "amoxicillin" = @("00002-8215-07", "00002-8215-08")
    "cyclobenzaprine" = @("00002-8215-09", "00002-8215-10")
    "omeprazole" = @("00002-8215-11", "00002-8215-12")
    "sertraline" = @("00002-8215-13", "00002-8215-14")
    "gabapentin" = @("00002-8215-15", "00002-8215-16")
    "hydrochlorothiazide" = @("00002-8215-17", "00002-8215-18")
    "ibuprofen" = @("00002-8215-19", "00002-8215-20")
}

# Monitoring advice templates
$MONITORING_ADVICE = @(
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
)

function Get-RandomElement {
    param (
        [array]$Array
    )
    return $Array | Get-Random
}

function Get-RandomRange {
    param (
        [int]$Min,
        [int]$Max
    )
    return Get-Random -Minimum $Min -Maximum ($Max + 1)
}

function Generate-Rule {
    # Select random category and its ICD codes
    $category = Get-RandomElement -Array $ICD_CODES.Keys
    $icdCodes = @(Get-RandomElement -Array $ICD_CODES[$category])
    if ((Get-Random -Minimum 0 -Maximum 2) -eq 1) {
        $icdCodes += Get-RandomElement -Array $ICD_CODES[$category]
    }
    
    # Select random medication
    $medicationName = Get-RandomElement -Array $MEDICATIONS.Keys
    $medicationCodes = @(Get-RandomElement -Array $MEDICATIONS[$medicationName])
    if ((Get-Random -Minimum 0 -Maximum 2) -eq 1) {
        $medicationCodes += Get-RandomElement -Array $MEDICATIONS[$medicationName]
    }
    
    # Generate age range based on condition
    switch ($category) {
        "asthma" {
            $minAge = Get-RandomRange -Min 2 -Max 12
            $maxAge = Get-RandomRange -Min ($minAge + 10) -Max 80
        }
        { $_ -in @("depression", "arthritis") } {
            $minAge = Get-RandomRange -Min 18 -Max 40
            $maxAge = Get-RandomRange -Min ($minAge + 20) -Max 90
        }
        default {
            $minAge = Get-RandomRange -Min 0 -Max 50
            $maxAge = Get-RandomRange -Min ($minAge + 15) -Max 100
        }
    }
    
    # Generate confidence score
    $confidenceScore = [math]::Round((Get-Random -Minimum 75 -Maximum 98) / 100, 2)
    
    # Generate recommendation
    $recommendation = "Consider prescribing $($medicationName.Substring(0,1).ToUpper() + $medicationName.Substring(1)) for $($category -replace '_', ' '). "
    $recommendation += Get-RandomElement -Array $MONITORING_ADVICE
    
    return @{
        icdCodes = $icdCodes
        ndcCodes = $medicationCodes
        minAge = $minAge
        maxAge = $maxAge
        recommendation = $recommendation
        confidenceScore = $confidenceScore
        active = $true
    }
}

$rules = @()
1..1000 | ForEach-Object {
    $rules += Generate-Rule
}

$output = @{
    rules = $rules
}

$jsonOutput = $output | ConvertTo-Json -Depth 10
Set-Content -Path "src/main/resources/default-rules.json" -Value $jsonOutput 