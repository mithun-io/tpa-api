
RulesController: localhost:8080/api/v1/rules

GET: /{id}
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "ruleKey": "MAX_CLAIM_AMOUNT",
    "ruleValue": "50000",
    "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "groovyScript": "return claim.amount <= 50000;",
    "ruleType": "PRE_AUTHORIZATION",
    "priority": 1,
    "version": 1,
    "active": true,
    "simulationMode": true,
    "category": "FINANCIAL_LIMITS",
    "lastUpdatedBy": "Standard string input",
    "createdAt": "2026-05-28T10:30:00Z",
    "updatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


PUT: /{id}
parameters
id: 101
request
{
  "id": 101,
  "ruleKey": "MAX_CLAIM_AMOUNT",
  "ruleValue": "50000",
  "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "groovyScript": "return claim.amount <= 50000;",
  "ruleType": "PRE_AUTHORIZATION",
  "priority": 1,
  "version": 1,
  "active": true,
  "simulationMode": true,
  "category": "FINANCIAL_LIMITS",
  "lastUpdatedBy": "Standard string input",
  "createdAt": "2026-05-28T10:30:00Z",
  "updatedAt": "2026-05-28T10:30:00Z"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "ruleKey": "MAX_CLAIM_AMOUNT",
    "ruleValue": "50000",
    "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "groovyScript": "return claim.amount <= 50000;",
    "ruleType": "PRE_AUTHORIZATION",
    "priority": 1,
    "version": 1,
    "active": true,
    "simulationMode": true,
    "category": "FINANCIAL_LIMITS",
    "lastUpdatedBy": "Standard string input",
    "createdAt": "2026-05-28T10:30:00Z",
    "updatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


DELETE: /{id}
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "ruleKey": "MAX_CLAIM_AMOUNT",
      "ruleValue": "50000",
      "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "groovyScript": "return claim.amount <= 50000;",
      "ruleType": "PRE_AUTHORIZATION",
      "priority": 1,
      "version": 1,
      "active": true,
      "simulationMode": true,
      "category": "FINANCIAL_LIMITS",
      "lastUpdatedBy": "Standard string input",
      "createdAt": "2026-05-28T10:30:00Z",
      "updatedAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


POST: /
request
{
  "id": 101,
  "ruleKey": "MAX_CLAIM_AMOUNT",
  "ruleValue": "50000",
  "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "groovyScript": "return claim.amount <= 50000;",
  "ruleType": "PRE_AUTHORIZATION",
  "priority": 1,
  "version": 1,
  "active": true,
  "simulationMode": true,
  "category": "FINANCIAL_LIMITS",
  "lastUpdatedBy": "Standard string input",
  "createdAt": "2026-05-28T10:30:00Z",
  "updatedAt": "2026-05-28T10:30:00Z"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "ruleKey": "MAX_CLAIM_AMOUNT",
    "ruleValue": "50000",
    "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "groovyScript": "return claim.amount <= 50000;",
    "ruleType": "PRE_AUTHORIZATION",
    "priority": 1,
    "version": 1,
    "active": true,
    "simulationMode": true,
    "category": "FINANCIAL_LIMITS",
    "lastUpdatedBy": "Standard string input",
    "createdAt": "2026-05-28T10:30:00Z",
    "updatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


POST: /simulate
request
{
  "policyId": "PID-500123",
  "policyName": "Comprehensive Platinum Health Plan",
  "policyNumber": "POL-ABC-998877",
  "policyStatus": "ACTIVE",
  "claimFormPresent": true,
  "claimFormPatientName": "Alexander Smith",
  "claimFormHospitalName": "Metro General Medical Center",
  "claimFormAdmissionDate": "2026-05-28",
  "claimFormDischargeDate": "2026-05-28",
  "combinedDocumentPresent": true,
  "combinedDocPatientName": "Alexander Smith",
  "combinedDocHospitalName": "Metro General Medical Center",
  "combinedDocAdmissionDate": "2026-05-28",
  "combinedDocDischargeDate": "2026-05-28",
  "claimedAmount": 4500.75,
  "totalBillAmount": 4500.75,
  "carrierName": "Global Health Insurance Partners",
  "claimType": "REIMBURSEMENT",
  "diagnosis": "Acute Bronchitis (J20.9)",
  "billNumber": "BILL-2026-001A",
  "billDate": "2026-05-28",
  "isDuplicate": false
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "claimStatus": "SUBMITTED",
    "reasons": [
      "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
    ]
  },
  "httpStatus": 42
}


POST: /seed
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


POST: /evaluate
request
{
  "policyId": "PID-500123",
  "policyName": "Comprehensive Platinum Health Plan",
  "policyNumber": "POL-ABC-998877",
  "policyStatus": "ACTIVE",
  "claimFormPresent": true,
  "claimFormPatientName": "Alexander Smith",
  "claimFormHospitalName": "Metro General Medical Center",
  "claimFormAdmissionDate": "2026-05-28",
  "claimFormDischargeDate": "2026-05-28",
  "combinedDocumentPresent": true,
  "combinedDocPatientName": "Alexander Smith",
  "combinedDocHospitalName": "Metro General Medical Center",
  "combinedDocAdmissionDate": "2026-05-28",
  "combinedDocDischargeDate": "2026-05-28",
  "claimedAmount": 4500.75,
  "totalBillAmount": 4500.75,
  "carrierName": "Global Health Insurance Partners",
  "claimType": "REIMBURSEMENT",
  "diagnosis": "Acute Bronchitis (J20.9)",
  "billNumber": "BILL-2026-001A",
  "billDate": "2026-05-28",
  "isDuplicate": false
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "claimStatus": "SUBMITTED",
    "reasons": [
      "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
    ]
  },
  "httpStatus": 42
}


PATCH: /{id}/deactivate
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "ruleKey": "MAX_CLAIM_AMOUNT",
    "ruleValue": "50000",
    "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "groovyScript": "return claim.amount <= 50000;",
    "ruleType": "PRE_AUTHORIZATION",
    "priority": 1,
    "version": 1,
    "active": true,
    "simulationMode": true,
    "category": "FINANCIAL_LIMITS",
    "lastUpdatedBy": "Standard string input",
    "createdAt": "2026-05-28T10:30:00Z",
    "updatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


PATCH: /{id}/activate
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "ruleKey": "MAX_CLAIM_AMOUNT",
    "ruleValue": "50000",
    "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "groovyScript": "return claim.amount <= 50000;",
    "ruleType": "PRE_AUTHORIZATION",
    "priority": 1,
    "version": 1,
    "active": true,
    "simulationMode": true,
    "category": "FINANCIAL_LIMITS",
    "lastUpdatedBy": "Standard string input",
    "createdAt": "2026-05-28T10:30:00Z",
    "updatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /audits/simulations
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "ruleKey": "MAX_CLAIM_AMOUNT",
      "ruleType": "PRE_AUTHORIZATION",
      "ruleVersion": 1,
      "inputStatus": "Standard string input",
      "outputStatus": "Standard string input",
      "reasons": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "simulation": true,
      "fired": true,
      "executionTimeMs": 42,
      "executedBy": "Standard string input",
      "executedAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /audits/rules/{ruleKey}
parameters
ruleKey: MAX_CLAIM_AMOUNT
pageable: value
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "claimId": 101,
        "ruleKey": "MAX_CLAIM_AMOUNT",
        "ruleType": "PRE_AUTHORIZATION",
        "ruleVersion": 1,
        "inputStatus": "Standard string input",
        "outputStatus": "Standard string input",
        "reasons": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "simulation": true,
        "fired": true,
        "executionTimeMs": 42,
        "executedBy": "Standard string input",
        "executedAt": "2026-05-28T10:30:00Z"
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}


GET: /audits/claims/{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "ruleKey": "MAX_CLAIM_AMOUNT",
      "ruleType": "PRE_AUTHORIZATION",
      "ruleVersion": 1,
      "inputStatus": "Standard string input",
      "outputStatus": "Standard string input",
      "reasons": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "simulation": true,
      "fired": true,
      "executionTimeMs": 42,
      "executedBy": "Standard string input",
      "executedAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /active
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "ruleKey": "MAX_CLAIM_AMOUNT",
      "ruleValue": "50000",
      "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "groovyScript": "return claim.amount <= 50000;",
      "ruleType": "PRE_AUTHORIZATION",
      "priority": 1,
      "version": 1,
      "active": true,
      "simulationMode": true,
      "category": "FINANCIAL_LIMITS",
      "lastUpdatedBy": "Standard string input",
      "createdAt": "2026-05-28T10:30:00Z",
      "updatedAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}



ClaimsController: localhost:8080/api/v1/claims

PUT: /{claimId}/carrier-approve
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /
parameters
pageable: value
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "userName": "Alexander Smith",
        "userEmail": "contact@globalhealth.com",
        "patientName": "Alexander Smith",
        "hospitalName": "Metro General Medical Center",
        "admissionDate": "2026-05-28",
        "dischargeDate": "2026-05-28",
        "totalBillAmount": 4500.75,
        "policyId": "PID-500123",
        "policyNumber": "POL-ABC-998877",
        "carrierName": "Global Health Insurance Partners",
        "claimStatus": "SUBMITTED",
        "claimType": "REIMBURSEMENT",
        "diagnosis": "Acute Bronchitis (J20.9)",
        "amount": 4500.75,
        "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "reviewedBy": "Standard string input",
        "reviewedAt": "2026-05-28T10:30:00Z",
        "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "createdDate": "2026-05-28T10:30:00Z",
        "processedDate": "2026-05-28T10:30:00Z",
        "riskScore": 85,
        "riskFlags": "LOW_RISK",
        "healthScore": 85,
        "riskLevel": "LOW_RISK",
        "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}


POST: /
request
{
  "policyId": "PID-500123",
  "policyName": "Comprehensive Platinum Health Plan",
  "policyNumber": "POL-ABC-998877",
  "policyStatus": "ACTIVE",
  "claimFormPresent": true,
  "claimFormPatientName": "Alexander Smith",
  "claimFormHospitalName": "Metro General Medical Center",
  "claimFormAdmissionDate": "2026-05-28",
  "claimFormDischargeDate": "2026-05-28",
  "combinedDocumentPresent": true,
  "combinedDocPatientName": "Alexander Smith",
  "combinedDocHospitalName": "Metro General Medical Center",
  "combinedDocAdmissionDate": "2026-05-28",
  "combinedDocDischargeDate": "2026-05-28",
  "claimedAmount": 4500.75,
  "totalBillAmount": 4500.75,
  "carrierName": "Global Health Insurance Partners",
  "claimType": "REIMBURSEMENT",
  "diagnosis": "Acute Bronchitis (J20.9)",
  "billNumber": "BILL-2026-001A",
  "billDate": "2026-05-28",
  "isDuplicate": false
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "userName": "Alexander Smith",
    "userEmail": "contact@globalhealth.com",
    "patientName": "Alexander Smith",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "totalBillAmount": 4500.75,
    "policyId": "PID-500123",
    "policyNumber": "POL-ABC-998877",
    "carrierName": "Global Health Insurance Partners",
    "claimStatus": "SUBMITTED",
    "claimType": "REIMBURSEMENT",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "amount": 4500.75,
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "riskScore": 85,
    "riskFlags": "LOW_RISK",
    "healthScore": 85,
    "riskLevel": "LOW_RISK",
    "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


GET: /{claimId}/queries
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "username": "Alexander Smith",
      "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "carrier": true,
      "timestamp": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


POST: /{claimId}/queries
parameters
claimId: 101
request
{
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "claimId": 101,
    "username": "Alexander Smith",
    "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "carrier": true,
    "timestamp": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


POST: /notify
parameters
userEmail: contact@globalhealth.com
title: Standard string input
message: All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


POST: /bulk-approve
request
[
  42
]

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalProcessed": 42,
    "success": 42,
    "failed": 42
  },
  "httpStatus": 42
}


POST: /broadcast/{claimId}
parameters
claimId: 101
status: SUBMITTED
message: All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "userName": "Alexander Smith",
    "userEmail": "contact@globalhealth.com",
    "patientName": "Alexander Smith",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "totalBillAmount": 4500.75,
    "policyId": "PID-500123",
    "policyNumber": "POL-ABC-998877",
    "carrierName": "Global Health Insurance Partners",
    "claimStatus": "SUBMITTED",
    "claimType": "REIMBURSEMENT",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "amount": 4500.75,
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "riskScore": 85,
    "riskFlags": "LOW_RISK",
    "healthScore": 85,
    "riskLevel": "LOW_RISK",
    "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


DELETE: /{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /{claimId}/timeline
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "fromStatus": "Standard string input",
      "toStatus": "Standard string input",
      "notes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "changedBy": "Standard string input",
      "occurredAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /{claimId}/export
parameters
claimId: 101
response
"Standard string input"


GET: /{claimId}/audits
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "previousStatus": "Standard string input",
      "newStatus": "Standard string input",
      "changedBy": "Standard string input",
      "notes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "changedAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /search
parameters
claimStatus: SUBMITTED
from: 2026-05-28T10:30:00Z
to: 2026-05-28T10:30:00Z
minAmount: 4500.75
maxAmount: 4500.75
pageable: value
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "userName": "Alexander Smith",
        "userEmail": "contact@globalhealth.com",
        "patientName": "Alexander Smith",
        "hospitalName": "Metro General Medical Center",
        "admissionDate": "2026-05-28",
        "dischargeDate": "2026-05-28",
        "totalBillAmount": 4500.75,
        "policyId": "PID-500123",
        "policyNumber": "POL-ABC-998877",
        "carrierName": "Global Health Insurance Partners",
        "claimStatus": "SUBMITTED",
        "claimType": "REIMBURSEMENT",
        "diagnosis": "Acute Bronchitis (J20.9)",
        "amount": 4500.75,
        "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "reviewedBy": "Standard string input",
        "reviewedAt": "2026-05-28T10:30:00Z",
        "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "createdDate": "2026-05-28T10:30:00Z",
        "processedDate": "2026-05-28T10:30:00Z",
        "riskScore": 85,
        "riskFlags": "LOW_RISK",
        "healthScore": 85,
        "riskLevel": "LOW_RISK",
        "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}



PaymentsController: localhost:8080/api/v1/payments

POST: /verify
request
{
  "razorpay_order_id": "ID-778899",
  "razorpay_payment_id": "ID-778899",
  "razorpay_signature": "abc123signaturehashxyz"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "claimId": 101,
    "amount": 4500.75,
    "currency": "USD",
    "status": "SUBMITTED",
    "razorpayOrderId": "ID-778899",
    "razorpayPaymentId": "ID-778899",
    "createdAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


POST: /create-order
request
{
  "claimId": 101,
  "amount": 4500.75
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "orderId": "ID-778899",
    "amount": 4500.75,
    "currency": "USD",
    "key": "Standard string input",
    "claimId": 101
  },
  "httpStatus": 42
}


GET: /claim/{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "claimId": 101,
    "amount": 4500.75,
    "currency": "USD",
    "status": "SUBMITTED",
    "razorpayOrderId": "ID-778899",
    "razorpayPaymentId": "ID-778899",
    "createdAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}



NotificationsController: localhost:8080/api/v1/notifications

POST: /mark-read
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


PATCH: /{id}/read
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "title": "Standard string input",
      "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "read": true,
      "createdAt": "2026-05-28T10:30:00Z",
      "targetUrl": "Standard string input"
    }
  ],
  "httpStatus": 42
}


GET: /unread-count
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}



MedicalController: localhost:8080/api/v1/medical

POST: /validate
request
{
  "icdCode": "J20.9",
  "diagnosis": "Acute Bronchitis (J20.9)",
  "claimedAmount": 4500.75
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "icdCode": "J20.9",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "validationIssues": [
      "ID-778899"
    ],
    "upcodingWarnings": [
      "Standard string input"
    ],
    "highRisk": true,
    "medicalRiskScore": 85,
    "overallStatus": "Standard string input",
    "validatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


POST: /validate/batch
request
[
  {
    "icdCode": "J20.9",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "claimedAmount": 4500.75
  }
]

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalValidated": 42,
    "flaggedCount": 42,
    "cleanCount": 42,
    "results": [
      {
        "icdCode": "J20.9",
        "diagnosis": "Acute Bronchitis (J20.9)",
        "validationIssues": [
          "ID-778899"
        ],
        "upcodingWarnings": [
          "Standard string input"
        ],
        "highRisk": true,
        "medicalRiskScore": 85,
        "overallStatus": "Standard string input",
        "validatedAt": "2026-05-28T10:30:00Z"
      }
    ],
    "validatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /upcoding/risk
parameters
icdCode: J20.9
claimedAmount: 4500.75
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "icdCode": "J20.9",
    "icdDescription": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "claimedAmount": 4500.75,
    "upcodingRisk": true,
    "warnings": [
      "Standard string input"
    ],
    "riskLevel": "LOW_RISK",
    "analyzedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /high-risk/codes
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalCodes": 42,
    "codes": [
      {
        "code": "Standard string input",
        "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      }
    ],
    "retrievedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /codes/lookup
parameters
code: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "code": "Standard string input",
    "found": true,
    "description": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "highRisk": true,
    "medicalRiskScore": 85,
    "retrievedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}



FilesController: localhost:8080/api/v1/files

POST: /upload
parameters
claimId: 101
documentType: medical_record.pdf (Binary File Attached)
request (multipart/form-data)
{
  "file": "medical_document.pdf (Binary File)",
  "type": "MEDICAL_REPORT"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "fileName": "medical_record.pdf (Binary File Attached)",
    "fileType": "medical_record.pdf (Binary File Attached)",
    "documentType": "medical_record.pdf (Binary File Attached)",
    "validationStatus": "ID-778899",
    "validationIssues": "ID-778899",
    "confidenceScore": 85,
    "fileUrl": "medical_record.pdf (Binary File Attached)"
  },
  "httpStatus": 42
}


POST: /upload/multiple
parameters
claimId: 101
request (multipart/form-data)
{
  "file": "medical_document.pdf (Binary File)",
  "type": "MEDICAL_REPORT"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "fileName": "medical_record.pdf (Binary File Attached)",
      "fileType": "medical_record.pdf (Binary File Attached)",
      "documentType": "medical_record.pdf (Binary File Attached)",
      "validationStatus": "ID-778899",
      "validationIssues": "ID-778899",
      "confidenceScore": 85,
      "fileUrl": "medical_record.pdf (Binary File Attached)"
    }
  ],
  "httpStatus": 42
}


GET: /{documentId}
parameters
documentId: 42
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "fileName": "medical_record.pdf (Binary File Attached)",
    "fileType": "medical_record.pdf (Binary File Attached)",
    "documentType": "medical_record.pdf (Binary File Attached)",
    "validationStatus": "ID-778899",
    "validationIssues": "ID-778899",
    "confidenceScore": 85,
    "fileUrl": "medical_record.pdf (Binary File Attached)"
  },
  "httpStatus": 42
}


GET: /download/{documentId}
parameters
documentId: 42
response
"Standard string input"


GET: /claim/{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "fileName": "medical_record.pdf (Binary File Attached)",
      "fileType": "medical_record.pdf (Binary File Attached)",
      "documentType": "medical_record.pdf (Binary File Attached)",
      "validationStatus": "ID-778899",
      "validationIssues": "ID-778899",
      "confidenceScore": 85,
      "fileUrl": "medical_record.pdf (Binary File Attached)"
    }
  ],
  "httpStatus": 42
}



CarrierController: localhost:8080/api/v1/carrier

POST: /claims/{id}/ai-analyze
parameters
id: 101
request
null

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "verdict": "Standard string input",
    "confidence": 42,
    "riskScore": 85,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 4500.75,
      "eligibleAmount": 4500.75
    },
    "flags": [
      "LOW_RISK"
    ],
    "recommendation": "Standard string input",
    "generatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


PATCH: /claims/{id}/validate
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


PATCH: /claims/{id}/remark
parameters
id: 101
request
null

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


PATCH: /claims/{id}/reject
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


PATCH: /claims/{id}/flag
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


PATCH: /claims/{id}/approve
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /claims
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "policyNumber": "POL-ABC-998877",
      "claimId": 101,
      "claimType": "REIMBURSEMENT",
      "claimStatus": "SUBMITTED",
      "amount": 4500.75,
      "totalBillAmount": 4500.75,
      "diagnosis": "Acute Bronchitis (J20.9)",
      "hospitalName": "Metro General Medical Center",
      "admissionDate": "2026-05-28",
      "dischargeDate": "2026-05-28",
      "createdDate": "2026-05-28T10:30:00Z",
      "processedDate": "2026-05-28T10:30:00Z",
      "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "reviewedBy": "Standard string input",
      "reviewedAt": "2026-05-28T10:30:00Z",
      "patientInfo": {
        "name": "Alexander Smith",
        "email": "contact@globalhealth.com",
        "mobile": "+1-800-555-0199",
        "dateOfBirth": "2026-05-28",
        "gender": "MALE",
        "address": "456 Medical Avenue, Suite 100, New York, NY 10001"
      },
      "fraudInfo": {
        "riskScore": 85,
        "riskLevel": "LOW_RISK",
        "healthScore": 85,
        "riskFlags": "LOW_RISK",
        "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      },
      "policyInfo": {
        "policyNumber": "POL-ABC-998877",
        "policyStatus": "ACTIVE",
        "reason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      }
    }
  ],
  "httpStatus": 42
}


GET: /claims/{id}
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "policyNumber": "POL-ABC-998877",
    "claimId": 101,
    "claimType": "REIMBURSEMENT",
    "claimStatus": "SUBMITTED",
    "amount": 4500.75,
    "totalBillAmount": 4500.75,
    "diagnosis": "Acute Bronchitis (J20.9)",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "patientInfo": {
      "name": "Alexander Smith",
      "email": "contact@globalhealth.com",
      "mobile": "+1-800-555-0199",
      "dateOfBirth": "2026-05-28",
      "gender": "MALE",
      "address": "456 Medical Avenue, Suite 100, New York, NY 10001"
    },
    "fraudInfo": {
      "riskScore": 85,
      "riskLevel": "LOW_RISK",
      "healthScore": 85,
      "riskFlags": "LOW_RISK",
      "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
    },
    "policyInfo": {
      "policyNumber": "POL-ABC-998877",
      "policyStatus": "ACTIVE",
      "reason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
    }
  },
  "httpStatus": 42
}


GET: /claims/{id}/policy-status
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "claimId": 101,
    "policyNumber": "POL-ABC-998877",
    "policyStatus": "ACTIVE",
    "reason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}



AiController: localhost:8080/api/v1/ai

POST: /validate-document
parameters
documentType: medical_record.pdf (Binary File Attached)
request (multipart/form-data)
{
  "file": "medical_document.pdf (Binary File)",
  "type": "MEDICAL_REPORT"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "status": "SUBMITTED",
    "issues": [
      "Standard string input"
    ],
    "confidenceScore": 85,
    "icdCode": "J20.9"
  },
  "httpStatus": 42
}


POST: /validate-claim
request
{
  "patientName": "Alexander Smith",
  "hospitalName": "Metro General Medical Center",
  "policyNumber": "POL-ABC-998877",
  "amount": 4500.75,
  "diagnosis": "Acute Bronchitis (J20.9)",
  "admissionDate": "Standard string input",
  "dischargeDate": "Standard string input"
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "verdict": "Standard string input",
    "confidence": 42,
    "riskScore": 85,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 4500.75,
      "eligibleAmount": 4500.75
    },
    "flags": [
      "LOW_RISK"
    ],
    "recommendation": "Standard string input",
    "generatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


POST: /claims/{id}/generate-summary
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


POST: /analyze/{claimId}
parameters
claimId: 101
request
null

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "verdict": "Standard string input",
    "confidence": 42,
    "riskScore": 85,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 4500.75,
      "eligibleAmount": 4500.75
    },
    "flags": [
      "LOW_RISK"
    ],
    "recommendation": "Standard string input",
    "generatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}



AdminController: localhost:8080/api/v1/admin

POST: /kafka/dlq/{eventId}/retry
parameters
eventId: ID-778899
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "success": true,
    "eventId": "ID-778899",
    "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "retriedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


POST: /claims/{id}/ai-chat
parameters
id: 101
request
null

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "verdict": "Standard string input",
    "confidence": 42,
    "riskScore": 85,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 4500.75,
      "eligibleAmount": 4500.75
    },
    "flags": [
      "LOW_RISK"
    ],
    "recommendation": "Standard string input",
    "generatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


PATCH: /users/{id}/unblock
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "name": "Alexander Smith",
    "email": "contact@globalhealth.com",
    "phoneNumber": "+1-800-555-0199",
    "dateOfBirth": "2026-05-28",
    "address": "456 Medical Avenue, Suite 100, New York, NY 10001",
    "gender": "MALE",
    "userRole": "PATIENT",
    "userStatus": "ACTIVE",
    "createdAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


PATCH: /users/{id}/block
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "name": "Alexander Smith",
    "email": "contact@globalhealth.com",
    "phoneNumber": "+1-800-555-0199",
    "dateOfBirth": "2026-05-28",
    "address": "456 Medical Avenue, Suite 100, New York, NY 10001",
    "gender": "MALE",
    "userRole": "PATIENT",
    "userStatus": "ACTIVE",
    "createdAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


PATCH: /claims/{id}/reject
parameters
id: 101
reason: All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "userName": "Alexander Smith",
    "userEmail": "contact@globalhealth.com",
    "patientName": "Alexander Smith",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "totalBillAmount": 4500.75,
    "policyId": "PID-500123",
    "policyNumber": "POL-ABC-998877",
    "carrierName": "Global Health Insurance Partners",
    "claimStatus": "SUBMITTED",
    "claimType": "REIMBURSEMENT",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "amount": 4500.75,
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "riskScore": 85,
    "riskFlags": "LOW_RISK",
    "healthScore": 85,
    "riskLevel": "LOW_RISK",
    "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


PATCH: /claims/{id}/assign-carrier
parameters
id: 101
request
null

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "userName": "Alexander Smith",
    "userEmail": "contact@globalhealth.com",
    "patientName": "Alexander Smith",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "totalBillAmount": 4500.75,
    "policyId": "PID-500123",
    "policyNumber": "POL-ABC-998877",
    "carrierName": "Global Health Insurance Partners",
    "claimStatus": "SUBMITTED",
    "claimType": "REIMBURSEMENT",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "amount": 4500.75,
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "riskScore": 85,
    "riskFlags": "LOW_RISK",
    "healthScore": 85,
    "riskLevel": "LOW_RISK",
    "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


PATCH: /claims/{id}/approve
parameters
id: 101
reason: All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "userName": "Alexander Smith",
    "userEmail": "contact@globalhealth.com",
    "patientName": "Alexander Smith",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "totalBillAmount": 4500.75,
    "policyId": "PID-500123",
    "policyNumber": "POL-ABC-998877",
    "carrierName": "Global Health Insurance Partners",
    "claimStatus": "SUBMITTED",
    "claimType": "REIMBURSEMENT",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "amount": 4500.75,
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "riskScore": 85,
    "riskFlags": "LOW_RISK",
    "healthScore": 85,
    "riskLevel": "LOW_RISK",
    "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


PATCH: /claims/review
request
{
  "claimId": 101,
  "claimStatus": "SUBMITTED",
  "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
}

response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "userName": "Alexander Smith",
    "userEmail": "contact@globalhealth.com",
    "patientName": "Alexander Smith",
    "hospitalName": "Metro General Medical Center",
    "admissionDate": "2026-05-28",
    "dischargeDate": "2026-05-28",
    "totalBillAmount": 4500.75,
    "policyId": "PID-500123",
    "policyNumber": "POL-ABC-998877",
    "carrierName": "Global Health Insurance Partners",
    "claimStatus": "SUBMITTED",
    "claimType": "REIMBURSEMENT",
    "diagnosis": "Acute Bronchitis (J20.9)",
    "amount": 4500.75,
    "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "reviewedBy": "Standard string input",
    "reviewedAt": "2026-05-28T10:30:00Z",
    "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
    "createdDate": "2026-05-28T10:30:00Z",
    "processedDate": "2026-05-28T10:30:00Z",
    "riskScore": 85,
    "riskFlags": "LOW_RISK",
    "healthScore": 85,
    "riskLevel": "LOW_RISK",
    "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


PATCH: /carriers/{id}/reject
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "companyName": "Global Health Insurance Partners",
    "email": "contact@globalhealth.com",
    "phoneNumber": "+1-800-555-0199",
    "companyType": "TPA Provider",
    "licenseNumber": "LIC-112233-MED",
    "registrationNumber": "REG-554433",
    "taxId": "TAX-99-888-777",
    "contactPersonName": "Standard string input",
    "contactPersonPhone": "+1-800-555-0199",
    "website": "https://www.globalhealth.com",
    "userStatus": "ACTIVE"
  },
  "httpStatus": 42
}


PATCH: /carriers/{id}/approve
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "companyName": "Global Health Insurance Partners",
    "email": "contact@globalhealth.com",
    "phoneNumber": "+1-800-555-0199",
    "companyType": "TPA Provider",
    "licenseNumber": "LIC-112233-MED",
    "registrationNumber": "REG-554433",
    "taxId": "TAX-99-888-777",
    "contactPersonName": "Standard string input",
    "contactPersonPhone": "+1-800-555-0199",
    "website": "https://www.globalhealth.com",
    "userStatus": "ACTIVE"
  },
  "httpStatus": 42
}


GET: /users
parameters
search: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "name": "Alexander Smith",
        "email": "contact@globalhealth.com",
        "phoneNumber": "+1-800-555-0199",
        "dateOfBirth": "2026-05-28",
        "address": "456 Medical Avenue, Suite 100, New York, NY 10001",
        "gender": "MALE",
        "userRole": "PATIENT",
        "userStatus": "ACTIVE",
        "createdAt": "2026-05-28T10:30:00Z"
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}


GET: /patients
parameters
username: Alexander Smith
email: contact@globalhealth.com
userStatus: ACTIVE
sortBy: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "name": "Alexander Smith",
        "email": "contact@globalhealth.com",
        "phoneNumber": "+1-800-555-0199",
        "dateOfBirth": "2026-05-28",
        "address": "456 Medical Avenue, Suite 100, New York, NY 10001",
        "gender": "MALE",
        "userRole": "PATIENT",
        "userStatus": "ACTIVE",
        "createdAt": "2026-05-28T10:30:00Z"
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}


GET: /monitoring
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "kafka": null,
    "failedClaims": [
      {
        "id": 101,
        "userName": "Alexander Smith",
        "userEmail": "contact@globalhealth.com",
        "patientName": "Alexander Smith",
        "hospitalName": "Metro General Medical Center",
        "admissionDate": "2026-05-28",
        "dischargeDate": "2026-05-28",
        "totalBillAmount": 4500.75,
        "policyId": "PID-500123",
        "policyNumber": "POL-ABC-998877",
        "carrierName": "Global Health Insurance Partners",
        "claimStatus": "SUBMITTED",
        "claimType": "REIMBURSEMENT",
        "diagnosis": "Acute Bronchitis (J20.9)",
        "amount": 4500.75,
        "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "reviewedBy": "Standard string input",
        "reviewedAt": "2026-05-28T10:30:00Z",
        "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "createdDate": "2026-05-28T10:30:00Z",
        "processedDate": "2026-05-28T10:30:00Z",
        "riskScore": 85,
        "riskFlags": "LOW_RISK",
        "healthScore": 85,
        "riskLevel": "LOW_RISK",
        "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      }
    ],
    "errorLogs": [
      null
    ]
  },
  "httpStatus": 42
}


GET: /kafka/topics
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "topics": [
      {
        "name": "Alexander Smith",
        "partitions": 42,
        "dlq": true
      }
    ],
    "totalTopics": 42,
    "dlqTopics": 42,
    "retrievedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /kafka/pending
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "pendingEvents": [
      {
        "id": 101,
        "eventId": "ID-778899",
        "claimId": 101,
        "stage": "Standard string input",
        "claimStatus": "SUBMITTED",
        "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "metadata": "Standard string input",
        "topic": "Standard string input",
        "receivedAt": "2026-05-28T10:30:00Z",
        "processedAt": "2026-05-28T10:30:00Z",
        "processed": true,
        "retryCount": 42,
        "errorDetails": "Standard string input"
      }
    ],
    "count": 42,
    "retrievedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /kafka/health
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalEventsProcessed": 42,
    "pendingEvents": 42,
    "totalEvents": 42,
    "successRate": 42,
    "dlqMessageCount": 42,
    "kafkaStatus": "Standard string input",
    "stageBreakdown": {
      "claimUploaded": 42,
      "ocrCompleted": 42,
      "aiAnalysisDone": 42,
      "ruleEvaluated": 42,
      "adminApproved": 42,
      "carrierApproved": 42,
      "paymentInitiated": 42,
      "paymentCompleted": 42,
      "rejected": 42
    },
    "retrievedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /kafka/dlq
parameters
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "dlqMessages": [
      {
        "id": 101,
        "eventId": "ID-778899",
        "claimId": 101,
        "stage": "Standard string input",
        "claimStatus": "SUBMITTED",
        "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "metadata": "Standard string input",
        "topic": "Standard string input",
        "receivedAt": "2026-05-28T10:30:00Z",
        "processedAt": "2026-05-28T10:30:00Z",
        "processed": true,
        "retryCount": 42,
        "errorDetails": "Standard string input"
      }
    ],
    "totalDlqMessages": 42,
    "page": 1,
    "size": 10,
    "retrievedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /claims
parameters
claimStatus: SUBMITTED
createdAt: 2026-05-28T10:30:00Z
sortBy: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "userName": "Alexander Smith",
        "userEmail": "contact@globalhealth.com",
        "patientName": "Alexander Smith",
        "hospitalName": "Metro General Medical Center",
        "admissionDate": "2026-05-28",
        "dischargeDate": "2026-05-28",
        "totalBillAmount": 4500.75,
        "policyId": "PID-500123",
        "policyNumber": "POL-ABC-998877",
        "carrierName": "Global Health Insurance Partners",
        "claimStatus": "SUBMITTED",
        "claimType": "REIMBURSEMENT",
        "diagnosis": "Acute Bronchitis (J20.9)",
        "amount": 4500.75,
        "rejectionReason": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "reviewedBy": "Standard string input",
        "reviewedAt": "2026-05-28T10:30:00Z",
        "reviewNotes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
        "createdDate": "2026-05-28T10:30:00Z",
        "processedDate": "2026-05-28T10:30:00Z",
        "riskScore": 85,
        "riskFlags": "LOW_RISK",
        "healthScore": 85,
        "riskLevel": "LOW_RISK",
        "aiSummary": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}


GET: /claims/{id}/ai-summary
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "verdict": "Standard string input",
    "confidence": 42,
    "riskScore": 85,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 4500.75,
      "eligibleAmount": 4500.75
    },
    "flags": [
      "LOW_RISK"
    ],
    "recommendation": "Standard string input",
    "generatedAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}


GET: /carriers
parameters
companyName: Global Health Insurance Partners
userStatus: ACTIVE
sortBy: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalElements": 42,
    "totalPages": 42,
    "first": true,
    "last": true,
    "size": 10,
    "content": [
      {
        "id": 101,
        "companyName": "Global Health Insurance Partners",
        "email": "contact@globalhealth.com",
        "phoneNumber": "+1-800-555-0199",
        "companyType": "TPA Provider",
        "licenseNumber": "LIC-112233-MED",
        "registrationNumber": "REG-554433",
        "taxId": "TAX-99-888-777",
        "contactPersonName": "Standard string input",
        "contactPersonPhone": "+1-800-555-0199",
        "website": "https://www.globalhealth.com",
        "userStatus": "ACTIVE"
      }
    ],
    "number": 42,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 42,
    "pageable": {
      "offset": 42,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 42,
      "pageSize": 42,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 42
}



FraudController: localhost:8080/api/v1/fraud

PATCH: /admin/claims/{id}/safe
parameters
id: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": null,
  "httpStatus": 42
}


GET: /carrier/dashboard
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "dashboardStats": {
      "totalClaims": 42,
      "flagged": 42,
      "highRisk": 42,
      "mediumRisk": 42,
      "lowRisk": 42
    },
    "claims": [
      {
        "claimId": 101,
        "policyNumber": "POL-ABC-998877",
        "amount": 4500.75,
        "riskScore": 85,
        "riskLevel": "LOW_RISK",
        "reasons": [
          "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
        ]
      }
    ]
  },
  "httpStatus": 42
}


GET: /admin/dashboard
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "dashboardStats": {
      "totalClaims": 42,
      "flagged": 42,
      "highRisk": 42,
      "mediumRisk": 42,
      "lowRisk": 42
    },
    "claims": [
      {
        "claimId": 101,
        "policyNumber": "POL-ABC-998877",
        "amount": 4500.75,
        "riskScore": 85,
        "riskLevel": "LOW_RISK",
        "reasons": [
          "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
        ]
      }
    ]
  },
  "httpStatus": 42
}



UsersController: localhost:8080/api/v1/users

GET: /profile
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "id": 101,
    "name": "Alexander Smith",
    "email": "contact@globalhealth.com",
    "phoneNumber": "+1-800-555-0199",
    "dateOfBirth": "2026-05-28",
    "address": "456 Medical Avenue, Suite 100, New York, NY 10001",
    "gender": "MALE",
    "userRole": "PATIENT",
    "userStatus": "ACTIVE",
    "createdAt": "2026-05-28T10:30:00Z"
  },
  "httpStatus": 42
}



AuditController: localhost:8080/api/v1/audit

GET: /range
parameters
from: 2026-05-28T10:30:00Z
to: 2026-05-28T10:30:00Z
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "action": "Standard string input",
      "previousStatus": "Standard string input",
      "newStatus": "Standard string input",
      "timestamp": "2026-05-28T10:30:00Z",
      "performedBy": "Standard string input",
      "details": "Standard string input",
      "integrityHash": "Standard string input",
      "previousHash": "Standard string input",
      "blockchainHash": "Standard string input",
      "ipAddress": "456 Medical Avenue, Suite 100, New York, NY 10001"
    }
  ],
  "httpStatus": 42
}


GET: /payments/reconcile
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalVerifiedPayments": 42,
    "totalAmountSettled": 4500.75,
    "currency": "USD",
    "reconciledAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /payments/payment/{paymentId}
parameters
paymentId: 42
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "paymentId": 42,
      "amount": 4500.75,
      "currency": "USD",
      "paymentEventType": "Standard string input",
      "paymentStatus": "Standard string input",
      "razorpayOrderId": "ID-778899",
      "razorpayPaymentId": "ID-778899",
      "notes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "initiatedBy": "Standard string input",
      "integrityHash": "Standard string input",
      "createdAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /payments/event/{eventType}
parameters
eventType: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "paymentId": 42,
      "amount": 4500.75,
      "currency": "USD",
      "paymentEventType": "Standard string input",
      "paymentStatus": "Standard string input",
      "razorpayOrderId": "ID-778899",
      "razorpayPaymentId": "ID-778899",
      "notes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "initiatedBy": "Standard string input",
      "integrityHash": "Standard string input",
      "createdAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /payments/claim/{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "paymentId": 42,
      "amount": 4500.75,
      "currency": "USD",
      "paymentEventType": "Standard string input",
      "paymentStatus": "Standard string input",
      "razorpayOrderId": "ID-778899",
      "razorpayPaymentId": "ID-778899",
      "notes": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "initiatedBy": "Standard string input",
      "integrityHash": "Standard string input",
      "createdAt": "2026-05-28T10:30:00Z"
    }
  ],
  "httpStatus": 42
}


GET: /events/unprocessed
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "eventId": "ID-778899",
      "claimId": 101,
      "stage": "Standard string input",
      "claimStatus": "SUBMITTED",
      "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "metadata": "Standard string input",
      "topic": "Standard string input",
      "receivedAt": "2026-05-28T10:30:00Z",
      "processedAt": "2026-05-28T10:30:00Z",
      "processed": true,
      "retryCount": 42,
      "errorDetails": "Standard string input"
    }
  ],
  "httpStatus": 42
}


GET: /events/stage/{stage}
parameters
stage: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "eventId": "ID-778899",
      "claimId": 101,
      "stage": "Standard string input",
      "claimStatus": "SUBMITTED",
      "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "metadata": "Standard string input",
      "topic": "Standard string input",
      "receivedAt": "2026-05-28T10:30:00Z",
      "processedAt": "2026-05-28T10:30:00Z",
      "processed": true,
      "retryCount": 42,
      "errorDetails": "Standard string input"
    }
  ],
  "httpStatus": 42
}


GET: /events/claim/{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "eventId": "ID-778899",
      "claimId": 101,
      "stage": "Standard string input",
      "claimStatus": "SUBMITTED",
      "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
      "metadata": "Standard string input",
      "topic": "Standard string input",
      "receivedAt": "2026-05-28T10:30:00Z",
      "processedAt": "2026-05-28T10:30:00Z",
      "processed": true,
      "retryCount": 42,
      "errorDetails": "Standard string input"
    }
  ],
  "httpStatus": 42
}


GET: /claims/{claimId}
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "action": "Standard string input",
      "previousStatus": "Standard string input",
      "newStatus": "Standard string input",
      "timestamp": "2026-05-28T10:30:00Z",
      "performedBy": "Standard string input",
      "details": "Standard string input",
      "integrityHash": "Standard string input",
      "previousHash": "Standard string input",
      "blockchainHash": "Standard string input",
      "ipAddress": "456 Medical Avenue, Suite 100, New York, NY 10001"
    }
  ],
  "httpStatus": 42
}


GET: /claims/{claimId}/verify
parameters
claimId: 101
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "claimId": 101,
    "chainIntact": true,
    "totalRecords": 42,
    "verifiedAt": "Standard string input",
    "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing."
  },
  "httpStatus": 42
}


GET: /claims/{claimId}/action/{action}
parameters
claimId: 101
action: Standard string input
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": [
    {
      "id": 101,
      "claimId": 101,
      "action": "Standard string input",
      "previousStatus": "Standard string input",
      "newStatus": "Standard string input",
      "timestamp": "2026-05-28T10:30:00Z",
      "performedBy": "Standard string input",
      "details": "Standard string input",
      "integrityHash": "Standard string input",
      "previousHash": "Standard string input",
      "blockchainHash": "Standard string input",
      "ipAddress": "456 Medical Avenue, Suite 100, New York, NY 10001"
    }
  ],
  "httpStatus": 42
}



AnalyticsController: localhost:8080/api/v1/analytics

GET: /sla/performance
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalClaims": 42,
    "withinSla": 42,
    "slaBreached": 42,
    "escalated": 42,
    "slaComplianceRate": 42,
    "avgProcessingHours": 42,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /payments/summary
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalSettledAmount": 4500.75,
    "totalSuccessfulSettlements": 42,
    "totalFailedPayments": 42,
    "successRate": 42,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /loss-ratio
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalClaimsPaid": 42,
    "estimatedPremiumPool": 42,
    "lossRatioPercent": 42,
    "lossRatioStatus": "Standard string input",
    "settledClaims": 42,
    "rejectedClaims": 42,
    "totalClaims": 42,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /leakage
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalClaimedAmount": 4500.75,
    "totalApprovedPayout": 42,
    "leakageAmount": 4500.75,
    "leakageRate": 42,
    "amountMismatchCount": 4500.75,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /hospitals
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "topHospitalsByVolume": null,
    "topHospitalsByAmount": null,
    "totalUniqueHospitals": 42,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /fraud/trends
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "riskDistribution": null,
    "fraudRate": 42,
    "topRiskHospitals": null,
    "averageFraudScore": 85,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /forecast
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "dailyAverageLast30Days": 42,
    "forecastNext7Days": 42,
    "forecastNext30Days": 42,
    "historicalData": [
      null
    ],
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


GET: /dashboard
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "totalClaims": 42,
    "totalApprovedPayout": 42,
    "totalClaimAmount": 4500.75,
    "statusDistribution": null,
    "claimsPerDay": [
      {
        "date": "Standard string input",
        "count": 42
      }
    ]
  },
  "httpStatus": 42
}


GET: /carrier/{carrierName}/summary
parameters
carrierName: Global Health Insurance Partners
response
{
  "success": true,
  "message": "All required documents, including medical reports and billing invoices, have been verified and submitted for immediate processing.",
  "data": {
    "carrier": "Global Health Insurance Partners",
    "totalClaims": 42,
    "totalClaimedAmount": 4500.75,
    "approvedClaims": 42,
    "rejectedClaims": 42,
    "approvalRate": 42,
    "highRiskClaims": 42,
    "generatedAt": "Standard string input"
  },
  "httpStatus": 42
}


