package com.belbenisolution.springbootrestapitemplate.dto;

import java.time.LocalDateTime;

public record TaskResponse(Long id, String title, String description, boolean done, LocalDateTime createdAt) {
}
