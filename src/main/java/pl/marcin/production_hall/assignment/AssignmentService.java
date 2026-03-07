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

    public List<AssignmentResponse> getActiveAssignments(){
        return assignmentRepository.findByEndTimeIsNull().stream().map(this::mapToResponse).toList();
    }

    public AssignmentResponse startAssignment(UUID machineId, String operatorName){
        Assignment existing=assignmentRepository.findByMachine_IdAndEndTimeIsNull(machineId)
                .orElse(null);

        if(existing!=null){
            throw new RuntimeException("Machine already occupied");
        }
        Machine machine =machineRepository.findById(machineId).orElseThrow(()->new RuntimeException("Machine not found"));

        Assignment assignment=Assignment.builder().machine(machine).operatorName(operatorName)
                .build();

        Assignment saved=assignmentRepository.save(assignment);

        return mapToResponse(saved);


    }
    public AssignmentResponse endAssignment(UUID machineId){
        Assignment assignment=assignmentRepository.findByMachine_IdAndEndTimeIsNull(machineId)
                .orElseThrow(()->new RuntimeException("Active assignment not found for this machine"));
            assignment.setEndTime(OffsetDateTime.now());

         Assignment saved=assignmentRepository.save(assignment);
          return mapToResponse(saved);
    }

    public List<AssignmentResponse> getAssignmentsForMachine(UUID machineId){
        return assignmentRepository.findByMachine_Id(machineId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AssignmentResponse mapToResponse(Assignment assignment){
        return AssignmentResponse.builder()
                .id(assignment.getId())
                .machineId(assignment.getMachine().getId())
                .machineCode(assignment.getMachine().getCode())
                .operatorName(assignment.getOperatorName())
                .startTime(assignment.getStartTime())
                .endTime(assignment.getEndTime())
                .build();

    }
}
