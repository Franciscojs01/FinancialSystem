package com.example.financialSystem.models.mapper;

import com.example.financialSystem.models.dto.responses.RefreshTokenResponse;
import com.example.financialSystem.models.entity.RefreshToken;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

    @Mapping(target = "refreshToken", source = "token")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "accessToken", ignore = true)
    RefreshTokenResponse toRefreshToken(RefreshToken token);
}
