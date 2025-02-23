package br.com.aplrm.aplrm.dto;

import java.util.List;

public class PedidoRequest {
    private List<PedidoItemDTO> items;

    public List<PedidoItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PedidoItemDTO> items) {
        this.items = items;
    }
}
