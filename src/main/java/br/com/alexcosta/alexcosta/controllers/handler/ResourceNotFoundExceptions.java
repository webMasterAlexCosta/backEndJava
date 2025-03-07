package br.com.alexcosta.alexcosta.controllers.handler;

public class ResourceNotFoundExceptions extends RuntimeException {
    public ResourceNotFoundExceptions(String msg){
        super(msg);
    }

}
