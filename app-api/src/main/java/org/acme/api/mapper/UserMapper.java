package org.acme.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import org.acme.api.dto.UserRequest;
import org.acme.api.dto.UserResponse;
import org.acme.persistence.model.User;

/**
 * MapStruct mapper for User entity and DTOs
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserResponse toResponse(User user);

    User toEntity(UserRequest request);

    void updateEntityFromRequest(UserRequest request, @MappingTarget User user);
}
