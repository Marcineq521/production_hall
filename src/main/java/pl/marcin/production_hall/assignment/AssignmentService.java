package pl.marcin.production_hall.assignment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.marcin.production_hall.domain.Assignment;
import pl.marcin.production_hall.domain.Machine;
import pl.marcin.production_hall.machine.MachineRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final MachineRepository machineRepository;

    public List<Assignment> getActiveAssignments(){
        return assignmentRepository.findByEndTimeIsNull();
    }

    public Assignment startAssignment(UUID machineId, String operatorName){
        Assignment existing=assignmentRepository.findByMachine_IdAndEndTimeIsNull(machineId)
                .orElse(null);

        if(existing!=null){
            throw new RuntimeException("Machine already occupied");
        }
        Machine machine =machineRepository.findById(machineId).orElseThrow(()->new RuntimeException("Machine not found"));

        Assignment assignment=Assignment.builder().machine(machine).operatorName(operatorName)
                .build();

        return assignmentRepository.save(assignment);


    }
    public Assignment endAssignment(UUID machineId){
        Assignment assignment=assignmentRepository.findByMachine_IdAndEndTimeIsNull(machineId)
                .orElseThrow(()->new RuntimeException("Active assignment not found for this machine"));
            assignment.setEndTime(OffsetDateTime.now());

            return assignmentRepository.save(assignment);
    }

    public List<Assignment> getAssignmentsForMachine(UUID machineId){
        return assignmentRepository.findByMachine_Id(machineId);
    }
}
