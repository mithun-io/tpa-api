
RulesController: localhost:8080/api/v1/rules

GET: /{id}
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "ruleKey": "string_value",
    "ruleValue": "string_value",
    "description": "string_value",
    "groovyScript": "string_value",
    "ruleType": "string_value",
    "priority": 100,
    "version": 100,
    "active": true,
    "simulationMode": true,
    "category": "string_value",
    "lastUpdatedBy": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z",
    "updatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


PUT: /{id}
parameters
id: 1
request
{
  "id": 100,
  "ruleKey": "string_value",
  "ruleValue": "string_value",
  "description": "string_value",
  "groovyScript": "string_value",
  "ruleType": "string_value",
  "priority": 100,
  "version": 100,
  "active": true,
  "simulationMode": true,
  "category": "string_value",
  "lastUpdatedBy": "string_value",
  "createdAt": "2026-05-26T00:00:00.000Z",
  "updatedAt": "2026-05-26T00:00:00.000Z"
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "ruleKey": "string_value",
    "ruleValue": "string_value",
    "description": "string_value",
    "groovyScript": "string_value",
    "ruleType": "string_value",
    "priority": 100,
    "version": 100,
    "active": true,
    "simulationMode": true,
    "category": "string_value",
    "lastUpdatedBy": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z",
    "updatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


DELETE: /{id}
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "ruleKey": "string_value",
      "ruleValue": "string_value",
      "description": "string_value",
      "groovyScript": "string_value",
      "ruleType": "string_value",
      "priority": 100,
      "version": 100,
      "active": true,
      "simulationMode": true,
      "category": "string_value",
      "lastUpdatedBy": "string_value",
      "createdAt": "2026-05-26T00:00:00.000Z",
      "updatedAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


POST: /
request
{
  "id": 100,
  "ruleKey": "string_value",
  "ruleValue": "string_value",
  "description": "string_value",
  "groovyScript": "string_value",
  "ruleType": "string_value",
  "priority": 100,
  "version": 100,
  "active": true,
  "simulationMode": true,
  "category": "string_value",
  "lastUpdatedBy": "string_value",
  "createdAt": "2026-05-26T00:00:00.000Z",
  "updatedAt": "2026-05-26T00:00:00.000Z"
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "ruleKey": "string_value",
    "ruleValue": "string_value",
    "description": "string_value",
    "groovyScript": "string_value",
    "ruleType": "string_value",
    "priority": 100,
    "version": 100,
    "active": true,
    "simulationMode": true,
    "category": "string_value",
    "lastUpdatedBy": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z",
    "updatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


POST: /simulate
request
{
  "policyId": "string_value",
  "policyName": "string_value",
  "policyNumber": "string_value",
  "policyStatus": "string_value",
  "claimFormPresent": true,
  "claimFormPatientName": "string_value",
  "claimFormHospitalName": "string_value",
  "claimFormAdmissionDate": "2026-05-26T00:00:00.000Z",
  "claimFormDischargeDate": "2026-05-26T00:00:00.000Z",
  "combinedDocumentPresent": true,
  "combinedDocPatientName": "string_value",
  "combinedDocHospitalName": "string_value",
  "combinedDocAdmissionDate": "2026-05-26T00:00:00.000Z",
  "combinedDocDischargeDate": "2026-05-26T00:00:00.000Z",
  "claimedAmount": 100,
  "totalBillAmount": 100,
  "carrierName": "string_value",
  "claimType": "string_value",
  "diagnosis": "string_value",
  "billNumber": "string_value",
  "billDate": "2026-05-26T00:00:00.000Z",
  "isDuplicate": true
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "claimStatus": "string_value",
    "reasons": [
      "string_value"
    ]
  },
  "httpStatus": 100
}


POST: /seed
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


POST: /evaluate
request
{
  "policyId": "string_value",
  "policyName": "string_value",
  "policyNumber": "string_value",
  "policyStatus": "string_value",
  "claimFormPresent": true,
  "claimFormPatientName": "string_value",
  "claimFormHospitalName": "string_value",
  "claimFormAdmissionDate": "2026-05-26T00:00:00.000Z",
  "claimFormDischargeDate": "2026-05-26T00:00:00.000Z",
  "combinedDocumentPresent": true,
  "combinedDocPatientName": "string_value",
  "combinedDocHospitalName": "string_value",
  "combinedDocAdmissionDate": "2026-05-26T00:00:00.000Z",
  "combinedDocDischargeDate": "2026-05-26T00:00:00.000Z",
  "claimedAmount": 100,
  "totalBillAmount": 100,
  "carrierName": "string_value",
  "claimType": "string_value",
  "diagnosis": "string_value",
  "billNumber": "string_value",
  "billDate": "2026-05-26T00:00:00.000Z",
  "isDuplicate": true
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "claimStatus": "string_value",
    "reasons": [
      "string_value"
    ]
  },
  "httpStatus": 100
}


PATCH: /{id}/deactivate
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "ruleKey": "string_value",
    "ruleValue": "string_value",
    "description": "string_value",
    "groovyScript": "string_value",
    "ruleType": "string_value",
    "priority": 100,
    "version": 100,
    "active": true,
    "simulationMode": true,
    "category": "string_value",
    "lastUpdatedBy": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z",
    "updatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


PATCH: /{id}/activate
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "ruleKey": "string_value",
    "ruleValue": "string_value",
    "description": "string_value",
    "groovyScript": "string_value",
    "ruleType": "string_value",
    "priority": 100,
    "version": 100,
    "active": true,
    "simulationMode": true,
    "category": "string_value",
    "lastUpdatedBy": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z",
    "updatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /audits/simulations
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "ruleKey": "string_value",
      "ruleType": "string_value",
      "ruleVersion": 100,
      "inputStatus": "string_value",
      "outputStatus": "string_value",
      "reasons": "string_value",
      "simulation": true,
      "fired": true,
      "executionTimeMs": 100,
      "executedBy": "string_value",
      "executedAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /audits/rules/{ruleKey}
parameters
ruleKey: string_value
pageable: value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "claimId": 100,
        "ruleKey": "string_value",
        "ruleType": "string_value",
        "ruleVersion": 100,
        "inputStatus": "string_value",
        "outputStatus": "string_value",
        "reasons": "string_value",
        "simulation": true,
        "fired": true,
        "executionTimeMs": 100,
        "executedBy": "string_value",
        "executedAt": "2026-05-26T00:00:00.000Z"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}


GET: /audits/claims/{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "ruleKey": "string_value",
      "ruleType": "string_value",
      "ruleVersion": 100,
      "inputStatus": "string_value",
      "outputStatus": "string_value",
      "reasons": "string_value",
      "simulation": true,
      "fired": true,
      "executionTimeMs": 100,
      "executedBy": "string_value",
      "executedAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /active
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "ruleKey": "string_value",
      "ruleValue": "string_value",
      "description": "string_value",
      "groovyScript": "string_value",
      "ruleType": "string_value",
      "priority": 100,
      "version": 100,
      "active": true,
      "simulationMode": true,
      "category": "string_value",
      "lastUpdatedBy": "string_value",
      "createdAt": "2026-05-26T00:00:00.000Z",
      "updatedAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}



ClaimsController: localhost:8080/api/v1/claims

PUT: /{claimId}/carrier-approve
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /
parameters
pageable: value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "userName": "string_value",
        "userEmail": "string_value",
        "patientName": "string_value",
        "hospitalName": "string_value",
        "admissionDate": "2026-05-26T00:00:00.000Z",
        "dischargeDate": "2026-05-26T00:00:00.000Z",
        "totalBillAmount": 100,
        "policyId": "string_value",
        "policyNumber": "string_value",
        "carrierName": "string_value",
        "claimStatus": "string_value",
        "claimType": "string_value",
        "diagnosis": "string_value",
        "amount": 100,
        "rejectionReason": "string_value",
        "reviewedBy": "string_value",
        "reviewedAt": "2026-05-26T00:00:00.000Z",
        "reviewNotes": "string_value",
        "createdDate": "2026-05-26T00:00:00.000Z",
        "processedDate": "2026-05-26T00:00:00.000Z",
        "riskScore": 100,
        "riskFlags": "string_value",
        "healthScore": 100,
        "riskLevel": "string_value",
        "aiSummary": "string_value"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}


POST: /
request
{
  "policyId": "string_value",
  "policyName": "string_value",
  "policyNumber": "string_value",
  "policyStatus": "string_value",
  "claimFormPresent": true,
  "claimFormPatientName": "string_value",
  "claimFormHospitalName": "string_value",
  "claimFormAdmissionDate": "2026-05-26T00:00:00.000Z",
  "claimFormDischargeDate": "2026-05-26T00:00:00.000Z",
  "combinedDocumentPresent": true,
  "combinedDocPatientName": "string_value",
  "combinedDocHospitalName": "string_value",
  "combinedDocAdmissionDate": "2026-05-26T00:00:00.000Z",
  "combinedDocDischargeDate": "2026-05-26T00:00:00.000Z",
  "claimedAmount": 100,
  "totalBillAmount": 100,
  "carrierName": "string_value",
  "claimType": "string_value",
  "diagnosis": "string_value",
  "billNumber": "string_value",
  "billDate": "2026-05-26T00:00:00.000Z",
  "isDuplicate": true
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "userName": "string_value",
    "userEmail": "string_value",
    "patientName": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "totalBillAmount": 100,
    "policyId": "string_value",
    "policyNumber": "string_value",
    "carrierName": "string_value",
    "claimStatus": "string_value",
    "claimType": "string_value",
    "diagnosis": "string_value",
    "amount": 100,
    "rejectionReason": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "reviewNotes": "string_value",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "riskScore": 100,
    "riskFlags": "string_value",
    "healthScore": 100,
    "riskLevel": "string_value",
    "aiSummary": "string_value"
  },
  "httpStatus": 100
}


GET: /{claimId}/queries
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "username": "string_value",
      "message": "string_value",
      "carrier": true,
      "timestamp": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


POST: /{claimId}/queries
parameters
claimId: 100
request
{
  "message": "string_value"
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "claimId": 100,
    "username": "string_value",
    "message": "string_value",
    "carrier": true,
    "timestamp": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


POST: /notify
parameters
userEmail: test@tpa.com
title: string_value
message: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


POST: /bulk-approve
request
[
  100
]

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalProcessed": 100,
    "success": 100,
    "failed": 100
  },
  "httpStatus": 100
}


POST: /broadcast/{claimId}
parameters
claimId: 100
status: ACTIVE
message: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "userName": "string_value",
    "userEmail": "string_value",
    "patientName": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "totalBillAmount": 100,
    "policyId": "string_value",
    "policyNumber": "string_value",
    "carrierName": "string_value",
    "claimStatus": "string_value",
    "claimType": "string_value",
    "diagnosis": "string_value",
    "amount": 100,
    "rejectionReason": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "reviewNotes": "string_value",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "riskScore": 100,
    "riskFlags": "string_value",
    "healthScore": 100,
    "riskLevel": "string_value",
    "aiSummary": "string_value"
  },
  "httpStatus": 100
}


DELETE: /{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /{claimId}/timeline
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "fromStatus": "string_value",
      "toStatus": "string_value",
      "notes": "string_value",
      "changedBy": "string_value",
      "occurredAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /{claimId}/export
parameters
claimId: 100
response
"string_value"


GET: /{claimId}/audits
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "previousStatus": "string_value",
      "newStatus": "string_value",
      "changedBy": "string_value",
      "notes": "string_value",
      "changedAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /search
parameters
claimStatus: ACTIVE
from: 2026-05-26T00:00:00.000Z
to: 2026-05-26T00:00:00.000Z
minAmount: 1500
maxAmount: 1500
pageable: value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "userName": "string_value",
        "userEmail": "string_value",
        "patientName": "string_value",
        "hospitalName": "string_value",
        "admissionDate": "2026-05-26T00:00:00.000Z",
        "dischargeDate": "2026-05-26T00:00:00.000Z",
        "totalBillAmount": 100,
        "policyId": "string_value",
        "policyNumber": "string_value",
        "carrierName": "string_value",
        "claimStatus": "string_value",
        "claimType": "string_value",
        "diagnosis": "string_value",
        "amount": 100,
        "rejectionReason": "string_value",
        "reviewedBy": "string_value",
        "reviewedAt": "2026-05-26T00:00:00.000Z",
        "reviewNotes": "string_value",
        "createdDate": "2026-05-26T00:00:00.000Z",
        "processedDate": "2026-05-26T00:00:00.000Z",
        "riskScore": 100,
        "riskFlags": "string_value",
        "healthScore": 100,
        "riskLevel": "string_value",
        "aiSummary": "string_value"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}



PaymentsController: localhost:8080/api/v1/payments

POST: /verify
request
{
  "razorpay_order_id": "string_value",
  "razorpay_payment_id": "string_value",
  "razorpay_signature": "string_value"
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "claimId": 100,
    "amount": 100,
    "currency": "string_value",
    "status": "string_value",
    "razorpayOrderId": "string_value",
    "razorpayPaymentId": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


POST: /create-order
request
{
  "claimId": 100,
  "amount": 100
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "orderId": "string_value",
    "amount": 100,
    "currency": "string_value",
    "key": "string_value",
    "claimId": 100
  },
  "httpStatus": 100
}


GET: /claim/{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "claimId": 100,
    "amount": 100,
    "currency": "string_value",
    "status": "string_value",
    "razorpayOrderId": "string_value",
    "razorpayPaymentId": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}



NotificationsController: localhost:8080/api/v1/notifications

POST: /mark-read
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


PATCH: /{id}/read
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "title": "string_value",
      "message": "string_value",
      "read": true,
      "createdAt": "2026-05-26T00:00:00.000Z",
      "targetUrl": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /unread-count
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}



MedicalController: localhost:8080/api/v1/medical

POST: /validate
request
{
  "icdCode": "string_value",
  "diagnosis": "string_value",
  "claimedAmount": 100
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "icdCode": "string_value",
    "diagnosis": "string_value",
    "validationIssues": [
      "string_value"
    ],
    "upcodingWarnings": [
      "string_value"
    ],
    "highRisk": true,
    "medicalRiskScore": 100,
    "overallStatus": "string_value",
    "validatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


POST: /validate/batch
request
[
  {
    "icdCode": "string_value",
    "diagnosis": "string_value",
    "claimedAmount": 100
  }
]

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalValidated": 100,
    "flaggedCount": 100,
    "cleanCount": 100,
    "results": [
      {
        "icdCode": "string_value",
        "diagnosis": "string_value",
        "validationIssues": [
          "string_value"
        ],
        "upcodingWarnings": [
          "string_value"
        ],
        "highRisk": true,
        "medicalRiskScore": 100,
        "overallStatus": "string_value",
        "validatedAt": "2026-05-26T00:00:00.000Z"
      }
    ],
    "validatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /upcoding/risk
parameters
icdCode: string_value
claimedAmount: 1500
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "icdCode": "string_value",
    "icdDescription": "string_value",
    "claimedAmount": 100,
    "upcodingRisk": true,
    "warnings": [
      "string_value"
    ],
    "riskLevel": "string_value",
    "analyzedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /high-risk/codes
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalCodes": 100,
    "codes": [
      {
        "code": "string_value",
        "description": "string_value"
      }
    ],
    "retrievedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /codes/lookup
parameters
code: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "code": "string_value",
    "found": true,
    "description": "string_value",
    "highRisk": true,
    "medicalRiskScore": 100,
    "retrievedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}



FilesController: localhost:8080/api/v1/files

POST: /upload
parameters
claimId: 100
documentType: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "fileName": "string_value",
    "fileType": "string_value",
    "documentType": "string_value",
    "validationStatus": "string_value",
    "validationIssues": "string_value",
    "confidenceScore": 100,
    "fileUrl": "string_value"
  },
  "httpStatus": 100
}


POST: /upload/multiple
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "fileName": "string_value",
      "fileType": "string_value",
      "documentType": "string_value",
      "validationStatus": "string_value",
      "validationIssues": "string_value",
      "confidenceScore": 100,
      "fileUrl": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /{documentId}
parameters
documentId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "fileName": "string_value",
    "fileType": "string_value",
    "documentType": "string_value",
    "validationStatus": "string_value",
    "validationIssues": "string_value",
    "confidenceScore": 100,
    "fileUrl": "string_value"
  },
  "httpStatus": 100
}


GET: /download/{documentId}
parameters
documentId: 100
response
"string_value"


GET: /claim/{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "fileName": "string_value",
      "fileType": "string_value",
      "documentType": "string_value",
      "validationStatus": "string_value",
      "validationIssues": "string_value",
      "confidenceScore": 100,
      "fileUrl": "string_value"
    }
  ],
  "httpStatus": 100
}



CarrierController: localhost:8080/api/v1/carrier

POST: /claims/{id}/ai-analyze
parameters
id: 1
request
null

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "verdict": "string_value",
    "confidence": 100,
    "riskScore": 100,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 100,
      "eligibleAmount": 100
    },
    "flags": [
      "string_value"
    ],
    "recommendation": "string_value",
    "generatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


PATCH: /claims/{id}/validate
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


PATCH: /claims/{id}/remark
parameters
id: 1
request
null

response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


PATCH: /claims/{id}/reject
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


PATCH: /claims/{id}/flag
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


PATCH: /claims/{id}/approve
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /claims
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "policyNumber": "string_value",
      "claimId": 100,
      "claimType": "string_value",
      "claimStatus": "string_value",
      "amount": 100,
      "totalBillAmount": 100,
      "diagnosis": "string_value",
      "hospitalName": "string_value",
      "admissionDate": "2026-05-26T00:00:00.000Z",
      "dischargeDate": "2026-05-26T00:00:00.000Z",
      "createdDate": "2026-05-26T00:00:00.000Z",
      "processedDate": "2026-05-26T00:00:00.000Z",
      "rejectionReason": "string_value",
      "reviewNotes": "string_value",
      "reviewedBy": "string_value",
      "reviewedAt": "2026-05-26T00:00:00.000Z",
      "patientInfo": {
        "name": "string_value",
        "email": "string_value",
        "mobile": "string_value",
        "dateOfBirth": "2026-05-26T00:00:00.000Z",
        "gender": "string_value",
        "address": "string_value"
      },
      "fraudInfo": {
        "riskScore": 100,
        "riskLevel": "string_value",
        "healthScore": 100,
        "riskFlags": "string_value",
        "aiSummary": "string_value"
      },
      "policyInfo": {
        "policyNumber": "string_value",
        "policyStatus": "string_value",
        "reason": "string_value"
      }
    }
  ],
  "httpStatus": 100
}


GET: /claims/{id}
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "policyNumber": "string_value",
    "claimId": 100,
    "claimType": "string_value",
    "claimStatus": "string_value",
    "amount": 100,
    "totalBillAmount": 100,
    "diagnosis": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "rejectionReason": "string_value",
    "reviewNotes": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "patientInfo": {
      "name": "string_value",
      "email": "string_value",
      "mobile": "string_value",
      "dateOfBirth": "2026-05-26T00:00:00.000Z",
      "gender": "string_value",
      "address": "string_value"
    },
    "fraudInfo": {
      "riskScore": 100,
      "riskLevel": "string_value",
      "healthScore": 100,
      "riskFlags": "string_value",
      "aiSummary": "string_value"
    },
    "policyInfo": {
      "policyNumber": "string_value",
      "policyStatus": "string_value",
      "reason": "string_value"
    }
  },
  "httpStatus": 100
}


GET: /claims/{id}/policy-status
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "claimId": 100,
    "policyNumber": "string_value",
    "policyStatus": "string_value",
    "reason": "string_value"
  },
  "httpStatus": 100
}



AiController: localhost:8080/api/v1/ai

POST: /validate-document
parameters
documentType: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "status": "string_value",
    "issues": [
      "string_value"
    ],
    "confidenceScore": 100,
    "icdCode": "string_value"
  },
  "httpStatus": 100
}


POST: /validate-claim
request
{
  "patientName": "string_value",
  "hospitalName": "string_value",
  "policyNumber": "string_value",
  "amount": 100,
  "diagnosis": "string_value",
  "admissionDate": "string_value",
  "dischargeDate": "string_value"
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "verdict": "string_value",
    "confidence": 100,
    "riskScore": 100,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 100,
      "eligibleAmount": 100
    },
    "flags": [
      "string_value"
    ],
    "recommendation": "string_value",
    "generatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


POST: /claims/{id}/generate-summary
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


POST: /analyze/{claimId}
parameters
claimId: 100
request
null

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "verdict": "string_value",
    "confidence": 100,
    "riskScore": 100,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 100,
      "eligibleAmount": 100
    },
    "flags": [
      "string_value"
    ],
    "recommendation": "string_value",
    "generatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}



AdminController: localhost:8080/api/v1/admin

POST: /kafka/dlq/{eventId}/retry
parameters
eventId: ID-12345
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "success": true,
    "eventId": "string_value",
    "message": "string_value",
    "retriedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


POST: /claims/{id}/ai-chat
parameters
id: 1
request
null

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "verdict": "string_value",
    "confidence": 100,
    "riskScore": 100,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 100,
      "eligibleAmount": 100
    },
    "flags": [
      "string_value"
    ],
    "recommendation": "string_value",
    "generatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


PATCH: /users/{id}/unblock
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "name": "string_value",
    "email": "string_value",
    "phoneNumber": "string_value",
    "dateOfBirth": "2026-05-26T00:00:00.000Z",
    "address": "string_value",
    "gender": "string_value",
    "userRole": "string_value",
    "userStatus": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


PATCH: /users/{id}/block
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "name": "string_value",
    "email": "string_value",
    "phoneNumber": "string_value",
    "dateOfBirth": "2026-05-26T00:00:00.000Z",
    "address": "string_value",
    "gender": "string_value",
    "userRole": "string_value",
    "userStatus": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


PATCH: /claims/{id}/reject
parameters
id: 1
reason: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "userName": "string_value",
    "userEmail": "string_value",
    "patientName": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "totalBillAmount": 100,
    "policyId": "string_value",
    "policyNumber": "string_value",
    "carrierName": "string_value",
    "claimStatus": "string_value",
    "claimType": "string_value",
    "diagnosis": "string_value",
    "amount": 100,
    "rejectionReason": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "reviewNotes": "string_value",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "riskScore": 100,
    "riskFlags": "string_value",
    "healthScore": 100,
    "riskLevel": "string_value",
    "aiSummary": "string_value"
  },
  "httpStatus": 100
}


PATCH: /claims/{id}/assign-carrier
parameters
id: 1
request
null

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "userName": "string_value",
    "userEmail": "string_value",
    "patientName": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "totalBillAmount": 100,
    "policyId": "string_value",
    "policyNumber": "string_value",
    "carrierName": "string_value",
    "claimStatus": "string_value",
    "claimType": "string_value",
    "diagnosis": "string_value",
    "amount": 100,
    "rejectionReason": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "reviewNotes": "string_value",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "riskScore": 100,
    "riskFlags": "string_value",
    "healthScore": 100,
    "riskLevel": "string_value",
    "aiSummary": "string_value"
  },
  "httpStatus": 100
}


PATCH: /claims/{id}/approve
parameters
id: 1
reason: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "userName": "string_value",
    "userEmail": "string_value",
    "patientName": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "totalBillAmount": 100,
    "policyId": "string_value",
    "policyNumber": "string_value",
    "carrierName": "string_value",
    "claimStatus": "string_value",
    "claimType": "string_value",
    "diagnosis": "string_value",
    "amount": 100,
    "rejectionReason": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "reviewNotes": "string_value",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "riskScore": 100,
    "riskFlags": "string_value",
    "healthScore": 100,
    "riskLevel": "string_value",
    "aiSummary": "string_value"
  },
  "httpStatus": 100
}


PATCH: /claims/review
request
{
  "claimId": 100,
  "claimStatus": "string_value",
  "reviewNotes": "string_value"
}

response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "userName": "string_value",
    "userEmail": "string_value",
    "patientName": "string_value",
    "hospitalName": "string_value",
    "admissionDate": "2026-05-26T00:00:00.000Z",
    "dischargeDate": "2026-05-26T00:00:00.000Z",
    "totalBillAmount": 100,
    "policyId": "string_value",
    "policyNumber": "string_value",
    "carrierName": "string_value",
    "claimStatus": "string_value",
    "claimType": "string_value",
    "diagnosis": "string_value",
    "amount": 100,
    "rejectionReason": "string_value",
    "reviewedBy": "string_value",
    "reviewedAt": "2026-05-26T00:00:00.000Z",
    "reviewNotes": "string_value",
    "createdDate": "2026-05-26T00:00:00.000Z",
    "processedDate": "2026-05-26T00:00:00.000Z",
    "riskScore": 100,
    "riskFlags": "string_value",
    "healthScore": 100,
    "riskLevel": "string_value",
    "aiSummary": "string_value"
  },
  "httpStatus": 100
}


PATCH: /carriers/{id}/reject
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "companyName": "string_value",
    "email": "string_value",
    "phoneNumber": "string_value",
    "companyType": "string_value",
    "licenseNumber": "string_value",
    "registrationNumber": "string_value",
    "taxId": "string_value",
    "contactPersonName": "string_value",
    "contactPersonPhone": "string_value",
    "website": "string_value",
    "userStatus": "string_value"
  },
  "httpStatus": 100
}


PATCH: /carriers/{id}/approve
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "companyName": "string_value",
    "email": "string_value",
    "phoneNumber": "string_value",
    "companyType": "string_value",
    "licenseNumber": "string_value",
    "registrationNumber": "string_value",
    "taxId": "string_value",
    "contactPersonName": "string_value",
    "contactPersonPhone": "string_value",
    "website": "string_value",
    "userStatus": "string_value"
  },
  "httpStatus": 100
}


GET: /users
parameters
search: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "name": "string_value",
        "email": "string_value",
        "phoneNumber": "string_value",
        "dateOfBirth": "2026-05-26T00:00:00.000Z",
        "address": "string_value",
        "gender": "string_value",
        "userRole": "string_value",
        "userStatus": "string_value",
        "createdAt": "2026-05-26T00:00:00.000Z"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}


GET: /patients
parameters
username: John Doe
email: test@tpa.com
userStatus: ACTIVE
sortBy: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "name": "string_value",
        "email": "string_value",
        "phoneNumber": "string_value",
        "dateOfBirth": "2026-05-26T00:00:00.000Z",
        "address": "string_value",
        "gender": "string_value",
        "userRole": "string_value",
        "userStatus": "string_value",
        "createdAt": "2026-05-26T00:00:00.000Z"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}


GET: /monitoring
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "kafka": null,
    "failedClaims": [
      {
        "id": 100,
        "userName": "string_value",
        "userEmail": "string_value",
        "patientName": "string_value",
        "hospitalName": "string_value",
        "admissionDate": "2026-05-26T00:00:00.000Z",
        "dischargeDate": "2026-05-26T00:00:00.000Z",
        "totalBillAmount": 100,
        "policyId": "string_value",
        "policyNumber": "string_value",
        "carrierName": "string_value",
        "claimStatus": "string_value",
        "claimType": "string_value",
        "diagnosis": "string_value",
        "amount": 100,
        "rejectionReason": "string_value",
        "reviewedBy": "string_value",
        "reviewedAt": "2026-05-26T00:00:00.000Z",
        "reviewNotes": "string_value",
        "createdDate": "2026-05-26T00:00:00.000Z",
        "processedDate": "2026-05-26T00:00:00.000Z",
        "riskScore": 100,
        "riskFlags": "string_value",
        "healthScore": 100,
        "riskLevel": "string_value",
        "aiSummary": "string_value"
      }
    ],
    "errorLogs": [
      null
    ]
  },
  "httpStatus": 100
}


GET: /kafka/topics
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "topics": [
      {
        "name": "string_value",
        "partitions": 100,
        "dlq": true
      }
    ],
    "totalTopics": 100,
    "dlqTopics": 100,
    "retrievedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /kafka/pending
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "pendingEvents": [
      {
        "id": 100,
        "eventId": "string_value",
        "claimId": 100,
        "stage": "string_value",
        "claimStatus": "string_value",
        "message": "string_value",
        "metadata": "string_value",
        "topic": "string_value",
        "receivedAt": "2026-05-26T00:00:00.000Z",
        "processedAt": "2026-05-26T00:00:00.000Z",
        "processed": true,
        "retryCount": 100,
        "errorDetails": "string_value"
      }
    ],
    "count": 100,
    "retrievedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /kafka/health
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalEventsProcessed": 100,
    "pendingEvents": 100,
    "totalEvents": 100,
    "successRate": 100,
    "dlqMessageCount": 100,
    "kafkaStatus": "string_value",
    "stageBreakdown": {
      "claimUploaded": 100,
      "ocrCompleted": 100,
      "aiAnalysisDone": 100,
      "ruleEvaluated": 100,
      "adminApproved": 100,
      "carrierApproved": 100,
      "paymentInitiated": 100,
      "paymentCompleted": 100,
      "rejected": 100
    },
    "retrievedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /kafka/dlq
parameters
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "dlqMessages": [
      {
        "id": 100,
        "eventId": "string_value",
        "claimId": 100,
        "stage": "string_value",
        "claimStatus": "string_value",
        "message": "string_value",
        "metadata": "string_value",
        "topic": "string_value",
        "receivedAt": "2026-05-26T00:00:00.000Z",
        "processedAt": "2026-05-26T00:00:00.000Z",
        "processed": true,
        "retryCount": 100,
        "errorDetails": "string_value"
      }
    ],
    "totalDlqMessages": 100,
    "page": 100,
    "size": 100,
    "retrievedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /claims
parameters
claimStatus: ACTIVE
createdAt: 2026-05-26T00:00:00.000Z
sortBy: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "userName": "string_value",
        "userEmail": "string_value",
        "patientName": "string_value",
        "hospitalName": "string_value",
        "admissionDate": "2026-05-26T00:00:00.000Z",
        "dischargeDate": "2026-05-26T00:00:00.000Z",
        "totalBillAmount": 100,
        "policyId": "string_value",
        "policyNumber": "string_value",
        "carrierName": "string_value",
        "claimStatus": "string_value",
        "claimType": "string_value",
        "diagnosis": "string_value",
        "amount": 100,
        "rejectionReason": "string_value",
        "reviewedBy": "string_value",
        "reviewedAt": "2026-05-26T00:00:00.000Z",
        "reviewNotes": "string_value",
        "createdDate": "2026-05-26T00:00:00.000Z",
        "processedDate": "2026-05-26T00:00:00.000Z",
        "riskScore": 100,
        "riskFlags": "string_value",
        "healthScore": 100,
        "riskLevel": "string_value",
        "aiSummary": "string_value"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}


GET: /claims/{id}/ai-summary
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "verdict": "string_value",
    "confidence": 100,
    "riskScore": 100,
    "validationChecks": {
      "policyActive": true,
      "documentsComplete": true,
      "withinLimit": true
    },
    "financialSummary": {
      "claimedAmount": 100,
      "eligibleAmount": 100
    },
    "flags": [
      "string_value"
    ],
    "recommendation": "string_value",
    "generatedAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}


GET: /carriers
parameters
companyName: John Doe
userStatus: ACTIVE
sortBy: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalElements": 100,
    "totalPages": 100,
    "first": true,
    "last": true,
    "size": 100,
    "content": [
      {
        "id": 100,
        "companyName": "string_value",
        "email": "string_value",
        "phoneNumber": "string_value",
        "companyType": "string_value",
        "licenseNumber": "string_value",
        "registrationNumber": "string_value",
        "taxId": "string_value",
        "contactPersonName": "string_value",
        "contactPersonPhone": "string_value",
        "website": "string_value",
        "userStatus": "string_value"
      }
    ],
    "number": 100,
    "sort": {
      "empty": true,
      "sorted": true,
      "unsorted": true
    },
    "numberOfElements": 100,
    "pageable": {
      "offset": 100,
      "sort": {
        "empty": true,
        "sorted": true,
        "unsorted": true
      },
      "paged": true,
      "pageNumber": 100,
      "pageSize": 100,
      "unpaged": true
    },
    "empty": true
  },
  "httpStatus": 100
}



FraudController: localhost:8080/api/v1/fraud

PATCH: /admin/claims/{id}/safe
parameters
id: 1
response
{
  "success": true,
  "message": "string_value",
  "data": null,
  "httpStatus": 100
}


GET: /carrier/dashboard
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "dashboardStats": {
      "totalClaims": 100,
      "flagged": 100,
      "highRisk": 100,
      "mediumRisk": 100,
      "lowRisk": 100
    },
    "claims": [
      {
        "claimId": 100,
        "policyNumber": "string_value",
        "amount": 100,
        "riskScore": 100,
        "riskLevel": "string_value",
        "reasons": [
          "string_value"
        ]
      }
    ]
  },
  "httpStatus": 100
}


GET: /admin/dashboard
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "dashboardStats": {
      "totalClaims": 100,
      "flagged": 100,
      "highRisk": 100,
      "mediumRisk": 100,
      "lowRisk": 100
    },
    "claims": [
      {
        "claimId": 100,
        "policyNumber": "string_value",
        "amount": 100,
        "riskScore": 100,
        "riskLevel": "string_value",
        "reasons": [
          "string_value"
        ]
      }
    ]
  },
  "httpStatus": 100
}



UsersController: localhost:8080/api/v1/users

GET: /profile
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "id": 100,
    "name": "string_value",
    "email": "string_value",
    "phoneNumber": "string_value",
    "dateOfBirth": "2026-05-26T00:00:00.000Z",
    "address": "string_value",
    "gender": "string_value",
    "userRole": "string_value",
    "userStatus": "string_value",
    "createdAt": "2026-05-26T00:00:00.000Z"
  },
  "httpStatus": 100
}



AuditController: localhost:8080/api/v1/audit

GET: /range
parameters
from: 2026-05-26T00:00:00.000Z
to: 2026-05-26T00:00:00.000Z
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "action": "string_value",
      "previousStatus": "string_value",
      "newStatus": "string_value",
      "timestamp": "2026-05-26T00:00:00.000Z",
      "performedBy": "string_value",
      "details": "string_value",
      "integrityHash": "string_value",
      "previousHash": "string_value",
      "blockchainHash": "string_value",
      "ipAddress": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /payments/reconcile
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalVerifiedPayments": 100,
    "totalAmountSettled": 100,
    "currency": "string_value",
    "reconciledAt": "string_value"
  },
  "httpStatus": 100
}


GET: /payments/payment/{paymentId}
parameters
paymentId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "paymentId": 100,
      "amount": 100,
      "currency": "string_value",
      "paymentEventType": "string_value",
      "paymentStatus": "string_value",
      "razorpayOrderId": "string_value",
      "razorpayPaymentId": "string_value",
      "notes": "string_value",
      "initiatedBy": "string_value",
      "integrityHash": "string_value",
      "createdAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /payments/event/{eventType}
parameters
eventType: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "paymentId": 100,
      "amount": 100,
      "currency": "string_value",
      "paymentEventType": "string_value",
      "paymentStatus": "string_value",
      "razorpayOrderId": "string_value",
      "razorpayPaymentId": "string_value",
      "notes": "string_value",
      "initiatedBy": "string_value",
      "integrityHash": "string_value",
      "createdAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /payments/claim/{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "paymentId": 100,
      "amount": 100,
      "currency": "string_value",
      "paymentEventType": "string_value",
      "paymentStatus": "string_value",
      "razorpayOrderId": "string_value",
      "razorpayPaymentId": "string_value",
      "notes": "string_value",
      "initiatedBy": "string_value",
      "integrityHash": "string_value",
      "createdAt": "2026-05-26T00:00:00.000Z"
    }
  ],
  "httpStatus": 100
}


GET: /events/unprocessed
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "eventId": "string_value",
      "claimId": 100,
      "stage": "string_value",
      "claimStatus": "string_value",
      "message": "string_value",
      "metadata": "string_value",
      "topic": "string_value",
      "receivedAt": "2026-05-26T00:00:00.000Z",
      "processedAt": "2026-05-26T00:00:00.000Z",
      "processed": true,
      "retryCount": 100,
      "errorDetails": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /events/stage/{stage}
parameters
stage: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "eventId": "string_value",
      "claimId": 100,
      "stage": "string_value",
      "claimStatus": "string_value",
      "message": "string_value",
      "metadata": "string_value",
      "topic": "string_value",
      "receivedAt": "2026-05-26T00:00:00.000Z",
      "processedAt": "2026-05-26T00:00:00.000Z",
      "processed": true,
      "retryCount": 100,
      "errorDetails": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /events/claim/{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "eventId": "string_value",
      "claimId": 100,
      "stage": "string_value",
      "claimStatus": "string_value",
      "message": "string_value",
      "metadata": "string_value",
      "topic": "string_value",
      "receivedAt": "2026-05-26T00:00:00.000Z",
      "processedAt": "2026-05-26T00:00:00.000Z",
      "processed": true,
      "retryCount": 100,
      "errorDetails": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /claims/{claimId}
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "action": "string_value",
      "previousStatus": "string_value",
      "newStatus": "string_value",
      "timestamp": "2026-05-26T00:00:00.000Z",
      "performedBy": "string_value",
      "details": "string_value",
      "integrityHash": "string_value",
      "previousHash": "string_value",
      "blockchainHash": "string_value",
      "ipAddress": "string_value"
    }
  ],
  "httpStatus": 100
}


GET: /claims/{claimId}/verify
parameters
claimId: 100
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "claimId": 100,
    "chainIntact": true,
    "totalRecords": 100,
    "verifiedAt": "string_value",
    "message": "string_value"
  },
  "httpStatus": 100
}


GET: /claims/{claimId}/action/{action}
parameters
claimId: 100
action: string_value
response
{
  "success": true,
  "message": "string_value",
  "data": [
    {
      "id": 100,
      "claimId": 100,
      "action": "string_value",
      "previousStatus": "string_value",
      "newStatus": "string_value",
      "timestamp": "2026-05-26T00:00:00.000Z",
      "performedBy": "string_value",
      "details": "string_value",
      "integrityHash": "string_value",
      "previousHash": "string_value",
      "blockchainHash": "string_value",
      "ipAddress": "string_value"
    }
  ],
  "httpStatus": 100
}



AnalyticsController: localhost:8080/api/v1/analytics

GET: /sla/performance
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalClaims": 100,
    "withinSla": 100,
    "slaBreached": 100,
    "escalated": 100,
    "slaComplianceRate": 100,
    "avgProcessingHours": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /payments/summary
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalSettledAmount": 100,
    "totalSuccessfulSettlements": 100,
    "totalFailedPayments": 100,
    "successRate": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /loss-ratio
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalClaimsPaid": 100,
    "estimatedPremiumPool": 100,
    "lossRatioPercent": 100,
    "lossRatioStatus": "string_value",
    "settledClaims": 100,
    "rejectedClaims": 100,
    "totalClaims": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /leakage
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalClaimedAmount": 100,
    "totalApprovedPayout": 100,
    "leakageAmount": 100,
    "leakageRate": 100,
    "amountMismatchCount": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /hospitals
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "topHospitalsByVolume": null,
    "topHospitalsByAmount": null,
    "totalUniqueHospitals": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /fraud/trends
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "riskDistribution": null,
    "fraudRate": 100,
    "topRiskHospitals": null,
    "averageFraudScore": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /forecast
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "dailyAverageLast30Days": 100,
    "forecastNext7Days": 100,
    "forecastNext30Days": 100,
    "historicalData": [
      null
    ],
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


GET: /dashboard
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "totalClaims": 100,
    "totalApprovedPayout": 100,
    "totalClaimAmount": 100,
    "statusDistribution": null,
    "claimsPerDay": [
      {
        "date": "string_value",
        "count": 100
      }
    ]
  },
  "httpStatus": 100
}


GET: /carrier/{carrierName}/summary
parameters
carrierName: John Doe
response
{
  "success": true,
  "message": "string_value",
  "data": {
    "carrier": "string_value",
    "totalClaims": 100,
    "totalClaimedAmount": 100,
    "approvedClaims": 100,
    "rejectedClaims": 100,
    "approvalRate": 100,
    "highRiskClaims": 100,
    "generatedAt": "string_value"
  },
  "httpStatus": 100
}


