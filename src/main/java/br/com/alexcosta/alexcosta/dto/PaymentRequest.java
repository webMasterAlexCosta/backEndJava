package br.com.alexcosta.alexcosta.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class PaymentRequest {
    // Getters e Setters
    private BigInteger transactionAmount;
    private String token;
    private String description;
    private Integer installments;
    private String paymentMethodId;
    private Payer payer;

    @Setter
    @Getter
    public static class Payer {
        // Getters e Setters
        private String email;
        private String firstName;
        private Identification identification;

        @Setter
        @Getter
        public static class Identification {
            // Getters e Setters
            private String type;
            private String number;

        }
    }
}