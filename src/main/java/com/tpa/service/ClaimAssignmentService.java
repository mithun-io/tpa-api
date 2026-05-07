package com.tpa.service;

import com.tpa.entity.Claim;
import com.tpa.repository.ClaimRepository;
import com.tpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimAssignmentService {

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;

    public void autoAssignClaim(Claim claim) {
        log.info("Auto-assigning claim #{} based on specialty and workload", claim.getId());
        
        // Logical Routing:
        // 1. High Value (> 5L) -> Senior Medical Officer
        // 2. Specialty (e.g. Cancer/Cardiac) -> Specialist
        // 3. Normal -> Round Robin / Random among Agents
        
        String assignedAgent = "system_default";
        
        if (claim.getAmount() != null && claim.getAmount() > 500000) {
            assignedAgent = "senior_medical_officer";
        } else if (claim.getIcdCode() != null && isSpecialistRequired(claim.getIcdCode())) {
            assignedAgent = "specialist_reviewer";
        } else {
            // Mock finding an available agent
            List<String> agents = List.of("agent_rahul", "agent_priya", "agent_amit");
            assignedAgent = agents.get(new Random().nextInt(agents.size()));
        }

        claim.setAssignedTo(assignedAgent);
        claimRepository.save(claim);
        log.info("Claim #{} assigned to {}", claim.getId(), assignedAgent);
    }

    private boolean isSpecialistRequired(String icdCode) {
        // Mock specialty codes
        return icdCode.startsWith("C") || icdCode.startsWith("I2"); // Cancer or Heart Infarction
    }
}
