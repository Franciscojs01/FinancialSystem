package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Investment;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.InvestmentType;
import com.example.financialSystem.util.Investment.InvestmentCreator;
import com.example.financialSystem.util.User.UserCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Investment Repository")
class InvestmentRepositoryTest {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Save Investment When is Valid")
    void save_PersistInvestment_WhenIsValid() {
        Investment investmentToBeSaved = InvestmentCreator.createInvestment();
        Investment savedInvestment = investmentRepository.save(investmentToBeSaved);

        Assertions.assertThat(savedInvestment).isNotNull();
        Assertions.assertThat(savedInvestment.getId()).isNotNull();
        Assertions.assertThat(savedInvestment.getInvestmentType()).isEqualTo(investmentToBeSaved.getInvestmentType());
        Assertions.assertThat(savedInvestment.getBrokerName()).isEqualTo(investmentToBeSaved.getBrokerName());
        Assertions.assertThat(savedInvestment.getValue()).isEqualTo(investmentToBeSaved.getValue());
    }

    @Test
    @DisplayName("Update Investment When Successful")
    void update_UpdateInvestment_WhenSuccessful() {
        Investment investmentToBeSaved = InvestmentCreator.createInvestment();
        Investment savedInvestment = investmentRepository.save(investmentToBeSaved);

        savedInvestment.setInvestmentType(InvestmentType.FIXED_INCOME);
        savedInvestment.setBrokerName("Rico");
        Investment updatedInvestment = investmentRepository.save(savedInvestment);

        Assertions.assertThat(updatedInvestment).isNotNull();
        Assertions.assertThat(updatedInvestment.getId()).isNotNull();
        Assertions.assertThat(updatedInvestment.getInvestmentType()).isEqualTo(InvestmentType.FIXED_INCOME);
        Assertions.assertThat(updatedInvestment.getBrokerName()).isEqualTo("Rico");
    }

    @Test
    @DisplayName("Find Investment By Id When Successful")
    void findById_ReturnInvestment_WhenSuccessful() {
        Investment investmentToBeFound = InvestmentCreator.createInvestment();
        Investment savedInvestment = investmentRepository.save(investmentToBeFound);

        Optional<Investment> foundInvestment = investmentRepository.findById(savedInvestment.getId());

        Assertions.assertThat(foundInvestment).isPresent();
        Assertions.assertThat(foundInvestment.get().getId()).isEqualTo(savedInvestment.getId());
        Assertions.assertThat(foundInvestment.get().getInvestmentType()).isEqualTo(savedInvestment.getInvestmentType());
        Assertions.assertThat(foundInvestment.get().getBrokerName()).isEqualTo(savedInvestment.getBrokerName());
    }

    @Test
    @DisplayName("Find Investment By Id Returns Empty When Not Found")
    void findById_ReturnEmpty_WhenNotFound() {
        Optional<Investment> foundInvestment = investmentRepository.findById(999);

        Assertions.assertThat(foundInvestment).isEmpty();
    }

    @Test
    @DisplayName("Find Investment By User And InvestmentType And BrokerName When Successful")
    void findByUserAndInvestmentTypeAndBrokerName_ReturnInvestment_WhenSuccessful() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Investment investment = InvestmentCreator.createInvestment();
        investment.setUser(savedUser);
        Investment savedInvestment = investmentRepository.save(investment);

        Optional<Investment> foundInvestment = investmentRepository.findByUserAndInvestmentTypeAndBrokerName(
                savedUser,
                savedInvestment.getInvestmentType(),
                savedInvestment.getBrokerName()
        );

        Assertions.assertThat(foundInvestment).isPresent();
        Assertions.assertThat(foundInvestment.get().getUser().getId()).isEqualTo(savedUser.getId());
        Assertions.assertThat(foundInvestment.get().getInvestmentType()).isEqualTo(savedInvestment.getInvestmentType());
        Assertions.assertThat(foundInvestment.get().getBrokerName()).isEqualTo(savedInvestment.getBrokerName());
    }

    @Test
    @DisplayName("Find Investment By User And InvestmentType And BrokerName Returns Empty When Not Found")
    void findByUserAndInvestmentTypeAndBrokerName_ReturnEmpty_WhenNotFound() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Optional<Investment> foundInvestment = investmentRepository.findByUserAndInvestmentTypeAndBrokerName(
                savedUser,
                InvestmentType.STOCK,
                "Broker Inexistente"
        );

        Assertions.assertThat(foundInvestment).isEmpty();
    }

    @Test
    @DisplayName("Find Investments By User And Deleted False When Successful")
    void findByUserAndDeletedFalse_ReturnListOfInvestments_WhenSuccessful() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Investment investment = InvestmentCreator.createInvestment();
        investment.setUser(savedUser);
        investmentRepository.save(investment);

        List<Investment> investmentList = investmentRepository.findByUserAndDeletedFalse(savedUser);

        Assertions.assertThat(investmentList).isNotNull();
        Assertions.assertThat(investmentList).hasSize(1);
        Assertions.assertThat(investmentList.get(0).getUser().getId()).isEqualTo(savedUser.getId());
        Assertions.assertThat(investmentList.get(0).getDeleted()).isFalse();
    }

    @Test
    @DisplayName("Find Investments By User And Deleted False Returns Empty When All Deleted")
    void findByUserAndDeletedFalse_ReturnEmptyList_WhenAllDeleted() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Investment investment = InvestmentCreator.createInvestment();
        investment.setUser(savedUser);
        investment.setDeleted(true);
        investmentRepository.save(investment);

        List<Investment> investmentList = investmentRepository.findByUserAndDeletedFalse(savedUser);

        Assertions.assertThat(investmentList).isEmpty();
    }

    @Test
    @DisplayName("Find All Active Investments When Successful")
    void findAllActive_ReturnListOfInvestments_WhenSuccessful() {
        User user = UserCreator.createUserAdmin();
        User savedUser = userRepository.save(user);

        Investment investment = InvestmentCreator.createInvestment();
        investment.setUser(savedUser);
        investmentRepository.save(investment);

        List<Investment> investmentList = investmentRepository.findAllActive();

        Assertions.assertThat(investmentList).isNotNull();
        Assertions.assertThat(investmentList).isNotEmpty();
    }

    @Test
    @DisplayName("Delete Investment (Soft Delete) When Successful")
    void delete_SoftDeleteInvestment_WhenSuccessful() {
        Investment investmentToBeDeleted = InvestmentCreator.createInvestment();
        Investment savedInvestment = investmentRepository.save(investmentToBeDeleted);

        savedInvestment.setDeleted(true);
        investmentRepository.save(savedInvestment);

        Optional<Investment> optionalInvestment = investmentRepository.findById(savedInvestment.getId());

        Assertions.assertThat(optionalInvestment).isPresent();
        Assertions.assertThat(optionalInvestment.get().getDeleted()).isTrue();
    }
}