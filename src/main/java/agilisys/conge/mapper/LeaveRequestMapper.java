package agilisys.conge.mapper;

import agilisys.conge.dto.LeaveRequestRequestDTO;
import agilisys.conge.dto.LeaveRequestResponseDTO;
import agilisys.conge.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "employee", ignore = true)
    LeaveRequest toEntity(LeaveRequestRequestDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "employeeName", source = "employee.name")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    LeaveRequestResponseDTO toResponseDto(LeaveRequest entity);
} 