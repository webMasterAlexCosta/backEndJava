package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.dto.PaymentRequest;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${mercado.pago.access.token}")
    private String accessToken;

    // Processar Pagamento com Cartão de Crédito/Débito
    @PostMapping("/credit-card")
    public String processCreditCardPayment(@RequestBody PaymentRequest request) {
        try {
            // Validação dos campos obrigatórios
            if (request.getTransactionAmount() == null || request.getTransactionAmount().isEmpty()) {
                return "Erro: transactionAmount é obrigatório.";
            }
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return "Erro: token é obrigatório.";
            }
            if (request.getDescription() == null || request.getDescription().isEmpty()) {
                return "Erro: description é obrigatória.";
            }
            if (request.getInstallments() == null || request.getInstallments() <= 0) {
                return "Erro: installments deve ser maior que zero.";
            }
            if (request.getPaymentMethodId() == null || request.getPaymentMethodId().isEmpty()) {
                return "Erro: paymentMethodId é obrigatório.";
            }
            if (request.getPayer() == null || request.getPayer().getEmail() == null || request.getPayer().getEmail().isEmpty()) {
                return "Erro: payer.email é obrigatório.";
            }
            if (request.getPayer().getIdentification() == null ||
                    request.getPayer().getIdentification().getType() == null ||
                    request.getPayer().getIdentification().getNumber() == null) {
                return "Erro: payer.identification.type e payer.identification.number são obrigatórios.";
            }

            // Configura o token de acesso
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentClient client = new PaymentClient();
            PaymentCreateRequest paymentCreateRequest =
                    PaymentCreateRequest.builder()
                            .transactionAmount(new BigDecimal(request.getTransactionAmount()))
                            .token(request.getToken())
                            .description(request.getDescription())
                            .installments(request.getInstallments())
                            .paymentMethodId(request.getPaymentMethodId())
                            .payer(
                                    PaymentPayerRequest.builder()
                                            .email(request.getPayer().getEmail())
                                            .firstName(request.getPayer().getFirstName())
                                            .identification(
                                                    IdentificationRequest.builder()
                                                            .type(request.getPayer().getIdentification().getType())
                                                            .number(request.getPayer().getIdentification().getNumber())
                                                            .build())
                                            .build())
                            .build();

            var payment = client.create(paymentCreateRequest);
            return "Pagamento realizado com sucesso! ID: " + payment.getId();
        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            return "Erro ao processar o pagamento: " + e.getMessage();
        }
    }

    // Processar Pagamento com PIX
    @PostMapping("/pix")
    public String processPixPayment() {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentClient client = new PaymentClient();
            PaymentCreateRequest paymentCreateRequest =
                    PaymentCreateRequest.builder()
                            .transactionAmount(new BigDecimal("100"))
                            .description("Título do produto")
                            .paymentMethodId("pix")
                            .dateOfExpiration(OffsetDateTime.of(2023, 1, 10, 10, 10, 10, 0, ZoneOffset.UTC))
                            .payer(
                                    PaymentPayerRequest.builder()
                                            .email("test@test.com")
                                            .firstName("Test")
                                            .identification(
                                                    IdentificationRequest.builder()
                                                            .type("CPF")
                                                            .number("19119119100")
                                                            .build())
                                            .build())
                            .build();

            var payment = client.create(paymentCreateRequest);
            return "Pagamento PIX criado com sucesso! Detalhes: " + payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            return "Erro ao processar o pagamento PIX: " + e.getMessage();
        }
    }

    // Processar Pagamento com Boleto
    @PostMapping("/boleto")
    public String processBoletoPayment() {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            PaymentClient client = new PaymentClient();
            PaymentCreateRequest paymentCreateRequest =
                    PaymentCreateRequest.builder()
                            .transactionAmount(new BigDecimal("100"))
                            .description("Título do produto")
                            .paymentMethodId("bolbradesco")
                            .dateOfExpiration(OffsetDateTime.of(2023, 1, 10, 10, 10, 10, 0, ZoneOffset.UTC))
                            .payer(
                                    PaymentPayerRequest.builder()
                                            .email("test@test.com")
                                            .firstName("Test")
                                            .lastName("User")
                                            .identification(
                                                    IdentificationRequest.builder()
                                                            .type("CPF")
                                                            .number("19119119100")
                                                            .build())
                                            .build())
                            .build();

            var payment = client.create(paymentCreateRequest);
            return "Boleto gerado com sucesso! URL: " + payment.getTransactionDetails().getExternalResourceUrl();
        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            return "Erro ao gerar o boleto: " + e.getMessage();
        }
    }

    // Obter o Token do Cartão
    @PostMapping("/card-token")
    public ResponseEntity<String> getCardToken(@RequestBody CardTokenRequest cardTokenRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<CardTokenRequest> request = new HttpEntity<>(cardTokenRequest, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mercadopago.com/v1/card_tokens",
                    request,
                    String.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao obter token do cartão: " + e.getMessage());
        }
    }

}

// Classe de requisição para o token do cartão
class CardTokenRequest {
    private String cardNumber;
    private String securityCode;
    private int expirationMonth;
    private int expirationYear;
    private Cardholder cardholder;

    // Getters e Setters
    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public int getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(int expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public int getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(int expirationYear) {
        this.expirationYear = expirationYear;
    }

    public Cardholder getCardholder() {
        return cardholder;
    }

    public void setCardholder(Cardholder cardholder) {
        this.cardholder = cardholder;
    }

    static class Cardholder {
        private String name;
        private Identification identification;

        // Getters e Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Identification getIdentification() {
            return identification;
        }

        public void setIdentification(Identification identification) {
            this.identification = identification;
        }

        static class Identification {
            private String type;
            private String number;

            // Getters e Setters
            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }
        }
    }
}
