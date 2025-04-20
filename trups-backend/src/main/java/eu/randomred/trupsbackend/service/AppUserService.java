package eu.randomred.trupsbackend.service;

import eu.randomred.trupsbackend.dto.AppUserDto;
import eu.randomred.trupsbackend.model.AppUser;
import eu.randomred.trupsbackend.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static eu.randomred.trupsbackend.util.SecurityContextHolderUtil.currentAppUser;

@Service
@Transactional
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUserDto getSelf() {
        return AppUser.toDto(currentAppUser());
    }

    public Page<AppUserDto> findAll(Boolean deleted, Pageable pageable) {
        return appUserRepository.findAllByDeleted(deleted, pageable).map(AppUser::toDto);
    }

    public AppUserDto findOne(Integer id) {
        return AppUser.toDto(appUserRepository.findOneById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public AppUserDto createUser(AppUserDto appUserDto) {
        return AppUser.toDto(appUserRepository.save(AppUser
                .builder()
                .username(appUserDto.username())
                .password(appUserDto.password())
                .role(appUserDto.role())
                .build()));
    }

    public AppUserDto updateAppUser(AppUserDto appUserDto) {
        AppUser appUser = appUserRepository.findOneById(appUserDto.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        appUser.partialUpdate(appUserDto);

        return AppUser.toDto(appUserRepository.save(appUser));
    }

    public void deleteAppUser(Integer id) {
        AppUser appUser = appUserRepository.findOneById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        appUser.setDeleted(!appUser.getDeleted());

        appUserRepository.save(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String apiKey) {
        return appUserRepository.findByPasswordAndEnabledTrueAndDeletedFalse(apiKey).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

}
