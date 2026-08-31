package com.Gdev.pos_lite.branch;

import com.Gdev.pos_lite.branch.dto.BranchRequest;
import com.Gdev.pos_lite.branch.dto.BranchResponse;

import java.util.List;

public interface BranchService {
    List<BranchResponse> findAll();
    BranchResponse findById(Long id);
    BranchResponse create(BranchRequest request);
    BranchResponse update(Long id, BranchRequest request);
    BranchResponse toggleActive(Long id);
}
