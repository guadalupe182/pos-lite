package com.Gdev.pos_lite.fit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<Membership> findAll(String tenantId) {
        return membershipRepository.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    public Membership findById(Long id, String tenantId) {
        return membershipRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada: " + id));
    }

    @Transactional
    public Membership create(Membership membership, String tenantId) {
        membership.setTenantId(tenantId);
        return membershipRepository.save(membership);
    }

    @Transactional
    public Membership update(Long id, Membership input, String tenantId) {
        Membership db = findById(id, tenantId);
        db.setClientName(input.getClientName());
        db.setPlanType(input.getPlanType());
        db.setStartDate(input.getStartDate());
        db.setEndDate(input.getEndDate());
        db.setStatus(input.getStatus());
        return membershipRepository.save(db);
    }

    @Transactional
    public void delete(Long id, String tenantId) {
        membershipRepository.delete(findById(id, tenantId));
    }
}