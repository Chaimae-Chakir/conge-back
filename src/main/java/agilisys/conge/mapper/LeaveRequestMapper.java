package agilisys.conge.mapper;

import agilisys.conge.dto.LeaveRequestDTO;
import agilisys.conge.entity.LeaveRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {
    LeaveRequestDTO toDto(LeaveRequest leaveRequest);
    
    LeaveRequest toEntity(LeaveRequestDTO leaveRequestDTO);
} 