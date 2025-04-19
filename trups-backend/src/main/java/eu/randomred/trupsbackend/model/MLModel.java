package eu.randomred.trupsbackend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import eu.randomred.trupsbackend.dto.MLModelDto;
import eu.randomred.trupsbackend.service.MLModelType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ml_model")
public class MLModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private AppUser owner;

    private String name;

    private String description;

    @Builder.Default
    private Boolean enabled = false;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MLModelType type;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private MLModelStatus status;

    @Builder.Default
    private Boolean publicAccess = true;

    @Builder.Default
    private Float score = null;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    private List<InputField> inputFields = null;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Builder.Default
    private Boolean deleted = false;

    public static MLModelDto toDto(MLModel mlModel) {
        return MLModelDto.builder()
                .id(mlModel.getId())
                .owner(mlModel.getOwner().getId())
                .name(mlModel.getName())
                .description(mlModel.getDescription())
                .enabled(mlModel.getEnabled())
                .type(mlModel.getType())
                .status(mlModel.getStatus())
                .score(mlModel.getScore())
                .publicAccess(mlModel.getPublicAccess())
                .inputFields(mlModel.getInputFields())
                .createdAt(mlModel.getCreatedAt())
                .updatedAt(mlModel.getUpdatedAt())
                .deleted(mlModel.getDeleted())
                .build();
    }

    public void partialUpdate(MLModelDto mlModelDto) {
        if (mlModelDto.enabled() != null && !this.enabled.equals(mlModelDto.enabled()))
            this.enabled = mlModelDto.enabled();

        if (mlModelDto.publicAccess() != null && !this.publicAccess.equals(mlModelDto.publicAccess()))
            this.publicAccess = mlModelDto.publicAccess();
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record InputField(
            String name,
            InputType type,
            List<String> enumeration
    ) {

        enum InputType {
            INTEGER,
            FLOAT,
            ENUMERATION
        }

    }

}
