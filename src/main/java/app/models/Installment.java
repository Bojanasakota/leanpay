package app.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "installments")
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int monthNumber;
    @Column(nullable = false)
    private double totalPayment;
    @Column(nullable = false)
    private double principalPayment;
    @Column(nullable = false)
    private double interestPayment;
    @Column(nullable = false)
    private double remainingBalance;
    @ManyToOne
    @JoinColumn(name = "loan_calculation_id")
    private LoanCalculation loanCalculation;

    public Installment(int monthNumber, double totalPayment, double principalPayment, double interestPayment, double remainingBalance) {
        this.monthNumber = monthNumber;
        this.totalPayment = totalPayment;
        this.principalPayment = principalPayment;
        this.interestPayment = interestPayment;
        this.remainingBalance = remainingBalance;
    }
}
