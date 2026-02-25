package com.example.financialSystem.repositories;

import com.example.financialSystem.models.entity.Cost;
import com.example.financialSystem.models.entity.User;
import com.example.financialSystem.models.enums.CostType;
import com.example.financialSystem.util.Cost.CostCreator;
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
@DisplayName("test for Cost Repository")
class CostRepositoryTest extends CostCreator {
    @Autowired
    private CostRepository costRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Save Cost When Successful")
    void save_PersistCost_WhenSuccessful() {
        Cost costToBeSaved = createCost();
        Cost savedCost = costRepository.save(costToBeSaved);
        Assertions.assertThat(savedCost).isNotNull();
        Assertions.assertThat(savedCost.getId()).isNotNull();
        Assertions.assertThat(savedCost.getCostType()).isEqualTo(costToBeSaved.getCostType());
        Assertions.assertThat(savedCost.getValue()).isEqualTo(costToBeSaved.getValue());
        Assertions.assertThat(savedCost.getObservation()).isEqualTo(costToBeSaved.getObservation());
    }

    @Test
    @DisplayName("Update Cost When Successful")
    void update_UpdateCost_WhenSuccessful() {
        Cost costToBeSaved = createCost();
        Cost updateCost = costRepository.save(costToBeSaved);

        updateCost.setCostType(CostType.OTHER);

        Assertions.assertThat(updateCost).isNotNull();
        Assertions.assertThat(updateCost.getId()).isNotNull();
        Assertions.assertThat(updateCost.getCostType()).isEqualTo(CostType.OTHER);

    }

    @Test
    @DisplayName("Find Cost By Id When Successful")
    void findById_ReturnCost_WhenSuccessful() {
        Cost costToBeFound = createCost();
        costRepository.save(costToBeFound);

        Optional<Cost> foundCost = costRepository.findById(costToBeFound.getId());

        Assertions.assertThat(foundCost).isPresent();
        Assertions.assertThat(foundCost.get().getId()).isEqualTo(costToBeFound.getId());
        Assertions.assertThat(foundCost.get().getCostType()).isEqualTo(costToBeFound.getCostType());
        Assertions.assertThat(foundCost.get().getValue()).isEqualTo(costToBeFound.getValue());
        Assertions.assertThat(foundCost.get().getObservation()).isEqualTo(costToBeFound.getObservation());

    }

    @Test
    @DisplayName("List All Costs When Successful")
    void findAll_ReturnListOfCosts_WhenSuccessful() {
        User user = UserCreator.createUserAdmin();
        User savedUser = userRepository.save(user);

        Cost cost = createCost();
        cost.setUser(savedUser);
        costRepository.save(cost);

        List<Cost> costList = costRepository.findAllActive();
        Assertions.assertThat(costList).isNotNull();
        Assertions.assertThat(costList).contains(cost);
    }

    @Test
    @DisplayName("List costs of activated user When Successful")
    void findByUserAndDeletedFalse_ReturnListOfCosts_WhenSuccessful() {
        User user = UserCreator.createUser();
        User savedUser = userRepository.save(user);

        Cost cost = createCost();
        cost.setUser(savedUser);
        costRepository.save(cost);

        List<Cost> costList = costRepository.findByUserAndDeletedFalse(savedUser);

        Assertions.assertThat(costList).isNotNull();
        Assertions.assertThat(costList).hasSize(1);
        Assertions.assertThat(costList.get(0).getCostType()).isEqualTo(cost.getCostType());
        Assertions.assertThat(costList.get(0).getValue()).isEqualTo(cost.getValue());
        Assertions.assertThat(costList.get(0).getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    @DisplayName("Delete Cost When Successful")
    void delete_RemoveCost_WhenSuccessful() {
        Cost costToBeDeleted = createCost();

        Cost savedCost = costRepository.save(costToBeDeleted);
        savedCost.setDeleted(Boolean.TRUE);
        costRepository.save(savedCost);

        Optional<Cost> optionalCost = costRepository.findById(savedCost.getId());

        Assertions.assertThat(optionalCost).isNotEmpty();
        Assertions.assertThat(optionalCost.get().getDeleted()).isTrue();
    }
}