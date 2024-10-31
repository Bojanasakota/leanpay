package app.controller;

import app.models.requests.LoanRequest;
import app.models.responses.InstallmentResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import app.service.LoanService;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Validated
public class LoanCalculatorController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/calculate")
    public List<InstallmentResponse> calculateLoan(@Valid @RequestBody LoanRequest request) {
        return loanService.calculateInstallmentPlans(request.getAmount(),
                request.getAnnualInterestRate(), request.getMonths());
    }
}
