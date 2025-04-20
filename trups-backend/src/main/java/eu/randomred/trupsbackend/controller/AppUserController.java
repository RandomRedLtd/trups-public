package eu.randomred.trupsbackend.controller;

import eu.randomred.trupsbackend.dto.AppUserDto;
import eu.randomred.trupsbackend.service.AppUserService;
import eu.randomred.trupsbackend.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppUserController {

    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/users/self")
    public ResponseEntity<AppUserDto> getSelf() {
        return ResponseEntity.ok(appUserService.getSelf());
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppUserDto>> getAllAppUsers(
            @RequestParam(defaultValue = "false") Boolean deleted,
            Pageable pageable) {
        return PaginationUtil.buildResponse(appUserService.findAll(deleted, pageable));
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUserDto> getAppUser(@PathVariable Integer id) {
        return ResponseEntity.ok(appUserService.findOne(id));
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUserDto> createAppUser(@RequestBody AppUserDto appUserDto) {
        return ResponseEntity.ok(appUserService.createUser(appUserDto));
    }

    @PatchMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUserDto> updateAppUser(@RequestBody AppUserDto appUserDto) {
        return ResponseEntity.ok(appUserService.updateAppUser(appUserDto));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Integer id) {
        appUserService.deleteAppUser(id);

        return ResponseEntity.ok(null);
    }

}
