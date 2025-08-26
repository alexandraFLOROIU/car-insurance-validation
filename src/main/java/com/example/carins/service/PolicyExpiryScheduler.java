package com.example.carins.service;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PolicyExpiryScheduler {
    private static final Logger log = LoggerFactory.getLogger(PolicyExpiryScheduler.class);
    private InsurancePolicyRepository policyRepository;
    private Set<Long> allLoggedPolicies = new HashSet<>();

    public PolicyExpiryScheduler(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void expiredPolicies() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<InsurancePolicy> allExpiredPolicies =  policyRepository.findByEndDate(yesterday);
        for(InsurancePolicy policy : allExpiredPolicies) {
            Long policyId = policy.getId();
            if(!allLoggedPolicies.contains(policyId)) {
                log.info("Policy with id {} for car with id {} expired on {}",
                        policyId,
                        policy.getCar().getId(),
                        policy.getEndDate());
                allLoggedPolicies.add(policyId);
            }
        }
    }





}
