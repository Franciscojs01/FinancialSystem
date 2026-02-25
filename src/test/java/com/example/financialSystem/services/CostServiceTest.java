package com.example.financialSystem.services;

import com.example.financialSystem.exceptions.NoChangeDetectedException;
import com.example.financialSystem.exceptions.duplicates.CostDuplicateException;
import com.example.financialSystem.exceptions.notFound.CostNotFoundException;
import com.example.financialSystem.models.dto.requests.CostPatchRequest;
import com.example.financialSystem.models.dto.requests.CostRequest;
import com.example.financialSystem.models.dto.responses.CostResponse;
import com.example.financialSystem.models.entity.Cost;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.models.enums.UserRole;
import com.example.financialSystem.models.mapper.CostMapper;
import com.example.financialSystem.repositories.CostRepository;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.util.Cost.CostCreator;
import com.example.financialSystem.util.Cost.CostRequestCreator;
import com.example.financialSystem.util.User.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for CostService")
class CostServiceTest {

    @InjectMocks
    private CostService costService;

    @Mock
    private CostRepository costRepository;

    @Mock
    private CostMapper costMapper;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User user;
    private Login login;
    private Cost cost;
    private CostResponse costResponse;

    @BeforeEach
    void setUp() {
        user = UserCreator.createUser();
        user.setId(1);
        user.setUserRole(UserRole.USER);

        login = new Login();
        login.setId(1);
        login.setUsername("test@test.com");
        login.setUser(user);

        cost = CostCreator.createCost();
        cost.setUser(user);

        costResponse = new CostResponse();
        costResponse.setId(1);
        costResponse.setCostType(CostType.OTHER);

        SecurityContextHolder.setContext(securityContext);
    }

    private void mockLoggedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        when(loginRepository.findByUsername("test@test.com")).thenReturn(Optional.of(login));
    }

    @Test
    @DisplayName("createCost returns CostResponse when successful")
    void createCost_ReturnsCostResponse_WhenSuccessful() {
        mockLoggedUser();
        CostRequest request = CostRequestCreator.createCostRequest();

        when(costMapper.toEntity(request)).thenReturn(cost);
        when(costRepository.existsByUserAndCostTypeAndDateFinancial(any(), any(), any())).thenReturn(false);
        when(costRepository.save(any(Cost.class))).thenReturn(cost);
        when(costMapper.toResponse(cost)).thenReturn(costResponse);

        CostResponse result = costService.createCost(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(costRepository, times(1)).save(any(Cost.class));
    }

    @Test
    @DisplayName("createCost throws CostDuplicateException when cost already exists")
    void createCost_ThrowsCostDuplicateException_WhenCostExists() {
        mockLoggedUser();
        CostRequest request = CostRequestCreator.createCostRequest();

        when(costMapper.toEntity(request)).thenReturn(cost);
        when(costRepository.existsByUserAndCostTypeAndDateFinancial(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> costService.createCost(request))
                .isInstanceOf(CostDuplicateException.class);
    }

    @Test
    @DisplayName("createCost throws IllegalArgumentException when date is in the future")
    void createCost_ThrowsIllegalArgumentException_WhenDateIsInFuture() {
        mockLoggedUser();
        CostRequest request = CostRequestCreator.createCostRequest();

        Cost futureCost = CostCreator.createCost();
        futureCost.setDateFinancial(LocalDate.now().plusDays(1));

        when(costMapper.toEntity(request)).thenReturn(futureCost);

        assertThatThrownBy(() -> costService.createCost(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    @Test
    @DisplayName("updateCost returns CostResponse when successful")
    void updateCost_ReturnsCostResponse_WhenSuccessful() {
        mockLoggedUser();
        CostRequest request = CostRequestCreator.createUpdatedCostRequest();

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));
        when(costMapper.toEntity(request)).thenReturn(cost);
        when(costRepository.save(any(Cost.class))).thenReturn(cost);
        when(costMapper.toResponse(cost)).thenReturn(costResponse);

        CostResponse result = costService.updateCost(1, request);

        assertThat(result).isNotNull();
        verify(costMapper, times(1)).updateFromUpdate(request, cost);
    }

    @Test
    @DisplayName("updateCost throws CostNotFoundException when cost not found")
    void updateCost_ThrowsCostNotFoundException_WhenNotFound() {
        CostRequest request = CostRequestCreator.createUpdatedCostRequest();

        when(costRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> costService.updateCost(999, request))
                .isInstanceOf(CostNotFoundException.class);
    }

    @Test
    @DisplayName("updateCost throws AccessDeniedException when user is not owner")
    void updateCost_ThrowsAccessDeniedException_WhenUserIsNotOwner() {
        mockLoggedUser();
        CostRequest request = CostRequestCreator.createUpdatedCostRequest();

        User anotherUser = UserCreator.createUser();
        anotherUser.setId(999);
        cost.setUser(anotherUser);

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));
        when(costMapper.toEntity(request)).thenReturn(cost);

        assertThatThrownBy(() -> costService.updateCost(1, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("patchCost returns CostResponse when successful")
    void patchCost_ReturnsCostResponse_WhenSuccessful() {
        mockLoggedUser();
        CostPatchRequest patchRequest = CostRequestCreator.createCostPatchRequest();

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));
        when(costRepository.save(any(Cost.class))).thenReturn(cost);
        when(costMapper.toResponse(cost)).thenReturn(costResponse);

        CostResponse result = costService.patchCost(1, patchRequest);

        assertThat(result).isNotNull();
        verify(costMapper, times(1)).updateFromPatch(patchRequest, cost);
    }

    @Test
    @DisplayName("getCostById returns CostResponse when successful")
    void getCostById_ReturnsCostResponse_WhenSuccessful() {
        mockLoggedUser();

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));
        when(costMapper.toResponse(cost)).thenReturn(costResponse);

        CostResponse result = costService.getCostById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getCostById throws CostNotFoundException when not found")
    void getCostById_ThrowsCostNotFoundException_WhenNotFound() {
        when(costRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> costService.getCostById(999))
                .isInstanceOf(CostNotFoundException.class);
    }

    @Test
    @DisplayName("listCost returns list of CostResponse when successful")
    void listCost_ReturnsListOfCostResponse_WhenSuccessful() {
        mockLoggedUser();

        when(costRepository.findByUserAndDeletedFalse(user)).thenReturn(List.of(cost));
        when(costMapper.toResponseList(List.of(cost))).thenReturn(List.of(costResponse));

        List<CostResponse> result = costService.listCost();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("listAllCost returns list of all CostResponse when successful")
    void listAllCost_ReturnsListOfAllCostResponse_WhenSuccessful() {
        when(costRepository.findAllActive()).thenReturn(List.of(cost));
        when(costMapper.toResponseList(List.of(cost))).thenReturn(List.of(costResponse));

        List<CostResponse> result = costService.listAllCost();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("activateCost activates cost when successful")
    void activateCost_ActivatesCost_WhenSuccessful() {
        mockLoggedUser();
        cost.setDeleted(true);

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));
        when(costRepository.save(any(Cost.class))).thenReturn(cost);

        assertThatCode(() -> costService.activateCost(1))
                .doesNotThrowAnyException();

        verify(costRepository, times(1)).save(cost);
    }

    @Test
    @DisplayName("activateCost throws IllegalArgumentException when cost is already active")
    void activateCost_ThrowsIllegalArgumentException_WhenAlreadyActive() {
        mockLoggedUser();
        cost.setDeleted(false);

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));

        assertThatThrownBy(() -> costService.activateCost(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already active");
    }

    @Test
    @DisplayName("deleteCost deletes cost when successful")
    void deleteCost_DeletesCost_WhenSuccessful() {
        mockLoggedUser();
        cost.setDeleted(false);

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));
        when(costRepository.save(any(Cost.class))).thenReturn(cost);

        assertThatCode(() -> costService.deleteCost(1))
                .doesNotThrowAnyException();

        verify(costRepository, times(1)).save(cost);
    }

    @Test
    @DisplayName("deleteCost throws IllegalStateException when cost is already deleted")
    void deleteCost_ThrowsIllegalStateException_WhenAlreadyDeleted() {
        mockLoggedUser();
        cost.setDeleted(true);

        when(costRepository.findById(1)).thenReturn(Optional.of(cost));

        assertThatThrownBy(() -> costService.deleteCost(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already deleted");
    }

    @Test
    @DisplayName("ensureChanged throws NoChangeDetectedException when no changes")
    void ensureChanged_ThrowsNoChangeDetectedException_WhenNoChanges() {
        CostRequest sameRequest = new CostRequest(
                cost.getCostType(),
                cost.getObservation(),
                cost.getValue(),
                cost.getDateFinancial(),
                cost.getBaseCurrency()
        );

        assertThatThrownBy(() -> costService.ensureChanged(cost, sameRequest))
                .isInstanceOf(NoChangeDetectedException.class);
    }

    @Test
    @DisplayName("validateCostDate throws IllegalArgumentException when date is in the future")
    void validateCostDate_ThrowsIllegalArgumentException_WhenDateIsInFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);

        assertThatThrownBy(() -> costService.validateCostDate(futureDate))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

