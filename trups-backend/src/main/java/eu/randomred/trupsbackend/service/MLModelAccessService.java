package eu.randomred.trupsbackend.service;

import eu.randomred.trupsbackend.dto.MLModelAccessDto;
import eu.randomred.trupsbackend.model.AppUser;
import eu.randomred.trupsbackend.model.MLModel;
import eu.randomred.trupsbackend.model.MLModelAccess;
import eu.randomred.trupsbackend.repository.MLModelAccessRepository;
import eu.randomred.trupsbackend.repository.MLModelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static eu.randomred.trupsbackend.util.SecurityContextHolderUtil.currentAppUser;
import static eu.randomred.trupsbackend.util.SecurityContextHolderUtil.isCurrentUserAdmin;

@Service
public class MLModelAccessService {

    private final MLModelRepository mlModelRepository;
    private final MLModelAccessRepository mlModelAccessRepository;

    @Autowired
    public MLModelAccessService(MLModelRepository mlModelRepository, MLModelAccessRepository mlModelAccessRepository) {
        this.mlModelRepository = mlModelRepository;
        this.mlModelAccessRepository = mlModelAccessRepository;
    }

    public MLModelAccessDto findOne(Integer modelId, Integer userId, Boolean deleted) {
        if (isCurrentUserAdmin()) {
            if (userId == null)
                return null;

            return MLModelAccess.toDto(mlModelAccessRepository.findById_AppUserIdAndId_MlModelIdAndDeleted(userId, modelId, deleted).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        }

        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (mlModel.getOwner().getId().equals(currentAppUser().getId()))
            return null;

        return MLModelAccess.toDto(mlModelAccessRepository.findById_AppUserIdAndId_MlModelIdAndDeleted(currentAppUser().getId(), modelId, false).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public List<MLModelAccessDto> findAll(Integer modelId) {
        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isCurrentUserAdmin() && currentAppUser().getId().equals(mlModel.getOwner().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return mlModelAccessRepository.findAllById_MlModelIdAndDeletedFalse(modelId).stream().map(MLModelAccess::toDto).toList();

    }

    public void createMlModelAccess(Integer modelId, Integer granteeId) {
        AppUser appUser = currentAppUser();

        if (!isCurrentUserAdmin() && !mlModelRepository.existsByIdAndOwner_Id(modelId, appUser.getId()))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        mlModelAccessRepository.save(MLModelAccess.builder()
                .id(MLModelAccess.MLModelAccessKey.builder().appUserId(granteeId).mlModelId(modelId).build())
                .enabled(true)
                .deleted(false)
                .build());
    }

    @Transactional
    public void updateMlModelAccess(Integer modelId, Integer granteeId, MLModelAccessDto mlModelAccessDto) {
        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isCurrentUserAdmin() && currentAppUser().getId().equals(mlModel.getOwner().getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        Optional<MLModelAccess> mlModelAccess = mlModelAccessRepository.findById_AppUserIdAndId_MlModelIdAndDeleted(granteeId, modelId, false);

        if (mlModelAccess.isEmpty()) {
            if (mlModelAccessDto.enabled())
                createMlModelAccess(modelId, granteeId);

            return;
        }

        mlModelAccess.get().partialUpdate(mlModelAccessDto);

        mlModelAccessRepository.save(mlModelAccess.get());
    }

    public void deleteMlModelAccess(Integer modelId, Integer userId) {
        MLModelAccess mlModelAccess = mlModelAccessRepository.findById_AppUserIdAndId_MlModelId(userId, modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        mlModelAccess.setDeleted(!mlModelAccess.getDeleted());

        mlModelAccessRepository.save(mlModelAccess);
    }

}
