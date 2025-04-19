package eu.randomred.trupsbackend.util;

import eu.randomred.trupsbackend.model.AppUser;
import eu.randomred.trupsbackend.model.AppUserRole;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHolderUtil {

    public static AppUser currentAppUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Boolean isCurrentUserAdmin() {
        return currentAppUser().getRole().equals(AppUserRole.ROLE_ADMIN);
    }

}
