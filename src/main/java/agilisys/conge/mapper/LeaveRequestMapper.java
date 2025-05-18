package agilisys.conge.mapper;

import agilisys.conge.dto.LeaveRequestDTO;
import agilisys.conge.entity.LeaveRequest;
import agilisys.conge.entity.LeaveStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {
    
    @Mapping(target = "employeeId", expression = "java(Long.parseLong(dto.getEmployeeId()))")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToLeaveStatus")
    LeaveRequest toEntity(LeaveRequestDTO dto);
    
    @Mapping(target = "employeeId", source = "employeeId", qualifiedByName = "longToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "leaveStatusToString")
    LeaveRequestDTO toDto(LeaveRequest entity);
    
    @Named("stringToLeaveStatus")
    default LeaveStatus stringToLeaveStatus(String status) {
        return status != null ? LeaveStatus.valueOf(status) : LeaveStatus.PENDING;
    }
    
    @Named("leaveStatusToString")
    default String leaveStatusToString(LeaveStatus status) {
        return status != null ? status.name() : null;
    }
    
    @Named("longToString")
    default String longToString(Long value) {
        return value != null ? value.toString() : null;
    }
} 