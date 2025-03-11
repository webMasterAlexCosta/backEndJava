package br.com.alexcosta.alexcosta.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PedidoRequest {
    @NotEmpty(message = "A lista de itens não pode ser vazia.")
    private List<PedidoItemDTO> items;

    public List<PedidoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PedidoItemDTO> items) {
        this.items = items;
    }
}
