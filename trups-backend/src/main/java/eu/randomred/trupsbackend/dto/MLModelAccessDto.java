package eu.randomred.trupsbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.randomred.trupsbackend.model.MLModelAccess;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MLModelAccessDto(
        MLModelAccess.MLModelAccessKey id,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt,
        Boolean deleted
) {}
