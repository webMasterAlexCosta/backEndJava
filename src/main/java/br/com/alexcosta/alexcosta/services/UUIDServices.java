package br.com.alexcosta.alexcosta.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UUIDServices {

    public UUID gerarUUId(){
        return UUID.randomUUID();
    }


}
