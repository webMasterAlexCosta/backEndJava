package br.com.alexcosta.alexcosta.controllers;

import br.com.alexcosta.alexcosta.services.FreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/frete")
public class FreteController {

    @Autowired
    private FreteService freteService;

    @GetMapping
    public Map<String, Double> calcularFrete(
            @RequestParam String estadoDestino,
            @RequestParam int quantidade) {

        Map<String, Double> frete = new HashMap<>();
        double valorSedex = calcularFreteSedex(estadoDestino, quantidade);
        double valorPac = calcularFretePac(estadoDestino, quantidade);

        frete.put("freteSedex", valorSedex);
        frete.put("fretePac", valorPac);

        return frete;
    }
    private double calcularFreteSedex(String estado, int quantidade) {

         return freteService.obterAjustePorEstado(estado, "sedex",quantidade);
    }

    private double calcularFretePac(String estado, int quantidade) {


        return freteService.obterAjustePorEstado(estado, "pac",quantidade);
    }

}
