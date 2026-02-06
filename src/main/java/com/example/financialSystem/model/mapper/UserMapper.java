package com.example.financialSystem.model.mapper;

import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.requests.UserPatchRequest;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.model.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest userRequest);

    User toEntityAdmin(UserRequest userAdminRequest);

    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entity);

    UserRequest toRequest(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromUpdate(UserRequest userRequest, @MappingTarget User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatch(UserPatchRequest userRequest, @MappingTarget User entity);
}
