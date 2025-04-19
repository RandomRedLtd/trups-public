package eu.randomred.trupsbackend.repository;

import eu.randomred.trupsbackend.model.MLModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MLModelRepository extends JpaRepository<MLModel, Integer>  {

    Optional<MLModel> findOneById(Integer id);

    boolean existsByIdAndOwner_Id(Integer id, Integer ownerId);

    Page<MLModel> findAllByDeleted(Boolean deleted, Pageable pageable);

}
