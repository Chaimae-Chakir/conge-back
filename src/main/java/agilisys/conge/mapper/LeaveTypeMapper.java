package agilisys.conge.mapper;

import agilisys.conge.dto.LeaveTypeDto;
import agilisys.conge.entity.LeaveType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {
    LeaveTypeDto toDto(LeaveType leaveType);
}
