package com.tpa.helper;

import com.tpa.entity.*;
import com.tpa.enums.*;
import com.tpa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@Order(2) // Runs after AdminInitializer (Order 1)
@RequiredArgsConstructor
public class EnterpriseDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CarrierRepository carrierRepository;
    private final PatientRepository patientRepository;
    private final ClaimRepository claimRepository;
    private final NotificationRepository notificationRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    private final InsuranceProductRepository insuranceProductRepository;

    private final PasswordEncoder passwordEncoder;

    private static final String DEMO_PASSWORD = "UR:39&$sq1sL";
    private static final int SEED_THRESHOLD = 50; // Only seed if fewer claims than this

    private static final String[] PATIENT_NAMES = {
            "James Anderson", "Emma Wilson", "Michael Johnson", "Olivia Brown", "William Taylor",
            "Sophia Martinez", "Daniel Thomas", "Isabella Moore", "Matthew Jackson", "Mia White",
            "Christopher Harris", "Charlotte Martin", "Andrew Thompson", "Amelia Garcia", "Joshua Clark",
            "Evelyn Rodriguez", "David Lewis", "Harper Lee", "Joseph Walker", "Abigail Hall",
            "Benjamin Allen", "Emily Young", "Samuel King", "Ella Wright", "Alexander Scott",
            "Scarlett Green", "Ryan Adams", "Lily Baker", "Nathan Nelson", "Grace Carter"
    };

    private static final String[] HOSPITALS = {
            "Mayo Clinic",
            "Cleveland Clinic",
            "Massachusetts General Hospital",
            "Johns Hopkins Hospital",
            "NewYork-Presbyterian Hospital",

            "UCLA Medical Center",
            "Cedars-Sinai Medical Center",
            "Mount Sinai Hospital",
            "Houston Methodist Hospital",
            "Stanford Health Care",

            "Charité – Universitätsmedizin Berlin",
            "University Hospital Zurich",
            "Karolinska University Hospital",
            "St Thomas' Hospital",
            "Royal London Hospital",

            "Pitié-Salpêtrière Hospital",
            "Hospital Clínic de Barcelona",
            "Vienna General Hospital",
            "Rigshospitalet",
            "University Hospital Heidelberg",

            "Moscow City Clinical Hospital",
            "Botkin Hospital Moscow",
            "European Medical Center Moscow",
            "Burdenko Clinical Hospital",
            "Central Clinical Hospital Moscow",
    };

    private static final String[][] DIAGNOSES = {
            {"Acute Myocardial Infarction", "I21.9", "Cardiac"},
            {"Type 2 Diabetes Mellitus", "E11.9", "Endocrine"},
            {"Community Acquired Pneumonia", "J18.9", "Respiratory"},
            {"Appendicitis", "K37", "Surgical"},
            {"Fracture - Femur", "S72.309A", "Orthopedic"},
            {"Dengue Fever", "A90", "Infectious"},
            {"Acute Cerebrovascular Accident", "I63.9", "Neurological"},
            {"Chronic Kidney Disease Stage 3", "N18.3", "Renal"},
            {"Cataract", "H26.9", "Ophthalmology"},
            {"Normal Vaginal Delivery", "Z37.0", "Maternity"},
            {"Coronary Artery Bypass Graft", "Z95.1", "Cardiac Surgery"},
            {"Cholecystitis - Laparoscopic", "K81.0", "Surgical"},
            {"Intervertebral Disc Disease", "M51.16", "Spine"},
            {"Acute Kidney Injury", "N17.9", "Renal"},
            {"Hypertensive Crisis", "I10", "Cardiac"},
            {"Liver Cirrhosis", "K74.60", "Gastro"},
            {"Road Traffic Accident - Polytrauma", "T07", "Emergency"},
            {"Malignant Neoplasm - Breast", "C50.919", "Oncology"},
            {"Sepsis", "A41.9", "Critical Care"},
            {"Hip Replacement", "Z96.641", "Orthopedic"},
    };

    private static final String[] POLICY_NUMBERS = {
            "UHG-HLT-2026-104582", "AET-CRIT-2026-208741",
            "CIG-MED-2026-315904", "BCBS-HOSP-2026-427615",
            "ALL-GLOB-2026-538290", "AXA-CARE-2026-641873",
            "BUPA-PLUS-2026-752194", "ZUR-HEALTH-2026-863520",
            "SBER-MED-2026-974281", "MANU-LIFE-2026-185763",
    };

    private static final String[] POLICY_NAMES = {
            "Family Floater", "Critical Illness", "Hospital Accident Protection",
            "AD&D Insurance", "Recuperative Care", "Life Insurance",
            "Maternity Cover", "Senior Citizen Plan", "Corporate Employee Health",
            "Cancer Care Plan", "OPD & Wellness Plan", "Cardiac Care Plan"
    };

    private static final String[] REJECTION_REASONS = {
            "Pre-existing condition not covered under waiting period",
            "Procedure excluded under policy terms",
            "Duplicate claim - already settled",
            "Hospital not in Preferred Provider Network",
            "Claim submitted after policy lapse",
            "Insufficient supporting documentation",
            "Fraud indicator detected by AI engine",
            "Claim amount exceeds policy coverage limit"
    };

    private static final String[] AI_SUMMARIES = {
            "AI analysis indicates low fraud risk (score: 0.12). Medical necessity validated against ICD-10 benchmarks. Recommended for approval.",
            "Moderate risk detected (score: 0.47). Billing inconsistency identified in procedure codes. Manual review recommended.",
            "High-risk claim flagged (score: 0.82). Provider flagged for inflated billing. Escalated to fraud investigation team.",
            "AI assessment: Clean claim (score: 0.08). Hospital accreditation verified. All documents authentic.",
            "Risk score 0.61: Duplicate billing pattern detected. Similar claim submitted within 30 days. Hold pending investigation.",
            "Low risk (score: 0.19). Emergency admission validated. Treatment aligns with diagnosis ICD-10 coding.",
            "AI flagged potential upcoding (score: 0.55). Procedure charges 42% above regional benchmark. Expert review required.",
            "Clean claim (score: 0.05). Pre-authorization verified. All documents match. Fast-track settlement recommended."
    };

    private static final String[] ASSIGNED_REVIEWERS = {
            "michael.turner@tpa.com", "sarah.mitchell@tpa.com",
            "david.anderson@tpa.com", "emily.carter@tpa.com",
            "oliver.bennett@tpa.com", "lucas.martin@tpa.com",
    };

    private final Random random = new Random(42);

    private void ensureDemoAccountsExist() {
        createDemoUserIfAbsent("emma.wilson@outlook.com", "Emma Wilson", "+1 917-555-4821", DEMO_PASSWORD, UserRole.PATIENT, Gender.FEMALE, "2458 Maplewood Drive, Austin, Texas 78701, USA");
        createDemoUserIfAbsent("michael.turner@healthshield.com", "Michael Turner", "+1 646-555-9134", DEMO_PASSWORD, UserRole.CARRIER, Gender.MALE, "890 Park Avenue, New York, NY 10021, USA");

        // Ensure the carrier entity exists for the carrier demo user
        userRepository.findByEmail("michael.turner@healthshield.com").ifPresent(carrierUser -> {
            if (!carrierRepository.existsByUser(carrierUser)) {
                Carrier carrier = Carrier.builder()
                        .user(carrierUser)
                        .companyName("HealthShield Insurance Group")
                        .companyType("Health Insurance")
                        .registrationNumber("IRDAI-HIG-2024-458921")
                        .licenseNumber("LIC-HS-784512-2024")
                        .taxId("27AABCH4589K1Z2")
                        .website("https://www.healthshieldinsurance.com")
                        .build();
                carrierRepository.save(carrier);
                log.info("Demo carrier entity created for michael.turner@healthshield.com");
            }
        });
        
        userRepository.findByEmail("emma.wilson@outlook.com").ifPresent(patientUser -> {
            if (!patientRepository.existsByUser(patientUser)) {
                Patient patient = Patient.builder().user(patientUser).build();
                patientRepository.save(patient);
                log.info("Demo patient entity created for emma.wilson@outlook.com");
            }
        });
    }

    private void createDemoUserIfAbsent(String email, String username, String phoneNumber, String password, UserRole role, Gender gender, String address) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email)
                    .username(username)
                    .phoneNumber(phoneNumber)
                    .password(passwordEncoder.encode(password))
                    .userRole(role)
                    .userStatus(UserStatus.ACTIVE)
                    .gender(gender)
                    .address(address)
                    .dateOfBirth(LocalDate.of(1990, 6, 15))
                    .createdAt(LocalDateTime.now().minusMonths(14))
                    .build();
            userRepository.save(user);
            log.info("Demo account created: {} ({})", email, role);
        } else {
            log.info("Demo account already exists: {}", email);
        }
    }

    private List<User> seedPatientUsers() {
        List<User> seeded = new ArrayList<>();
        String[] firstNames = {
                "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda", "William", "Elizabeth",
                "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen",
                "Christopher", "Nancy", "Daniel", "Lisa", "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra"
        };

        String[] lastNames = {
                "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
                "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"
        };

        String[] addresses = {
                "New York", "Los Angeles", "Chicago", "Houston", "Phoenix",
                "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"
        };

        for (int i = 0; i < 50; i++) {
            String fn = firstNames[i % firstNames.length];
            String ln = lastNames[i % lastNames.length];
            String email = fn.toLowerCase() + "." + ln.toLowerCase() + i + "@demo.tpa";
            if (userRepository.existsByEmail(email)) continue;

            User u = User.builder()
                    .username(fn + " " + ln)
                    .email(email)
                    .phoneNumber(String.format("+1-%03d-%03d-%04d", 200 + (i % 700), 100 + (i % 900), 1000 + i))
                    .password(passwordEncoder.encode(DEMO_PASSWORD))
                    .userRole(UserRole.PATIENT)
                    .userStatus(UserStatus.ACTIVE)
                    .gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                    .address(addresses[i % addresses.length])
                    .dateOfBirth(LocalDate.of(1975 + (i % 30), (i % 12) + 1, (i % 28) + 1))
                    .createdAt(LocalDateTime.now().minusMonths(12 - (i % 12)))
                    .build();
            User savedUser = userRepository.save(u);
            seeded.add(savedUser);
            
            if (!patientRepository.existsByUser(savedUser)) {
                patientRepository.save(Patient.builder().user(savedUser).build());
            }
        }

        userRepository.findByEmail("emma.wilson@outlook.com").ifPresent(seeded::add);
        log.info("Seeded {} customer users", seeded.size());
        return seeded;
    }

    private List<User> seedCarrierUsers() {
        List<User> seeded = new ArrayList<>();
        String[][] carrierData = {
                {"UnitedHealth Group", "NAIC-UHG-001", "support@uhg.com", "+1-212-555-1001", "UHG Corporate"},
                {"Blue Cross Blue Shield", "NAIC-BCBS-002", "contact@bcbs.com", "+1-312-555-1002", "BCBS Group"},
                {"Aetna Insurance", "NAIC-AET-003", "service@aetna.com", "+1-415-555-1003", "Aetna Health Group"},
                {"Cigna Healthcare", "NAIC-CIG-004", "help@cigna.com", "+1-646-555-1004", "Cigna Benefits Group"},
                {"Humana Insurance", "NAIC-HUM-005", "info@humana.com", "+1-305-555-1005", "Humana Care Group"}
        };

        for (String[] cd : carrierData) {
            String email = cd[2];
            if (userRepository.existsByEmail(email)) continue;

            User u = User.builder()
                    .username(cd[4])
                    .email(email)
                    .phoneNumber(cd[3])
                    .dateOfBirth(LocalDate.of(1985, 1, 1)).createdAt(LocalDateTime.now().minusMonths(18))
                    .address("Albuquerque, New Mexico")
                    .password(passwordEncoder.encode(DEMO_PASSWORD))
                    .gender(Gender.MALE)
                    .userRole(UserRole.CARRIER)
                    .userStatus(UserStatus.ACTIVE)
                    .build();
            User saved = userRepository.save(u);
            seeded.add(saved);

            if (!carrierRepository.existsByUser(saved)) {
                carrierRepository.save(Carrier.builder()
                        .user(saved)
                        .companyName(cd[0])
                        .registrationNumber(cd[1])
                        .companyType("Health Insurance")
                        .licenseNumber("LIC-" + cd[1])
                        .taxId("TAX-" + cd[1])
                        .website("https://" + cd[0].replace(" ", "").toLowerCase().replace(".", "") + ".com")
                        .build());
            }
        }

        userRepository.findByEmail("michael.turner@healthshield.com").ifPresent(seeded::add);
        log.info("Seeded {} carrier users", seeded.size());
        return seeded;
    }

    private List<Claim> seedClaims(List<User> patients, List<User> carriers) {
        List<Claim> seeded = new ArrayList<>();

        List<Carrier> allCarriers = carrierRepository.findAll();
        if (allCarriers.isEmpty()) {
            log.warn("No carriers found for claim assignment");
            return seeded;
        }

        ClaimStatus[] statusPool = {
                ClaimStatus.SUBMITTED,
                ClaimStatus.AI_VALIDATED,
                ClaimStatus.UNDER_REVIEW,
                ClaimStatus.ADMIN_APPROVED,
                ClaimStatus.CARRIER_APPROVED,
                ClaimStatus.APPROVED,
                ClaimStatus.REJECTED,
                ClaimStatus.PAYMENT_PENDING,
                ClaimStatus.SETTLED,
                ClaimStatus.SETTLED,
                ClaimStatus.SETTLED,
                ClaimStatus.APPROVED
        };

        int targetClaims = 500;
        int batchSize = 50;

        List<Claim> batch = new ArrayList<>();

        for (int i = 0; i < targetClaims; i++) {
            User patient = patients.get(i % patients.size());
            Carrier carrier = allCarriers.get(i % allCarriers.size());

            String[] dx = DIAGNOSES[i % DIAGNOSES.length];

            ClaimStatus claimStatus = statusPool[i % statusPool.length];

            LocalDateTime createdDate = LocalDateTime.now().minusDays(200 - (i % 200)).minusHours(i % 24);

            LocalDate admissionDate = createdDate.toLocalDate().minusDays(3 + (i % 5));
            LocalDate dischargeDate = admissionDate.plusDays(3 + (i % 7));

            double billAmount = 15000 + (random.nextDouble() * 185000);
            double fraudScore = random.nextDouble();

            RiskLevel riskLevel = fraudScore < 0.3 ? RiskLevel.LOW : fraudScore < 0.7 ? RiskLevel.MEDIUM : RiskLevel.HIGH;

            boolean isEscalated = claimStatus == ClaimStatus.UNDER_REVIEW && fraudScore > 0.6;
            boolean isRejected = claimStatus == ClaimStatus.REJECTED;

            String policyIdx = POLICY_NUMBERS[i % POLICY_NUMBERS.length];
            String policyName = POLICY_NAMES[i % POLICY_NAMES.length];

            Claim.ClaimBuilder claimBuilder = Claim.builder()
                    .policyNumber(policyIdx + "-" + String.format("%04d", i + 1))
                    .claimStatus(claimStatus)
                    .amount(isRejected ? 0.0 : billAmount * 0.8)
                    .user(patient)
                    .createdDate(createdDate)
                    .patientName(PATIENT_NAMES[i % PATIENT_NAMES.length])
                    .hospitalName(HOSPITALS[i % HOSPITALS.length])
                    .admissionDate(admissionDate)
                    .dischargeDate(dischargeDate)
                    .totalBillAmount(billAmount)
                    .policyId(policyIdx)
                    .carrierName(carrier.getCompanyName())
                    .policyName(policyName)
                    .claimType(dx[2])
                    .diagnosis(dx[0])
                    .billNumber("BILL-" + String.format("%06d", i + 1))
                    .icdCode(dx[1])
                    .billDate(admissionDate)
                    .carrier(carrier)
                    .reviewedBy(ASSIGNED_REVIEWERS[i % ASSIGNED_REVIEWERS.length])
                    .assignedTo(ASSIGNED_REVIEWERS[i % ASSIGNED_REVIEWERS.length])
                    .riskScore(fraudScore * 100)
                    .riskFlags(riskLevel == RiskLevel.HIGH ? "INFLATED_BILLING, DUPLICATE_PATTERN" : riskLevel == RiskLevel.MEDIUM ? "ELEVATED_COST" : "")
                    .riskLevel(riskLevel)
                    .fraudScore(fraudScore)
                    .fraudFlags(fraudScore > 0.7 ? "HIGH_FRAUD_PROBABILITY, PROVIDER_WATCHLIST" : "")
                    .aiSummary(AI_SUMMARIES[i % AI_SUMMARIES.length])
                    .healthScore(40 + random.nextDouble(60))
                    .tenantId("default")
                    .escalated(isEscalated)
                    .slaDeadline(createdDate.plusHours(48));

            if (isEscalated) {
                claimBuilder.escalatedAt(createdDate.plusHours(36)).escalationReason("High fraud risk score exceeds threshold. Requires senior medical officer review.");
            }

            if (isRejected) {
                claimBuilder.rejectionReason(REJECTION_REASONS[i % REJECTION_REASONS.length])
                        .reviewNotes("Claim rejected after thorough investigation. " + REJECTION_REASONS[i % REJECTION_REASONS.length])
                        .reviewedAt(createdDate.plusDays(3));
            }

            if (claimStatus == ClaimStatus.SETTLED || claimStatus == ClaimStatus.APPROVED || claimStatus == ClaimStatus.ADMIN_APPROVED || claimStatus == ClaimStatus.CARRIER_APPROVED) {
                claimBuilder.processedDate(createdDate.plusDays(2 + random.nextInt(5)))
                        .reviewedAt(createdDate.plusDays(1))
                        .reviewNotes("Claim validated and approved. All documentation verified. Payment initiated.");
            }

            batch.add(claimBuilder.build());

            if (batch.size() == batchSize) {
                seeded.addAll(claimRepository.saveAll(batch));
                batch.clear();
                log.info("  → Seeded {} / {} claims", seeded.size(), targetClaims);
            }
        }

        if (!batch.isEmpty()) {
            seeded.addAll(claimRepository.saveAll(batch));
        }

        log.info("Seeded {} claims total", seeded.size());
        return seeded;
    }

    private void seedNotifications(List<User> customers, List<Claim> claims) {
        String[][] notifTemplates = {
                {"Claim Submitted Successfully", "Your claim %s has been submitted and is under review. Expected processing time: 48 hours."},
                {"Claim Approved", "Great news! Your claim %s has been approved. Settlement of ₹%s is being processed."},
                {"Claim Rejected", "Your claim %s has been rejected. Reason: Insufficient documentation. Please contact support."},
                {"SLA Breach Alert", "Claim %s has exceeded the 48-hour SLA. Escalated to senior reviewer for urgent action."},
                {"Payment Initiated", "Payment of ₹%s for claim %s has been initiated and will reflect in 2-3 business days."},
                {"Document Required", "Additional documents required for claim %s. Please upload your discharge summary."},
                {"AI Validation Complete", "AI pre-validation complete for claim %s. Risk score: LOW. Forwarded for human review."},
                {"Premium Reminder", "Your insurance premium of ₹12,000 is due on %s. Please ensure timely payment to avoid lapse."},
                {"Policy Renewal", "Your %s policy is due for renewal on %s. Renew now to avoid coverage gap."},
                {"Fraud Alert", "Unusual activity detected on claim %s. Our fraud team is investigating. No action needed from you."}
        };

        List<Notification> batch = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < 1000 && count < 1000; i++) {
            User user = customers.get(i % customers.size());
            Claim claim = claims.isEmpty() ? null : claims.get(i % claims.size());

            String[] template = notifTemplates[i % notifTemplates.length];

            String claimReference = claim != null ? claim.getPolicyNumber() : "N/A";
            String amount = String.format("%.0f", 15000 + random.nextDouble() * 50000);
            String message = template[1].replace("%s", claimReference).replace("%s", amount).replace("%s", "15-May-2026").replace("%s", "Family Floater");

            batch.add(Notification.builder()
                    .user(user)
                    .title(template[0])
                    .message(message)
                    .isRead(i % 3 == 0)
                    .targetUrl(claim != null ? "/claim/" + claim.getId() : "/dashboard")
                    .build());

            count++;
            if (batch.size() == 100) {
                notificationRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) notificationRepository.saveAll(batch);
        log.info("Seeded {} notifications", count);
    }

    private void seedPayments(List<Claim> claims, List<User> customers) {
        List<Payment> batch = new ArrayList<>();
        int count = 0;

        for (Claim claim : claims) {
            if (claim.getClaimStatus() == ClaimStatus.SETTLED || claim.getClaimStatus() == ClaimStatus.APPROVED) {
                batch.add(Payment.builder()
                        .claimId(claim.getId())
                        .userId(claim.getUser().getId())
                        .amount(claim.getAmount() != null ? claim.getAmount() : 50000.0)
                        .currency("INR")
                        .status(PaymentStatus.SUCCESS)
                        .razorpayOrderId("order_" + UUID.randomUUID())
                        .razorpayPaymentId("pay_" + UUID.randomUUID())
                        .razorpaySignature("sig_" + UUID.randomUUID())
                        .build());
                count++;

                if (batch.size() == 100) {
                    paymentRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }
        if (!batch.isEmpty()) paymentRepository.saveAll(batch);
        log.info("Seeded {} payment records", count);
    }

    private void seedAuditLogs(List<Claim> claims, List<User> carriers) {
        String[][] transitions = {
                {"CLAIM_SUBMITTED", null, "SUBMITTED"},
                {"AI_VALIDATION_COMPLETE", "SUBMITTED", "AI_VALIDATED"},
                {"ASSIGNED_TO_REVIEWER", "AI_VALIDATED", "UNDER_REVIEW"},
                {"ADMIN_APPROVED", "UNDER_REVIEW", "ADMIN_APPROVED"},
                {"CARRIER_APPROVED", "ADMIN_APPROVED", "CARRIER_APPROVED"},
                {"PAYMENT_INITIATED", "CARRIER_APPROVED", "PAYMENT_PENDING"},
                {"CLAIM_SETTLED", "PAYMENT_PENDING", "SETTLED"}
        };

        List<AuditLog> batch = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < claims.size() && count < 2000; i++) {

            Claim claim = claims.get(i);
            int steps = 2 + (i % transitions.length);

            for (int j = 0; j < steps && j < transitions.length; j++) {
                String[] t = transitions[j];

                batch.add(AuditLog.builder()
                        .claimId(claim.getId())
                        .action(t[0])
                        .previousStatus(t[1] == null ? null : ClaimStatus.valueOf(t[1]))
                        .newStatus(t[2] == null ? null : ClaimStatus.valueOf(t[2]))
                        .performedBy(j == 0 ? claim.getUser().getEmail() : ASSIGNED_REVIEWERS[j % ASSIGNED_REVIEWERS.length])
                        .details("Demo: " + t[0] + " for claim " + claim.getPolicyNumber())
                        .integrityHash("DEMO_HASH_" + String.format("%012d", count))
                        .previousHash(count == 0 ? "GENESIS" : "DEMO_HASH_" + String.format("%012d", count - 1))
                        .build());
                count++;
            }

            if (batch.size() >= 200) {
                auditLogRepository.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) auditLogRepository.saveAll(batch);
        log.info("Seeded {} audit log entries", count);
    }

    private void seedInsuranceProducts() {
        if (insuranceProductRepository.count() > 0) {
            log.info("Insurance products already seeded.");
            return;
        }

        List<Carrier> carriers = carrierRepository.findAll();

        if (carriers.isEmpty()) {
            log.warn("No carriers found. Skipping insurance product seeding.");
            return;
        }

        String[][] products = {
                {"Premium Individual Health", "IND-3819"},
                {"Family Care Plus", "FAM-7654"},
                {"Senior Secure Plan", "SNR-9789"},
                {"Critical Illness Protect", "CRT-4656"},
                {"Corporate Employee Health", "GRP-9243"}
        };

        PolicyType[] policyTypes = {
                PolicyType.INDIVIDUAL_HEALTH,
                PolicyType.FAMILY_FLOATER,
                PolicyType.SENIOR_CITIZEN,
                PolicyType.CRITICAL_ILLNESS,
                PolicyType.GROUP_HEALTH
        };

        List<InsuranceProduct> batch = new ArrayList<>();

        for (Carrier carrier : carriers) {
            for (int i = 0; i < products.length; i++) {

                InsuranceProduct insuranceProduct = InsuranceProduct.builder()
                        .carrier(carrier)
                        .productName(products[i][0])
                        .productCode(products[i][1] + "-" + UUID.randomUUID().toString().substring(0, 6))
                        .policyType(policyTypes[i])
                        .coverageAmount(50000.0 + random.nextDouble() * 450000)
                        .premiumAmount(500.0 + random.nextDouble() * 5000)
                        .waitingPeriodDays(30 + random.nextInt(180))
                        .active(true)
                        .build();

                batch.add(insuranceProduct);
            }
        }

        insuranceProductRepository.saveAll(batch);
        log.info("Seeded {} insurance products", batch.size());
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        long existingClaims = claimRepository.count();

        if (existingClaims >= SEED_THRESHOLD) {
            log.info("Enterprise Demo Seeder: {} claims already exist. Threshold is {}. Skipping full seed.", existingClaims, SEED_THRESHOLD);
            ensureDemoAccountsExist();
            return;
        }

        log.info("Enterprise Data Seeder: Starting full enterprise demo data generation...");
        long start = System.currentTimeMillis();

        ensureDemoAccountsExist();

        List<User> patients = seedPatientUsers();
        List<User> carriers = seedCarrierUsers();

        List<Claim> claims = seedClaims(patients, carriers);

        seedNotifications(patients, claims);
        seedPayments(claims, patients);
        seedAuditLogs(claims, carriers);

        long elapsed = System.currentTimeMillis() - start;
        log.info("Enterprise Demo Seeder: Completed in {}ms. Claims: {}, Users: {}", elapsed, claimRepository.count(), userRepository.count());
    }
}
