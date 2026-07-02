package com.academicsaas.academic.presentation.dto;

import java.util.UUID;

public record UserDto(
    UUID id, String email, String firstName, String lastName, String phone, String status
) {}
