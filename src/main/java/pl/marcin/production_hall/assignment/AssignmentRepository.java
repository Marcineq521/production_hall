package pl.marcin.production_hall.assignment;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.marcin.production_hall.domain.Assignment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    List<Assignment> findByEndTimeIsNull();

    Optional<Assignment> findByMachineIdAndEndTimeIsNull(UUID machineId);

    List<Assignment> findByMachineId(UUID machineId);
}
