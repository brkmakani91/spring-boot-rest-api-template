package com.belbenisolution.springbootrestapitemplate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Documents the RFC 9457 error responses produced by GlobalExceptionHandler on every endpoint.
 */
@Configuration
public class OpenApiConfig {

    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String PROBLEM_REF = "#/components/schemas/ProblemDetail";

    @Bean
    public OpenApiCustomizer problemDetailResponses() {
        return openApi -> {
            if (openApi.getComponents() == null) {
                openApi.setComponents(new Components());
            }
            openApi.getComponents()
                    .addSchemas("FieldViolation", fieldViolationSchema())
                    .addSchemas("ProblemDetail", problemDetailSchema());

            openApi.getPaths().values().forEach(path ->
                    path.readOperations().forEach(OpenApiConfig::addErrorResponses));
        };
    }

    private static void addErrorResponses(Operation operation) {
        ApiResponses responses = operation.getResponses();
        boolean hasBody = operation.getRequestBody() != null;
        boolean hasPathId = operation.getParameters() != null
                && operation.getParameters().stream().anyMatch(p -> "path".equals(p.getIn()));

        if (hasBody) {
            Map<String, Object> example = example("Validation failed", 400,
                    "One or more fields are invalid.", "/api/v1/tasks");
            example.put("errors", List.of(Map.of("field", "title", "message", "Title can't be empty")));
            responses.addApiResponse("400", problem("Invalid request body", example));
        } else if (hasPathId) {
            responses.addApiResponse("400", problem("Invalid path parameter", example("Invalid parameter", 400,
                    "Parameter 'id' must be of type Long, but got 'abc'.", "/api/v1/tasks/abc")));
        }
        if (hasPathId) {
            responses.addApiResponse("404", problem("Task not found", example("Resource not found", 404,
                    "Task with ID not found 42", "/api/v1/tasks/42")));
        }
        responses.addApiResponse("500", problem("Unexpected server error", example("Internal server error", 500,
                "An unexpected error occurred.", "/api/v1/tasks")));
    }

    // LinkedHashMap keeps the fields in RFC order in the Swagger UI example
    private static Map<String, Object> example(String title, int status, String detail, String instance) {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("title", title);
        example.put("status", status);
        example.put("detail", detail);
        example.put("instance", instance);
        example.put("timestamp", "2026-09-24T19:30:46.909194Z");
        return example;
    }

    private static ApiResponse problem(String description, Map<String, Object> example) {
        MediaType mediaType = new MediaType()
                .schema(new Schema<>().$ref(PROBLEM_REF))
                .addExamples("example", new Example().value(example));
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(PROBLEM_JSON, mediaType));
    }

    private static Schema<?> problemDetailSchema() {
        return new ObjectSchema()
                .description("RFC 9457 Problem Details")
                .addProperty("type", new StringSchema().format("uri")
                        .description("URI identifying the error type; omitted when it is about:blank"))
                .addProperty("title", new StringSchema().description("Short summary of the error type"))
                .addProperty("status", new IntegerSchema().description("HTTP status code"))
                .addProperty("detail", new StringSchema().description("Explanation specific to this occurrence"))
                .addProperty("instance", new StringSchema().format("uri").description("Path of the failed request"))
                .addProperty("timestamp", new DateTimeSchema().description("When the error occurred"))
                .addProperty("errors", new ArraySchema()
                        .items(new Schema<>().$ref("#/components/schemas/FieldViolation"))
                        .description("Invalid fields; present only on validation errors"));
    }

    private static Schema<?> fieldViolationSchema() {
        return new ObjectSchema()
                .addProperty("field", new StringSchema())
                .addProperty("message", new StringSchema());
    }
}
