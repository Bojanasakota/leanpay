package app.models.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @Min(value = 0, message = "Annual interest rate mut be positive")
    private double annualInterestRate;

    @Positive(message = "Number of months must be greater than 0")
    private int months;
}
