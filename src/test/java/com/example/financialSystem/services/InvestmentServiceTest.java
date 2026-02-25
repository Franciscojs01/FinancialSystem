package com.example.financialSystem.services;

import com.example.financialSystem.exceptions.NoChangeDetectedException;
import com.example.financialSystem.exceptions.duplicates.InvestmentDuplicateException;
import com.example.financialSystem.exceptions.notFound.InvestmentNotFoundException;
import com.example.financialSystem.models.dto.requests.InvestmentPatchRequest;
import com.example.financialSystem.models.dto.requests.InvestmentRequest;
import com.example.financialSystem.models.dto.responses.InvestmentResponse;
import com.example.financialSystem.models.entity.Investment;
import com.example.financialSystem.models.entity.Login;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.models.enums.UserRole;
import com.example.financialSystem.models.mapper.InvestmentMapper;
import com.example.financialSystem.repositories.InvestmentRepository;
import com.example.financialSystem.repositories.LoginRepository;
import com.example.financialSystem.utils.BenchMarkRate;
import com.example.financialSystem.util.Investment.InvestmentCreator;
import com.example.financialSystem.util.Investment.InvestmentRequestCreator;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for InvestmentService")
class InvestmentServiceTest {

    @InjectMocks
    private InvestmentService investmentService;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private InvestmentMapper investmentMapper;

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private User user;
    private Login login;
    private Investment investment;
    private InvestmentResponse investmentResponse;

    @BeforeEach
    void setUp() {
        user = UserCreator.createUser();
        user.setId(1);
        user.setUserRole(UserRole.USER);

        login = new Login();
        login.setId(1);
        login.setUsername("test@test.com");
        login.setUser(user);

        investment = InvestmentCreator.createInvestment();
        investment.setUser(user);
        investment.setBaseCurrency(BenchMarkRate.BRL);

        investmentResponse = new InvestmentResponse();
        investmentResponse.setId(1);
        investmentResponse.setInvestmentType(InvestmentType.STOCK);

        SecurityContextHolder.setContext(securityContext);
    }

    private void mockLoggedUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
        when(loginRepository.findByUsername("test@test.com")).thenReturn(Optional.of(login));
    }

    @Test
    @DisplayName("createInvestment returns InvestmentResponse when successful")
    void createInvestment_ReturnsInvestmentResponse_WhenSuccessful() {
        mockLoggedUser();
        InvestmentRequest request = InvestmentRequestCreator.createInvestmentRequest();

        when(investmentMapper.toEntity(request)).thenReturn(investment);
        when(investmentRepository.findByUserAndInvestmentTypeAndBrokerName(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);
        when(investmentMapper.toResponse(investment)).thenReturn(investmentResponse);

        InvestmentResponse result = investmentService.createInvestment(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(investmentRepository, times(1)).save(any(Investment.class));
    }

    @Test
    @DisplayName("createInvestment throws InvestmentDuplicateException when stock investment already exists")
    void createInvestment_ThrowsInvestmentDuplicateException_WhenStockExists() {
        mockLoggedUser();
        InvestmentRequest request = InvestmentRequestCreator.createInvestmentRequest();

        investment.setInvestmentType(InvestmentType.STOCK);

        when(investmentMapper.toEntity(request)).thenReturn(investment);
        when(investmentRepository.findByUserAndInvestmentTypeAndBrokerName(any(), any(), any()))
                .thenReturn(Optional.of(investment));

        assertThatThrownBy(() -> investmentService.createInvestment(request))
                .isInstanceOf(InvestmentDuplicateException.class);
    }

    @Test
    @DisplayName("createInvestment throws IllegalArgumentException when date is in the future")
    void createInvestment_ThrowsIllegalArgumentException_WhenDateIsInFuture() {
        mockLoggedUser();
        InvestmentRequest request = InvestmentRequestCreator.createInvestmentRequest();

        Investment futureInvestment = InvestmentCreator.createInvestment();
        futureInvestment.setDateFinancial(LocalDate.now().plusDays(1));

        when(investmentMapper.toEntity(request)).thenReturn(futureInvestment);

        assertThatThrownBy(() -> investmentService.createInvestment(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }

    @Test
    @DisplayName("updateInvestment returns InvestmentResponse when successful")
    void updateInvestment_ReturnsInvestmentResponse_WhenSuccessful() {
        mockLoggedUser();
        InvestmentRequest request = InvestmentRequestCreator.createUpdatedInvestmentRequest();

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);
        when(investmentMapper.toResponse(investment)).thenReturn(investmentResponse);

        InvestmentResponse result = investmentService.updateInvestment(1, request);

        assertThat(result).isNotNull();
        verify(investmentMapper, times(1)).updateEntityFromUpdate(request, investment);
    }

    @Test
    @DisplayName("updateInvestment throws InvestmentNotFoundException when investment not found")
    void updateInvestment_ThrowsInvestmentNotFoundException_WhenNotFound() {
        InvestmentRequest request = InvestmentRequestCreator.createUpdatedInvestmentRequest();

        when(investmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.updateInvestment(999, request))
                .isInstanceOf(InvestmentNotFoundException.class);
    }

    @Test
    @DisplayName("updateInvestment throws AccessDeniedException when user is not owner")
    void updateInvestment_ThrowsAccessDeniedException_WhenUserIsNotOwner() {
        mockLoggedUser();
        InvestmentRequest request = InvestmentRequestCreator.createUpdatedInvestmentRequest();

        User anotherUser = UserCreator.createUser();
        anotherUser.setId(999);
        investment.setUser(anotherUser);

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));

        assertThatThrownBy(() -> investmentService.updateInvestment(1, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("patchInvestment returns InvestmentResponse when successful")
    void patchInvestment_ReturnsInvestmentResponse_WhenSuccessful() {
        mockLoggedUser();
        InvestmentPatchRequest patchRequest = InvestmentRequestCreator.createInvestmentPatchRequest();

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);
        when(investmentMapper.toResponse(investment)).thenReturn(investmentResponse);

        InvestmentResponse result = investmentService.patchInvestment(1, patchRequest);

        assertThat(result).isNotNull();
        verify(investmentMapper, times(1)).updateEntityFromPatch(patchRequest, investment);
    }

    @Test
    @DisplayName("getInvestmentById returns InvestmentResponse when successful")
    void getInvestmentById_ReturnsInvestmentResponse_WhenSuccessful() {
        mockLoggedUser();

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));
        when(investmentMapper.toResponse(investment)).thenReturn(investmentResponse);

        InvestmentResponse result = investmentService.getInvestmentById(1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("getInvestmentById throws InvestmentNotFoundException when not found")
    void getInvestmentById_ThrowsInvestmentNotFoundException_WhenNotFound() {
        when(investmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.getInvestmentById(999))
                .isInstanceOf(InvestmentNotFoundException.class);
    }

    @Test
    @DisplayName("listInvestments returns list of InvestmentResponse when successful")
    void listInvestments_ReturnsListOfInvestmentResponse_WhenSuccessful() {
        mockLoggedUser();

        when(investmentRepository.findByUserAndDeletedFalse(user)).thenReturn(List.of(investment));
        when(investmentMapper.toResponseList(List.of(investment))).thenReturn(List.of(investmentResponse));

        List<InvestmentResponse> result = investmentService.listInvestments();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("listAllInvestments returns list of all InvestmentResponse when successful")
    void listAllInvestments_ReturnsListOfAllInvestmentResponse_WhenSuccessful() {
        when(investmentRepository.findAllActive()).thenReturn(List.of(investment));
        when(investmentMapper.toResponseList(List.of(investment))).thenReturn(List.of(investmentResponse));

        List<InvestmentResponse> result = investmentService.listAllInvestments();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("activateInvestment activates investment when successful")
    void activateInvestment_ActivatesInvestment_WhenSuccessful() {
        mockLoggedUser();
        investment.setDeleted(true);

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        assertThatCode(() -> investmentService.activateInvestment(1))
                .doesNotThrowAnyException();

        verify(investmentRepository, times(1)).save(investment);
    }

    @Test
    @DisplayName("activateInvestment throws IllegalStateException when investment is already active")
    void activateInvestment_ThrowsIllegalStateException_WhenAlreadyActive() {
        mockLoggedUser();
        investment.setDeleted(false);

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));

        assertThatThrownBy(() -> investmentService.activateInvestment(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already active");
    }

    @Test
    @DisplayName("deleteInvestment deletes investment when successful")
    void deleteInvestment_DeletesInvestment_WhenSuccessful() {
        mockLoggedUser();
        investment.setDeleted(false);

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        assertThatCode(() -> investmentService.deleteInvestment(1))
                .doesNotThrowAnyException();

        verify(investmentRepository, times(1)).save(investment);
    }

    @Test
    @DisplayName("deleteInvestment throws IllegalStateException when investment is already deleted")
    void deleteInvestment_ThrowsIllegalStateException_WhenAlreadyDeleted() {
        mockLoggedUser();
        investment.setDeleted(true);

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));

        assertThatThrownBy(() -> investmentService.deleteInvestment(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already deleted");
    }

    @Test
    @DisplayName("simulateInvestment returns InvestmentResponse with future value when successful")
    void simulateInvestment_ReturnsInvestmentResponseWithFutureValue_WhenSuccessful() {
        mockLoggedUser();

        when(investmentRepository.findById(1)).thenReturn(Optional.of(investment));
        when(investmentMapper.toResponse(investment)).thenReturn(investmentResponse);

        InvestmentResponse result = investmentService.simulateInvestment(1, 365);

        assertThat(result).isNotNull();
        verify(investmentRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("simulateInvestment throws IllegalArgumentException when days is zero or negative")
    void simulateInvestment_ThrowsIllegalArgumentException_WhenDaysIsZeroOrNegative() {
        assertThatThrownBy(() -> investmentService.simulateInvestment(1, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");

        assertThatThrownBy(() -> investmentService.simulateInvestment(1, -10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    @DisplayName("simulateInvestment throws InvestmentNotFoundException when investment not found")
    void simulateInvestment_ThrowsInvestmentNotFoundException_WhenNotFound() {
        when(investmentRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> investmentService.simulateInvestment(999, 30))
                .isInstanceOf(InvestmentNotFoundException.class);
    }

    @Test
    @DisplayName("ensureChanged throws NoChangeDetectedException when no changes")
    void ensureChanged_ThrowsNoChangeDetectedException_WhenNoChanges() {
        InvestmentRequest sameRequest = new InvestmentRequest(
                investment.getInvestmentType(),
                investment.getValue(),
                investment.getDateFinancial(),
                investment.getBaseCurrency(),
                investment.getActionQuantity(),
                investment.getBrokerName()
        );

        assertThatThrownBy(() -> investmentService.ensureChanged(investment, sameRequest))
                .isInstanceOf(NoChangeDetectedException.class);
    }

    @Test
    @DisplayName("recalculateFields updates current value")
    void recalculateFields_UpdatesCurrentValue() {
        investment.setDateFinancial(LocalDate.now().minusDays(30));
        investment.setValue(new BigDecimal("1000.00"));
        investment.setBaseCurrency(BenchMarkRate.BRL);

        investmentService.recalculateFields(investment);

        assertThat(investment.getCurrentValue()).isNotNull();
        assertThat(investment.getCurrentValue()).isGreaterThanOrEqualTo(investment.getValue());
    }
}
