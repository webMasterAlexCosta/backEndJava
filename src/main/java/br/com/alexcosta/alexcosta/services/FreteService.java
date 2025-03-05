package br.com.alexcosta.alexcosta.services;


import br.com.alexcosta.alexcosta.dto.FreteDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FreteService {
    public double calcularFrete(FreteDTO pedido) {
        double taxaBase = 5.0;

        double taxaPorUnidade = 1.0;

        return (taxaBase + (pedido.getQuantidade() * taxaPorUnidade));
    }

    public double obterAjustePorEstado(String estado, String tipoFrete, int quantidade) {

        double valorBase;
        double adicionalPorItem;
        if(tipoFrete.equals("pac")){
            valorBase=10.0;
            adicionalPorItem=1.0;
        }else{
            valorBase=15.0;
            adicionalPorItem=1.0;
        }

        Map<String, Double> ajustes = new HashMap<>();
        ajustes.put("SP", 3.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("RJ", 2.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("ES", 3.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("MG", 3.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("RS", 5.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("SC", 4.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("PR", 5.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("MS", 6.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("MT", 6.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("GO", 6.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("DF", 7.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("AC", 8.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("AP", 8.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("AM", 8.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("PA", 8.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("RO", 8.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("RR", 8.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("TO", 4.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("SE", 6.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("RN", 7.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("PI", 7.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("PE", 7.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("PB", 7.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("MA", 7.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("CE", 4.0+(valorBase+(adicionalPorItem*quantidade)));
        ajustes.put("BA", 4.0+(valorBase+(adicionalPorItem*quantidade)));
        return ajustes.getOrDefault(estado, 0.0);
    }
}
