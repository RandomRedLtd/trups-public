package eu.randomred.trupsbackend.model;

import eu.randomred.trupsbackend.dto.AppUserDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static eu.randomred.trupsbackend.util.SecurityContextHolderUtil.currentAppUser;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "app_user")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;

    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AppUserRole role;

    private Boolean enabled;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Boolean deleted;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public static AppUserDto toDto(AppUser appUser) {
        AppUserDto.AppUserDtoBuilder appUserDtoBuilder = AppUserDto.builder()
                .id(appUser.getId())
                .username(appUser.getUsername());

        if (currentAppUser().getRole().equals(AppUserRole.ROLE_ADMIN))
            appUserDtoBuilder.role(appUser.getRole())
                    .enabled(appUser.getEnabled())
                    .createdAt(appUser.getCreatedAt())
                    .updatedAt(appUser.getUpdatedAt())
                    .deleted(appUser.getDeleted());

        return appUserDtoBuilder.build();
    }

    public void partialUpdate(AppUserDto appUserDto) {
        if (appUserDto.username() != null && !this.username.equals(appUserDto.username()))
            this.password = appUserDto.password();

        if (appUserDto.password() != null && !this.password.equals(appUserDto.password()))
            this.password = appUserDto.password();

        if (appUserDto.enabled() != null && !this.enabled.equals(appUserDto.enabled()))
            this.enabled = appUserDto.enabled();
    }

}
