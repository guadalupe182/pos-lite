package com.Gdev.pos_lite.fit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByTenantId(String tenantId);

    Optional<Membership> findByIdAndTenantId(Long id, String tenantId);
}