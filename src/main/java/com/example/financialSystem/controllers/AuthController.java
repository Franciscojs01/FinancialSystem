package com.example.financialSystem.controllers;

import com.example.financialSystem.exceptions.handlers.ExceptionDetails;
import com.example.financialSystem.models.dto.requests.LoginRequest;
import com.example.financialSystem.models.dto.requests.RefreshTokenRequest;
import com.example.financialSystem.models.dto.responses.LoginResponse;
import com.example.financialSystem.models.dto.responses.RefreshTokenResponse;
import com.example.financialSystem.models.entity.RefreshToken;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.services.AuthService;
import com.example.financialSystem.services.RefreshTokenService;
import com.example.financialSystem.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user login and security token generation")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;

    public AuthController(AuthService authService,
                          RefreshTokenService refreshTokenService,
                          TokenService tokenService
                          ) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "/login")
    @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens for stateless authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ExceptionDetails.class)))
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
            ) {

        LoginResponse loginResponse = authService.login(loginRequest);

        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using the refresh token. Only for users without Remember Me.")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String newAccessToken = tokenService.generateToken(user.getLogin());
        RefreshTokenResponse newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        return ResponseEntity.ok(new RefreshTokenResponse(
                newAccessToken,
                newRefreshToken.refreshToken(),
                "Bearer"
        ));
    }
}