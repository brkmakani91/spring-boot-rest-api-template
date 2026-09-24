package com.belbenisolution.springbootrestapitemplate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank(message = "Title can't be empty")
        @Size(max = 100, message = "The title must not exceed 100 characters.")
        String title,

        @Size(max = 500)
        String description,
        Boolean done
) {}
