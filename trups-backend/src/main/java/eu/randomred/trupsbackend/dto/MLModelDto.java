package eu.randomred.trupsbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.randomred.trupsbackend.model.MLModel;
import eu.randomred.trupsbackend.model.MLModelStatus;
import eu.randomred.trupsbackend.service.MLModelType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MLModelDto(
        Integer id,
        Integer owner,
        String name,
        String description,
        Boolean enabled,
        MLModelType type,
        MLModelStatus status,
        Boolean publicAccess,
        Float score,
        List<MLModel.InputField> inputFields,
        Instant createdAt,
        Instant updatedAt,
        Boolean deleted
) {}
