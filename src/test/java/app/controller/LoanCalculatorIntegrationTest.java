package app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.models.Installment;
import app.models.requests.LoanRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanCalculatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCalculateLoanInstallments_InvalidInterestRate() throws Exception {
        LoanRequest request = new LoanRequest();
        request.setAmount(10000.00);
        request.setAnnualInterestRate(-5.0);
        request.setMonths(24);

        mockMvc.perform(post("/api/loans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualMessage = result.getResolvedException().getMessage();
                    assertTrue(actualMessage.contains("Annual interest rate should be a positive value"));
                });
    }

    @Test
    public void testCalculateLoanInstallments_InvalidAmount() throws Exception {
        LoanRequest request = new LoanRequest();
        request.setAmount(-10000.00);
        request.setAnnualInterestRate(5.0);
        request.setMonths(24);

        mockMvc.perform(post("/api/loans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualMessage = result.getResolvedException().getMessage();
                    assertTrue(actualMessage.contains("Amount must be greater than 0"));
                });
    }

    @Test
    public void testCalculateLoanInstallments_InvalidMonths() throws Exception {
        LoanRequest request = new LoanRequest();
        request.setAmount(10000.00);
        request.setAnnualInterestRate(5.0);
        request.setMonths(-5);

        mockMvc.perform(post("/api/loans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String actualMessage = result.getResolvedException().getMessage();
                    assertTrue(actualMessage.contains("Number of months must be greater than 0"));
                });
    }

    @Test
    public void testCalculateLoanInstallments_Success() throws Exception {
        LoanRequest request = new LoanRequest();
        request.setAmount(10000.00);
        request.setAnnualInterestRate(5.0);
        request.setMonths(24);

        mockMvc.perform(post("/api/loans/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    Installment[] installments = objectMapper.readValue(responseContent, Installment[].class);

                    assertEquals(24, installments.length);

                    assertEquals(1, installments[0].getMonthNumber());
                    assertEquals(438.71, installments[0].getTotalPayment());
                    assertEquals(397.04, installments[0].getPrincipalPayment());
                    assertEquals(41.67, installments[0].getInterestPayment());
                    assertEquals(9602.96, installments[0].getRemainingBalance());

                    assertEquals(12, installments[11].getMonthNumber());
                    assertEquals(438.71, installments[11].getTotalPayment());
                    assertEquals(415.63, installments[11].getPrincipalPayment());
                    assertEquals(23.08, installments[11].getInterestPayment());
                    assertEquals(5124.77, installments[11].getRemainingBalance());

                    assertEquals(24, installments[23].getMonthNumber());
                    assertEquals(438.81, installments[23].getTotalPayment());
                    assertEquals(436.99, installments[23].getPrincipalPayment());
                    assertEquals(1.82, installments[23].getInterestPayment());
                    assertEquals(0.00, installments[23].getRemainingBalance());
                });
    }
}
