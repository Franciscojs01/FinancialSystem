package com.example.financialSystem.models.mapper;

import com.example.financialSystem.models.dto.requests.UserRequest;
import com.example.financialSystem.models.dto.requests.UserPatchRequest;
import com.example.financialSystem.models.dto.responses.UserFinancialResponse;
import com.example.financialSystem.models.dto.responses.UserResponse;
import com.example.financialSystem.models.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(UserRequest userRequest);

    User toEntityAdmin(UserRequest userAdminRequest);

    UserResponse toResponse(User entity);

    List<UserResponse> toResponseList(List<User> entity);

    List<UserFinancialResponse> toFinancialResponseList(List<User> entity);

    @Mapping(target = "financials", source = "financial")
    UserFinancialResponse userToUserFinancialResponse(User user);

    UserRequest toRequest(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromUpdate(UserRequest userRequest, @MappingTarget User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatch(UserPatchRequest userRequest, @MappingTarget User entity);
}
