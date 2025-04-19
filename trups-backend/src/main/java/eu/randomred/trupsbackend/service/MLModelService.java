package eu.randomred.trupsbackend.service;

import eu.randomred.trupsbackend.dto.MLModelDto;
import eu.randomred.trupsbackend.model.AppUserRole;
import eu.randomred.trupsbackend.model.MLModel;
import eu.randomred.trupsbackend.model.MLModelStatus;
import eu.randomred.trupsbackend.repository.MLModelAccessRepository;
import eu.randomred.trupsbackend.repository.MLModelRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static eu.randomred.trupsbackend.util.SecurityContextHolderUtil.currentAppUser;
import static eu.randomred.trupsbackend.util.SecurityContextHolderUtil.isCurrentUserAdmin;

@Service
@Slf4j
public class MLModelService {

    private final MLModelRepository mlModelRepository;
    private final MLModelAccessRepository mlModelAccessRepository;
    private final FileService fileService;
    private final MLService mlService;
    private final SchedulerService schedulerService;

    @Autowired
    public MLModelService(MLModelRepository mlModelRepository,
                          MLModelAccessRepository mlModelAccessRepository,
                          FileService fileService,
                          MLService mlService,
                          SchedulerService schedulerService) {
        this.mlModelRepository = mlModelRepository;
        this.mlModelAccessRepository = mlModelAccessRepository;
        this.fileService = fileService;
        this.mlService = mlService;
        this.schedulerService = schedulerService;
    }

    public Page<MLModelDto> findAll(Boolean deleted, Pageable pageable) {
        if (deleted && !isCurrentUserAdmin())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return mlModelRepository.findAllByDeleted(deleted, pageable).map(MLModel::toDto);
    }

    public MLModelDto findOne(Integer id) {
        MLModelDto mlModelDto = MLModel.toDto(mlModelRepository.findOneById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        if (mlModelDto.deleted() && !isCurrentUserAdmin())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return mlModelDto;
    }

    @Transactional
    public MLModelDto createMlModel(MLModelDto mlModelDto, MultipartFile file) {
        MLModel mlModel = createMlModel(mlModelDto);

        fileService.saveModelData(mlModel.getId(), file);

        schedulerService.scheduleTrainingJob(mlModel.getId());

        return MLModel.toDto(mlModel);
    }

    @Transactional
    public MLModelDto updateMlModel(Integer modelId, MLModelDto mlModelDto) {
        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAuthorizedForOperation(mlModel))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        mlModel.partialUpdate(mlModelDto);

        return MLModel.toDto(mlModelRepository.save(mlModel));
    }

    public void deleteMlModel(Integer id) {
        MLModel mlModel = mlModelRepository.findOneById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        mlModel.setDeleted(!mlModel.getDeleted());

        mlModelRepository.save(mlModel);
    }

    public byte[] getMlModelClientFiles(Integer modelId) {
        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAuthorizedForInference(mlModel))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return fileService.getModelClientFiles(modelId);
    }

    public byte[] inference(Integer modelId, byte[] ciphertext) {
        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAuthorizedForInference(mlModel))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return mlService.runInference(currentAppUser().getId(), modelId, ciphertext);
    }

    @Transactional
    public void uploadEvaluationKey(Integer modelId, MultipartFile evaluationKey) {
        MLModel mlModel = mlModelRepository.findOneById(modelId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAuthorizedForInference(mlModel))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        fileService.saveEvaluationKey(currentAppUser().getId(), modelId, evaluationKey);
    }

    private MLModel createMlModel(MLModelDto mlModelDto) {
        return mlModelRepository.save(MLModel.builder()
                .owner(currentAppUser())
                .name(mlModelDto.name())
                .description(mlModelDto.description())
                .type(mlModelDto.type())
                .status(MLModelStatus.IN_QUEUE)
                .publicAccess(mlModelDto.publicAccess() == null || mlModelDto.publicAccess())
                .inputFields(mlModelDto.inputFields())
                .build());
    }

    private Boolean isAuthorizedForOperation(MLModel mlModel) {
        return isCurrentUserAdmin() || currentAppUser().getId().equals(mlModel.getOwner().getId());
    }

    private Boolean isAuthorizedForInference(MLModel mlModel) {
        return isAuthorizedForOperation(mlModel) || mlModelAccessRepository.existsById_AppUserIdAndId_MlModelIdAndEnabledTrue(currentAppUser().getId(), mlModel.getId()) || mlModel.getPublicAccess();
    }

}
