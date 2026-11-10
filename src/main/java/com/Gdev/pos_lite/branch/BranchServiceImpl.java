package com.Gdev.pos_lite.branch;

import com.Gdev.pos_lite.branch.dto.BranchRequest;
import com.Gdev.pos_lite.branch.dto.BranchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public List<BranchResponse> findAll() {
        return branchRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BranchResponse findById(Long id) {
        return branchRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new NoSuchElementException("Branch not found with id: " + id));
    }

    @Override
    @Transactional
    public BranchResponse create(BranchRequest request) {
        Branch branch = Branch.builder()
                .name(request.name())
                .address(request.address())
                .phone(request.phone())
                .active(true)
                .build();
        
        Branch savedBranch = branchRepository.save(branch);
        return mapToResponse(savedBranch);
    }

    @Override
    @Transactional
    public BranchResponse update(Long id, BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Branch not found with id: " + id));
        
        branch.setName(request.name());
        branch.setAddress(request.address());
        branch.setPhone(request.phone());
        
        Branch updatedBranch = branchRepository.save(branch);
        return mapToResponse(updatedBranch);
    }

    @Override
    @Transactional
    public BranchResponse toggleActive(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Branch not found with id: " + id));
        
        branch.setActive(!branch.isActive());
        Branch updatedBranch = branchRepository.save(branch);
        return mapToResponse(updatedBranch);
    }

    private BranchResponse mapToResponse(Branch branch) {
        return new BranchResponse(
                branch.getId(),
                branch.getName(),
                branch.getAddress(),
                branch.getPhone(),
                branch.isActive()
        );
    }
}
