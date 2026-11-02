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
    public List<Membership> findAll() {
        return membershipRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Membership findById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada: " + id));
    }

    @Transactional
    public Membership create(Membership membership) {
        return membershipRepository.save(membership);
    }

    @Transactional
    public Membership update(Long id, Membership input) {
        Membership db = findById(id);
        db.setClientName(input.getClientName());
        db.setPlanType(input.getPlanType());
        db.setStartDate(input.getStartDate());
        db.setEndDate(input.getEndDate());
        db.setStatus(input.getStatus());
        db.setTenantId(input.getTenantId());
        return membershipRepository.save(db);
    }

    @Transactional
    public void delete(Long id) {
        membershipRepository.deleteById(id);
    }
}