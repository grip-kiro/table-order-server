package com.tableorder.customer.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record LoginRequest(
        @NotNull @Positive Long storeId,
        @NotNull @Positive Integer tableNumber,
        @NotNull @Pattern(regexp = "^\\d{4,6}$") String pin
) {}
