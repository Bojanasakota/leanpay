package app.service;

import app.models.Installment;
import app.models.LoanCalculation;
import app.models.responses.InstallmentResponse;
import app.repository.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import app.repository.LoanCalculationRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {
    @Autowired
    private LoanCalculationRepository loanCalculationRepository;
    @Autowired
    private InstallmentRepository installmentRepository;

    @Transactional
    public List<InstallmentResponse> calculateInstallmentPlans(double amount, double annualInterestRate, int months) {

        if (amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be greater than 0");
        }
        if (annualInterestRate < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Annual interest rate should be a positive value");
        }
        if (months <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of months must be greater than 0");
        }

        LoanCalculation loanCalculation = new LoanCalculation();
        loanCalculation.setAmount(amount);
        loanCalculation.setAnnualInterestRate(annualInterestRate);
        loanCalculation.setMonths(months);

        double monthlyRate = annualInterestRate / 100 / 12;
        double monthlyPayment = round((amount * monthlyRate * Math.pow(1 + monthlyRate, months)) /
                (Math.pow(1 + monthlyRate, months) - 1));

        List<Installment> installments = new ArrayList<>();
        double remainingBalance = amount;

        for (int month = 1; month <= months; month++) {
            double interestPayment = round(remainingBalance * monthlyRate);
            double principalPayment = round(monthlyPayment - interestPayment);

            // Adjust balance for the final month
            if (month == months) {
                principalPayment = round(remainingBalance);
                interestPayment = round(monthlyRate * remainingBalance);
                monthlyPayment = round(principalPayment + interestPayment);
                remainingBalance = 0;
            } else {
                remainingBalance = round(remainingBalance - principalPayment);
            }

            Installment installment = new Installment(month, monthlyPayment, principalPayment, interestPayment, remainingBalance);
            installment.setLoanCalculation(loanCalculation);
            installments.add(installment);
        }

        loanCalculation.setInstallments(installments);
        loanCalculationRepository.save(loanCalculation);

        return installments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private InstallmentResponse mapToResponse(Installment installment) {
        return new InstallmentResponse(
                installment.getMonthNumber(),
                installment.getTotalPayment(),
                installment.getPrincipalPayment(),
                installment.getInterestPayment(),
                installment.getRemainingBalance()
        );
    }
}
