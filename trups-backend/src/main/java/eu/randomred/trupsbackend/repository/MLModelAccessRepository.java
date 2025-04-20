package eu.randomred.trupsbackend.repository;

import eu.randomred.trupsbackend.dto.MLModelAccessDto;
import eu.randomred.trupsbackend.model.MLModelAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MLModelAccessRepository extends JpaRepository<MLModelAccess, Integer> {

    boolean existsById_AppUserIdAndId_MlModelIdAndEnabledTrue(Integer idAppUserId, Integer idMlModelId);

    Optional<MLModelAccess> findById_AppUserIdAndId_MlModelIdAndDeleted(Integer idAppUserId, Integer idMlModelId, Boolean deleted);

    Optional<MLModelAccess> findById_AppUserIdAndId_MlModelId(Integer idAppUserId, Integer idMlModelId);

    List<MLModelAccess> findAllById_MlModelIdAndDeletedFalse(Integer modelId);

}
