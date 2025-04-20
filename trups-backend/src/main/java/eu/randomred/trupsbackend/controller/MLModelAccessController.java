package eu.randomred.trupsbackend.controller;

import eu.randomred.trupsbackend.dto.MLModelAccessDto;
import eu.randomred.trupsbackend.service.MLModelAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MLModelAccessController {

    private final MLModelAccessService mlModelAccessService;

    @Autowired
    public MLModelAccessController(MLModelAccessService mlModelAccessService) {
        this.mlModelAccessService = mlModelAccessService;
    }

    @GetMapping("/mlmodelaccess/{modelId}")
    public ResponseEntity<MLModelAccessDto> getMlModelAccess(@PathVariable Integer modelId,
                                                             @RequestParam(required = false) Integer userId,
                                                             @RequestParam(required = false, defaultValue = "false") Boolean deleted) {
        return ResponseEntity.ok(mlModelAccessService.findOne(modelId, userId, deleted));
    }

    @GetMapping("/mlmodelaccess/{modelId}/all")
    public ResponseEntity<List<MLModelAccessDto>> getAllMlModelAccess(@PathVariable Integer modelId) {
        return ResponseEntity.ok(mlModelAccessService.findAll(modelId));
    }

    @PostMapping("/mlmodelaccess/{modelId}/{userId}")
    public ResponseEntity<Void> createMlModelAccess(@PathVariable Integer modelId, @PathVariable Integer userId) {
        mlModelAccessService.createMlModelAccess(modelId, userId);

        return ResponseEntity.ok(null);
    }

    @PatchMapping("/mlmodelaccess/{modelId}/{userId}")
    public ResponseEntity<Void> updateMLModelAccess(@PathVariable Integer modelId, @PathVariable Integer userId, @RequestBody MLModelAccessDto mlModelAccessDto) {
        mlModelAccessService.updateMlModelAccess(modelId, userId, mlModelAccessDto);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/mlmodelaccess/{modelId}/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteMlModelAccess(@PathVariable Integer modelId, @PathVariable Integer userId) {
        mlModelAccessService.deleteMlModelAccess(modelId, userId);

        return ResponseEntity.ok(null);
    }

}
