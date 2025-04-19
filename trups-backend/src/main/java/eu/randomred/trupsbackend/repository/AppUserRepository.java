package eu.randomred.trupsbackend.repository;

import eu.randomred.trupsbackend.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findOneById(Integer id);

    Optional<AppUser> findByPasswordAndEnabledTrueAndDeletedFalse(String apiKey);

    Page<AppUser> findAllByDeleted(Boolean deleted, Pageable pageable);

}
