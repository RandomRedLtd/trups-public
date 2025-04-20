package eu.randomred.trupsbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.randomred.trupsbackend.model.AppUserRole;
import lombok.Builder;

import java.time.Instant;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppUserDto(
        Integer id,
        String username,
        String password,
        AppUserRole role,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt,
        Boolean deleted
) {}
