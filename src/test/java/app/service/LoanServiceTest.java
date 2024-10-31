package app.service;

import app.models.responses.InstallmentResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LoanServiceTest {

    @Autowired
    private LoanService loanCalculationService;


    @Test
    void testCalculateInstallmentPlans_ValidInput() {
        double amount = 1000.0;
        double annualInterestRate = 5.0;
        int months = 10;

        List<InstallmentResponse> installments = loanCalculationService.calculateInstallmentPlans(amount, annualInterestRate, months);

        assertEquals(months, installments.size(), "The number of installments should match the number of months.");
        assertEquals(1, installments.get(0).getMonthNumber(), "The first installment should be for month 1.");
        assertEquals(102.31, installments.get(0).getTotalPayment(), "First installment total payment is incorrect.");
        assertEquals(102.31, installments.get(0).getPrincipalPayment() + installments.get(0).getInterestPayment(), "Payments should sum to the monthly payment.");
    }

    @Test
    void testCalculateInstallmentPlans_ZeroAmount() {
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            loanCalculationService.calculateInstallmentPlans(0, 5.0, 10);
        });

        assertTrue(exception.getMessage().contains("Amount must be greater than 0"));
    }

    @Test
    void testCalculateInstallmentPlans_NegativeAmount() {
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            loanCalculationService.calculateInstallmentPlans(-1000, 5.0, 10);
        });

        assertTrue(exception.getMessage().contains("Amount must be greater than 0"));
    }

    @Test
    void testCalculateInstallmentPlans_NegativeInterestRate() {
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            loanCalculationService.calculateInstallmentPlans(1000, -5.0, 10);
        });

        assertTrue(exception.getMessage().contains("Annual interest rate should be a positive value"));
    }

    @Test
    void testCalculateInstallmentPlans_ZeroMonths() {
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            loanCalculationService.calculateInstallmentPlans(1000, 5.0, 0);
        });

        assertTrue(exception.getMessage().contains("Number of months must be greater than 0"));
    }

    @Test
    void testCalculateInstallmentPlans_NegativeMonths() {
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            loanCalculationService.calculateInstallmentPlans(1000, 5.0, -5);
        });

        assertTrue(exception.getMessage().contains("Number of months must be greater than 0"));
    }
}
