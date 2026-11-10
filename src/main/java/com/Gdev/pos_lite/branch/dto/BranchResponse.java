package com.Gdev.pos_lite.branch.dto;

public record BranchResponse(
    Long id,
    String name,
    String address,
    String phone,
    boolean active
) {}
