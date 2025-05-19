package agilisys.conge.mapper;

import agilisys.conge.dto.LeaveRequestRequestDTO;
import agilisys.conge.dto.LeaveRequestResponseDTO;
import agilisys.conge.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "status", ignore = true)
    LeaveRequest toEntity(LeaveRequestRequestDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "employeeName", source = "employeeName")
    @Mapping(target = "status", source = "status")
    LeaveRequestResponseDTO toResponseDto(LeaveRequest entity);
} 