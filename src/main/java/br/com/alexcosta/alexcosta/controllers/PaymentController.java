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
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${mercado.pago.access.token}")
    private String accessToken;

    // Processar Pagamento com Cartão de Crédito/Débito
    @PostMapping("/credit-card")
    public ResponseEntity<Map<String, Object>> processCreditCardPayment(@RequestBody PaymentRequest request) {
        try {
            // Validação dos campos obrigatórios
            if (request.getTransactionAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "transactionAmount é obrigatório."));
            }
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "token é obrigatório."));
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

            // Retorna uma resposta JSON
            return ResponseEntity.ok(Map.of(
                    "message", "Pagamento realizado com sucesso!",
                    "paymentId", payment.getId()
            ));
        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
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
    public ResponseEntity<String> getCardToken(@RequestBody PaymentRequest cardTokenRequest) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<PaymentRequest> request = new HttpEntity<>(cardTokenRequest, headers);

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

