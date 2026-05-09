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
public class EnterpriseDemoDataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CarrierRepository carrierRepository;
    private final ClaimRepository claimRepository;
    private final NotificationRepository notificationRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEMO_PASSWORD = "Test@123";
    private static final int SEED_THRESHOLD = 50; // Only seed if fewer claims than this

    // ── Realistic Data Arrays ──────────────────────────────────────────────────
    private static final String[] PATIENT_NAMES = {
        "Aarav Sharma", "Priya Mehta", "Rohan Verma", "Ananya Singh", "Karthik Nair",
        "Deepa Iyer", "Vikram Patel", "Sneha Reddy", "Arjun Bose", "Meera Pillai",
        "Suresh Gupta", "Kavya Rao", "Amit Kumar", "Pooja Joshi", "Rahul Chandra",
        "Sunita Devi", "Manoj Tiwari", "Lakshmi Venkat", "Gaurav Malhotra", "Divya Nambiar",
        "Sanjay Krishnan", "Rekha Shah", "Nikhil Aggarwal", "Bhavana Menon", "Ashwin Rajan",
        "Pallavi Desai", "Vivek Choudhary", "Shobha Srivastava", "Harish Pandey", "Usha Narayanan"
    };

    private static final String[] HOSPITALS = {
        "Apollo Hospitals", "Fortis Healthcare", "AIIMS Delhi", "Manipal Hospital",
        "Narayana Health", "Max Super Specialty Hospital", "Medanta The Medicity",
        "Kokilaben Dhirubhai Ambani Hospital", "Sir HN Reliance Foundation Hospital",
        "Lilavati Hospital", "Ruby Hall Clinic", "Breach Candy Hospital",
        "Hinduja Hospital", "Wockhardt Hospital", "Columbia Asia Hospital",
        "Aster Medcity", "Care Hospital", "KIMS Hospital", "Global Hospital",
        "Yashoda Hospital"
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
        "FAM-FLT-2024-001", "CRIT-ILL-2024-002", "HOSP-INS-2024-003",
        "ADD-PRO-2024-004", "RECUP-2024-005", "LIFE-INS-2024-006",
        "MAT-COV-2024-007", "SNR-CIT-2024-008", "CORP-GRP-2024-009",
        "CANC-CARE-2024-010", "OPD-WEL-2024-011", "CARD-CARE-2024-012"
    };

    private static final String[] POLICY_NAMES = {
        "Family Floater", "Critical Illness", "Hospitalization Insurance",
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
        "reviewer.01@tpa.internal", "reviewer.02@tpa.internal", "reviewer.03@tpa.internal",
        "analyst.senior@tpa.internal", "medical.officer@tpa.internal", "claims.head@tpa.internal"
    };

    private final Random random = new Random(42); // Fixed seed for reproducible demo data

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        long existingClaims = claimRepository.count();
        if (existingClaims >= SEED_THRESHOLD) {
            log.info("Enterprise Demo Seeder: {} claims already exist. Threshold is {}. Skipping full seed.", existingClaims, SEED_THRESHOLD);
            ensureDemoAccountsExist();
            return;
        }

        log.info("Enterprise Demo Seeder: Starting full enterprise demo data generation...");
        long start = System.currentTimeMillis();

        ensureDemoAccountsExist();
        List<User> customers = seedCustomerUsers();
        List<User> carriers = seedCarrierUsers();
        List<Claim> claims = seedClaims(customers, carriers);
        seedNotifications(customers, claims);
        seedPayments(claims, customers);
        seedAuditLogs(claims, carriers);

        long elapsed = System.currentTimeMillis() - start;
        log.info("Enterprise Demo Seeder: Completed in {}ms. Claims: {}, Users: {}",
                elapsed, claimRepository.count(), userRepository.count());
    }

    // ── Guarantee Demo Accounts ─────────────────────────────────────────────
    private void ensureDemoAccountsExist() {
        createDemoUserIfAbsent("aerica.pancake@allfreemail.net", "aerica_pancake", "9900000001",
                DEMO_PASSWORD, UserRole.CUSTOMER, Gender.FEMALE, "12 Maple Grove, Mumbai");
        createDemoUserIfAbsent("pwgcy57804@minitts.net", "pwgcy57804", "9900000002",
                DEMO_PASSWORD, UserRole.CARRIER_USER, Gender.MALE, "45 Business Park, Bangalore");

        // Ensure the carrier entity exists for the carrier demo user
        userRepository.findByEmail("pwgcy57804@minitts.net").ifPresent(carrierUser -> {
            if (!carrierRepository.existsByUser(carrierUser)) {
                Carrier carrier = Carrier.builder()
                        .user(carrierUser)
                        .companyName("HealthShield Insurance Ltd.")
                        .registrationNumber("IRDAI-DEMO-HC-001")
                        .companyType("Health Insurance")
                        .licenseNumber("LIC-DEMO-2024-001")
                        .taxId("GSTIN-DEMO-27AABCH")
                        .contactPersonName("Priya Healthshield")
                        .contactPersonPhone("9900000002")
                        .website("https://healthshield.demo")
                        .aiRiskScore(0.12)
                        .aiRiskStatus(AiRiskStatus.LOW_RISK)
                        .aiRecommendation(AiRecommendation.SAFE_TO_APPROVE)
                        .build();
                carrierRepository.save(carrier);
                log.info("Demo carrier entity created for pwgcy57804@minitts.net");
            }
        });
    }

    private void createDemoUserIfAbsent(String email, String username, String mobile,
                                         String password, UserRole role, Gender gender, String address) {
        if (!userRepository.existsByEmail(email)) {
            User user = User.builder()
                    .email(email)
                    .username(username)
                    .mobile(mobile)
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

    // ── Seed Customer Users ─────────────────────────────────────────────────
    private List<User> seedCustomerUsers() {
        List<User> seeded = new ArrayList<>();
        String[] firstNames = {"Aarav","Priya","Rohan","Ananya","Karthik","Deepa","Vikram","Sneha","Arjun","Meera",
                "Suresh","Kavya","Amit","Pooja","Rahul","Sunita","Manoj","Lakshmi","Gaurav","Divya",
                "Sanjay","Rekha","Nikhil","Bhavana","Ashwin","Pallavi","Vivek","Shobha","Harish","Usha"};
        String[] lastNames = {"Sharma","Mehta","Verma","Singh","Nair","Iyer","Patel","Reddy","Bose","Pillai",
                "Gupta","Rao","Kumar","Joshi","Chandra","Devi","Tiwari","Venkat","Malhotra","Nambiar"};
        String[] addresses = {"Mumbai","Delhi","Bangalore","Chennai","Hyderabad","Kolkata","Pune","Ahmedabad","Jaipur","Lucknow"};

        for (int i = 0; i < 50; i++) {
            String fn = firstNames[i % firstNames.length];
            String ln = lastNames[i % lastNames.length];
            String email = fn.toLowerCase() + "." + ln.toLowerCase() + i + "@demo.tpa";
            if (userRepository.existsByEmail(email)) continue;

            User u = User.builder()
                    .username(fn + " " + ln)
                    .email(email)
                    .mobile("98" + String.format("%08d", 10000000 + i))
                    .password(passwordEncoder.encode(DEMO_PASSWORD))
                    .userRole(UserRole.CUSTOMER)
                    .userStatus(UserStatus.ACTIVE)
                    .gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                    .address(addresses[i % addresses.length])
                    .dateOfBirth(LocalDate.of(1975 + (i % 30), (i % 12) + 1, (i % 28) + 1))
                    .createdAt(LocalDateTime.now().minusMonths(12 - (i % 12)))
                    .build();
            seeded.add(userRepository.save(u));
        }

        // Also include the guaranteed demo customer
        userRepository.findByEmail("aerica.pancake@allfreemail.net").ifPresent(seeded::add);
        log.info("Seeded {} customer users", seeded.size());
        return seeded;
    }

    // ── Seed Carrier Users ──────────────────────────────────────────────────
    private List<User> seedCarrierUsers() {
        List<User> seeded = new ArrayList<>();
        String[][] carrierData = {
            {"Star Health Insurance", "IRDAI-SHI-001", "star.health@demo.tpa", "9800100001", "StarHealth GRP"},
            {"HDFC Ergo General Insurance", "IRDAI-HDFC-002", "hdfc.ergo@demo.tpa", "9800100002", "HDFC Ergo GRP"},
            {"ICICI Lombard", "IRDAI-ICICI-003", "icici.lombard@demo.tpa", "9800100003", "ICICI Lombard GRP"},
            {"New India Assurance", "IRDAI-NIA-004", "new.india@demo.tpa", "9800100004", "New India GRP"},
            {"Bajaj Allianz", "IRDAI-BAJAJ-005", "bajaj.allianz@demo.tpa", "9800100005", "Bajaj Allianz GRP"}
        };

        for (String[] cd : carrierData) {
            String email = cd[2];
            if (userRepository.existsByEmail(email)) continue;

            User u = User.builder()
                    .username(cd[4]).email(email).mobile(cd[3])
                    .password(passwordEncoder.encode(DEMO_PASSWORD))
                    .userRole(UserRole.CARRIER_USER).userStatus(UserStatus.ACTIVE)
                    .gender(Gender.MALE).address("Insurance District, Mumbai")
                    .dateOfBirth(LocalDate.of(1985, 1, 1)).createdAt(LocalDateTime.now().minusMonths(18))
                    .build();
            User saved = userRepository.save(u);
            seeded.add(saved);

            if (!carrierRepository.existsByUser(saved)) {
                carrierRepository.save(Carrier.builder()
                    .user(saved).companyName(cd[0]).registrationNumber(cd[1])
                    .companyType("Health Insurance").licenseNumber("LIC-" + cd[1])
                    .taxId("GSTIN-" + cd[1]).contactPersonName("Contact " + cd[0])
                    .contactPersonPhone(cd[3]).website("https://" + cd[0].replace(" ", "").toLowerCase() + ".com")
                    .aiRiskScore(0.1 + random.nextDouble() * 0.3)
                    .aiRiskStatus(AiRiskStatus.LOW_RISK).aiRecommendation(AiRecommendation.SAFE_TO_APPROVE)
                    .build());
            }
        }

        userRepository.findByEmail("pwgcy57804@minitts.net").ifPresent(seeded::add);
        log.info("Seeded {} carrier users", seeded.size());
        return seeded;
    }

    // ── Seed Claims ─────────────────────────────────────────────────────────
    private List<Claim> seedClaims(List<User> customers, List<User> carriers) {
        List<Claim> seeded = new ArrayList<>();

        // Fetch all carriers for assignment
        List<Carrier> allCarriers = carrierRepository.findAll();
        if (allCarriers.isEmpty()) {
            log.warn("No carriers found for claim assignment");
            return seeded;
        }

        ClaimStatus[] statusPool = {
            ClaimStatus.SUBMITTED, ClaimStatus.AI_VALIDATED, ClaimStatus.UNDER_REVIEW,
            ClaimStatus.ADMIN_APPROVED, ClaimStatus.CARRIER_APPROVED, ClaimStatus.APPROVED,
            ClaimStatus.REJECTED, ClaimStatus.PAYMENT_PENDING, ClaimStatus.SETTLED,
            ClaimStatus.SETTLED, ClaimStatus.SETTLED, ClaimStatus.APPROVED // weight towards completed
        };

        int targetClaims = 500;
        int batchSize = 50;
        List<Claim> batch = new ArrayList<>();

        for (int i = 0; i < targetClaims; i++) {
            User customer = customers.get(i % customers.size());
            Carrier carrier = allCarriers.get(i % allCarriers.size());
            String[] dx = DIAGNOSES[i % DIAGNOSES.length];
            ClaimStatus status = statusPool[i % statusPool.length];

            LocalDateTime createdDate = LocalDateTime.now().minusDays(200 - (i % 200)).minusHours(i % 24);
            LocalDate admDate = createdDate.toLocalDate().minusDays(3 + (i % 5));
            LocalDate disDate = admDate.plusDays(3 + (i % 7));
            double billAmt = 15000 + (random.nextDouble() * 185000);
            double fraudScore = random.nextDouble();
            RiskLevel risk = fraudScore < 0.3 ? RiskLevel.LOW : fraudScore < 0.7 ? RiskLevel.MEDIUM : RiskLevel.HIGH;

            boolean isEscalated = status == ClaimStatus.UNDER_REVIEW && fraudScore > 0.6;
            boolean isRejected = status == ClaimStatus.REJECTED;
            String policyIdx = POLICY_NUMBERS[i % POLICY_NUMBERS.length];
            String policyName = POLICY_NAMES[i % POLICY_NAMES.length];

            Claim.ClaimBuilder builder = Claim.builder()
                .policyNumber(policyIdx + "-" + String.format("%04d", i + 1))
                .status(status)
                .amount(isRejected ? 0.0 : billAmt * 0.8)
                .user(customer)
                .createdDate(createdDate)
                .patientName(PATIENT_NAMES[i % PATIENT_NAMES.length])
                .hospitalName(HOSPITALS[i % HOSPITALS.length])
                .admissionDate(admDate)
                .dischargeDate(disDate)
                .totalBillAmount(billAmt)
                .policyId(policyIdx)
                .carrierName(carrier.getCompanyName())
                .policyName(policyName)
                .claimType(dx[2])
                .diagnosis(dx[0])
                .billNumber("BILL-" + String.format("%06d", i + 1))
                .icdCode(dx[1])
                .billDate(admDate)
                .carrier(carrier)
                .reviewedBy(ASSIGNED_REVIEWERS[i % ASSIGNED_REVIEWERS.length])
                .assignedTo(ASSIGNED_REVIEWERS[i % ASSIGNED_REVIEWERS.length])
                .riskScore(fraudScore * 100)
                .riskFlags(risk == RiskLevel.HIGH ? "INFLATED_BILLING,DUPLICATE_PATTERN" : risk == RiskLevel.MEDIUM ? "ELEVATED_COST" : "")
                .riskLevel(risk)
                .fraudScore(fraudScore)
                .fraudFlags(fraudScore > 0.7 ? "HIGH_FRAUD_PROBABILITY,PROVIDER_WATCHLIST" : "")
                .aiSummary(AI_SUMMARIES[i % AI_SUMMARIES.length])
                .healthScore(40 + random.nextInt(60))
                .tenantId("default")
                .escalated(isEscalated)
                .slaDeadline(createdDate.plusHours(48));

            if (isEscalated) {
                builder.escalatedAt(createdDate.plusHours(36))
                       .escalationReason("High fraud risk score exceeds threshold. Requires senior medical officer review.");
            }

            if (isRejected) {
                builder.rejectionReason(REJECTION_REASONS[i % REJECTION_REASONS.length])
                       .reviewNotes("Claim rejected after thorough investigation. " + REJECTION_REASONS[i % REJECTION_REASONS.length])
                       .reviewedAt(createdDate.plusDays(3));
            }

            if (status == ClaimStatus.SETTLED || status == ClaimStatus.APPROVED
                    || status == ClaimStatus.ADMIN_APPROVED || status == ClaimStatus.CARRIER_APPROVED) {
                builder.processedDate(createdDate.plusDays(2 + random.nextInt(5)))
                       .reviewedAt(createdDate.plusDays(1))
                       .reviewNotes("Claim validated and approved. All documentation verified. Payment initiated.");
            }

            batch.add(builder.build());

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

    // ── Seed Notifications ──────────────────────────────────────────────────
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
            String[] tmpl = notifTemplates[i % notifTemplates.length];
            String claimRef = claim != null ? claim.getPolicyNumber() : "N/A";
            String amount = String.format("%.0f", 15000 + random.nextDouble() * 50000);
            String message = tmpl[1].replace("%s", claimRef).replace("%s", amount).replace("%s", "15-May-2026").replace("%s", "Family Floater");

            batch.add(Notification.builder()
                .user(user)
                .title(tmpl[0])
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

    // ── Seed Payments ───────────────────────────────────────────────────────
    private void seedPayments(List<Claim> claims, List<User> customers) {
        List<Payment> batch = new ArrayList<>();
        int count = 0;

        for (Claim claim : claims) {
            if (claim.getStatus() == ClaimStatus.SETTLED || claim.getStatus() == ClaimStatus.APPROVED) {
                batch.add(Payment.builder()
                    .claimId(claim.getId())
                    .userId(claim.getUser().getId())
                    .amount(claim.getAmount() != null ? claim.getAmount() : 50000.0)
                    .currency("INR")
                    .status(PaymentStatus.PAID)
                    .razorpayOrderId("order_DEMO_" + String.format("%08d", count))
                    .razorpayPaymentId("pay_DEMO_" + String.format("%08d", count))
                    .razorpaySignature("sig_DEMO_" + String.format("%08d", count))
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

    // ── Seed Audit Logs ─────────────────────────────────────────────────────
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
                    .previousStatus(t[1])
                    .newStatus(t[2])
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
}
