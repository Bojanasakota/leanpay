package app.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentResponse {

    private int monthNumber;
    private double totalPayment;
    private double principalPayment;
    private double interestPayment;
    private double remainingBalance;
}
