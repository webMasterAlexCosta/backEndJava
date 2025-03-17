package br.com.alexcosta.alexcosta.services;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.exception.StripeException;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public String createPaymentIntent(double amount) throws StripeException {
        // Configura a chave secreta do Stripe
        Stripe.apiKey = stripeApiKey;

        // Cria o PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(
                new PaymentIntentCreateParams.Builder()
                        .setAmount((long) amount)  // Valor em centavos
                        .setCurrency("brl")        // Defina a moeda desejada
                        .build()
        );

        // Retorna o clientSecret do PaymentIntent
        return paymentIntent.getClientSecret();
    }
}
