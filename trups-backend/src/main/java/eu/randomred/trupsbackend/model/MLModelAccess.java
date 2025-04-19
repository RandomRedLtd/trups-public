package eu.randomred.trupsbackend.model;

import eu.randomred.trupsbackend.dto.MLModelAccessDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ml_model_access")
public class MLModelAccess {

    @EmbeddedId
    private MLModelAccessKey id;

    @Builder.Default
    private Boolean enabled = true;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder.Default
    private Boolean deleted = true;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Embeddable
    @EqualsAndHashCode
    public static class MLModelAccessKey implements Serializable {

        private Integer appUserId;

        private Integer mlModelId;

    }

    public static MLModelAccessDto toDto(MLModelAccess mlModelAccess) {
        return MLModelAccessDto.builder()
                .id(mlModelAccess.getId())
                .enabled(mlModelAccess.getEnabled())
                .build();
    }

    public void partialUpdate(MLModelAccessDto mlModelAccessDto) {
        if (mlModelAccessDto.enabled() != null && !this.enabled.equals(mlModelAccessDto.enabled()))
            this.enabled = mlModelAccessDto.enabled();
    }

}
