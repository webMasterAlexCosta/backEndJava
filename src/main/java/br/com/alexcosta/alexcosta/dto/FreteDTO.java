package br.com.alexcosta.alexcosta.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter@NonNull@AllArgsConstructor
public class FreteDTO {
    private int quantidade;
    private double valorTotal;

}

