package com.example.profile.dto;

public record ProfileRequest(
        String userId,
        String filename
) {}