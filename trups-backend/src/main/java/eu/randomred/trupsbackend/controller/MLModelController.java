package eu.randomred.trupsbackend.controller;

import eu.randomred.trupsbackend.dto.MLModelDto;
import eu.randomred.trupsbackend.service.MLModelService;
import eu.randomred.trupsbackend.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MLModelController {

    private final MLModelService mlModelService;

    @Autowired
    public MLModelController(MLModelService mlModelService) {
        this.mlModelService = mlModelService;
    }

    @GetMapping("/mlmodels/")
    public ResponseEntity<List<MLModelDto>> getAllMlModels(@RequestParam(defaultValue = "false") Boolean deleted,
                                                           Pageable pageable) {
        return PaginationUtil.buildResponse(mlModelService.findAll(deleted, pageable));
    }

    @GetMapping("/mlmodels/{id}/")
    public ResponseEntity<MLModelDto> getMlModel(@PathVariable Integer id) {
        return ResponseEntity.ok(mlModelService.findOne(id));
    }

    @PostMapping("/mlmodels/")
    public ResponseEntity<MLModelDto> createMlModel(@RequestPart MLModelDto model,
                                                    @RequestPart MultipartFile data) {
        return ResponseEntity.ok(mlModelService.createMlModel(model, data));
    }

    @PatchMapping("/mlmodels/{modelId}")
    public ResponseEntity<MLModelDto> updateMlModel(@PathVariable Integer modelId, @RequestBody MLModelDto mlModelDto) {
        return ResponseEntity.ok(mlModelService.updateMlModel(modelId, mlModelDto));
    }

    @DeleteMapping("/mlmodels/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMlModel(@PathVariable Integer id) {
        mlModelService.deleteMlModel(id);

        return ResponseEntity.ok(null);
    }

    @PostMapping("/mlmodels/{modelId}/evaluation-key")
    public ResponseEntity<Void> uploadEvaluationKey(@PathVariable Integer modelId,
                                                    @RequestPart("eval-key") MultipartFile evaluationKey) {
        mlModelService.uploadEvaluationKey(modelId, evaluationKey);

        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "/mlmodels/{modelId}/client", produces = "application/zip")
    public ResponseEntity<byte[]> getMlModelClientFiles(@PathVariable Integer modelId) {
        return ResponseEntity.ok(mlModelService.getMlModelClientFiles(modelId));
    }

    @PostMapping(value = "/mlmodels/{modelId}/inference")
    public ResponseEntity<byte[]> inference(@PathVariable Integer modelId, @RequestBody byte[] ciphertext) {
        return ResponseEntity.ok(mlModelService.inference(modelId, ciphertext));
    }

}
