package com.example.financialSystem.controller;

import com.example.financialSystem.exception.handler.ExceptionDetails;
import com.example.financialSystem.model.dto.requests.UserPatchRequest;
import com.example.financialSystem.model.dto.requests.UserRequest;
import com.example.financialSystem.model.dto.responses.UserFinancialResponse;
import com.example.financialSystem.model.dto.responses.UserResponse;
import com.example.financialSystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "Users", description = "Endpoints for managing user accounts.")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/admin/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create admin user", description = "Creates a user with the ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = "{ \"name\": \"Admin\", \"email\": \"admin@example.com\" }")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class),
                            examples = @ExampleObject(value = "{ \"title\": \"Invalid Request\", \"statusCode\": 400, \"details\": \"Invalid fields\" }")
                    )),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class),
                            examples = @ExampleObject(value = "{ \"title\": \"Duplicate\", \"statusCode\": 409, \"details\": \"Email already exists\" }")
                    ))
    })
    public ResponseEntity<UserResponse> registerAdminUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.registerAdminUser(request));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register standard user", description = "Registers a user with the USER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = "{ \"name\": \"John\", \"email\": \"john@example.com\" }")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )),
            @ApiResponse(responseCode = "409", description = "Email already registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.registerUser(request));
    }

    @PutMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update user", description = "Updates the data of the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Permission denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<UserResponse> editUser(@PathVariable int id, @RequestBody UserRequest request) {
        return ResponseEntity.ok().body(userService.updateUser(id, request));
    }

    @PatchMapping(value = "/patch/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Patch user", description = "Applies a partial update (patch) to the user data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User patch applied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Permission denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    )),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<UserResponse> patchUser(@PathVariable int id, @RequestBody UserPatchRequest request) {
        return ResponseEntity.ok().body(userService.patchUser(id, request));
    }

    @GetMapping(value = "/list/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all users", description = "Returns a list of active users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok().body(userService.listAllUser());
    }

    @GetMapping(value = "/list/financial", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List users with financials",
            description = "Returns a list of users including their financial data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List retrieved",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserFinancialResponse.class),
                            examples = @ExampleObject(value =
                                    "[ { \"id\": 1, \"name\": \"John\", " +
                                            "\"financials\": [ { \"id\": 10, \"value\": 100.0, " +
                                            "\"dateFinancial\": \"2026-02-10\", \"financialType\": \"INCOME\", " +
                                            "\"deleted\": false } ] } ]")
                    )),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<List<UserFinancialResponse>> getAllUsersFinancial() {
        return ResponseEntity.ok().body(userService.listAllUserFinancial());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user by ID", description = "Returns the specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<UserResponse> getUser(@PathVariable int id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PutMapping(value = "/{id}/deactivate")
    @Operation(summary = "Deactivate user", description = "Marks a user as deleted/inactive.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deactivated"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<Void> deactivateUser(@PathVariable int id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "{id}/activate")
    @Operation(summary = "Activate user", description = "Reactivates a previously deleted/deactivated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User activated"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExceptionDetails.class)
                    ))
    })
    public ResponseEntity<Void> activateUser(@PathVariable int id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}