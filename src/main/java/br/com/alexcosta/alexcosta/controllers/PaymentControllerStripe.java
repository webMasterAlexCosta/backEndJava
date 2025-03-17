package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.services.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.exception.StripeException;

import java.util.Map;

@RestController
public class PaymentControllerStripe {

    @Autowired
    private StripeService stripeService;

    @GetMapping("/api/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestParam double amount) {
        try {
            if (amount <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Amount must be greater than 0.");
            }
            // Converte o valor para centavos (multiplica por 100)
            int amountInCents = (int) (amount * 100);

            // Chama o serviço Stripe para criar o PaymentIntent
            String clientSecret = stripeService.createPaymentIntent(amountInCents);

            // Retorna um JSON contendo o clientSecret
            return ResponseEntity.ok(Map.of("clientSecret", clientSecret));
        } catch (StripeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment intent: " + e.getMessage());
        }
    }


}
