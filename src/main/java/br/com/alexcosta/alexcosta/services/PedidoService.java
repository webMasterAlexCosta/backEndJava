package br.com.alexcosta.alexcosta.services;

import br.com.alexcosta.alexcosta.dto.PedidoDTO;
import br.com.alexcosta.alexcosta.dto.PedidoItemDTO;
import br.com.alexcosta.alexcosta.dto.PedidoRequest;
import br.com.alexcosta.alexcosta.entities.*;
import br.com.alexcosta.alexcosta.repositories.PedidoItemRepository;
import br.com.alexcosta.alexcosta.repositories.PedidoRepository;
import br.com.alexcosta.alexcosta.repositories.ProdutoRepository;
import br.com.alexcosta.alexcosta.repositories.TamanhoRepository;
import br.com.alexcosta.alexcosta.controllers.handler.ResourceNotFoundExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoItemRepository pedidoItemRepository;

    @Autowired
    private TamanhoRepository tamanhoRepository;

    @Autowired
    private UUIDServices uuid;

    @Transactional(readOnly = true)
    public PedidoDTO findById(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptions("Recurso não encontrado"));
        return new PedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO insert(PedidoRequest pedidoRequest) {
        if (pedidoRequest == null || pedidoRequest.getItems() == null || pedidoRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("O pedido não pode ser vazio.");
        }

        Pedido pedido = new Pedido();
        pedido.setMomento(Instant.now());
        pedido.setStatus(StatusPedido.esperandoPagamento);

        User user = userService.authenticated();
        pedido.setCliente(user);
        pedido.setNumeroPedido("B&D"+uuid.gerarUUId().toString().substring(0,10));

        for (PedidoItemDTO itemDTO : pedidoRequest.getItems()) {
            if (itemDTO.getId() == null) {
                throw new IllegalArgumentException("ID do produto não pode ser nulo.");
            }

            Produto produto = produtoRepository.findById(itemDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundExceptions("Produto não encontrado"));

            Integer tamanhoId = itemDTO.getTamanho() ;

            if (tamanhoId == null) {
                tamanhoId=1;
                //throw new IllegalArgumentException("O tamanho do produto não pode ser nulo.");
            }

            PedidoItem item = new PedidoItem(pedido, produto, itemDTO.getQuantidade(), produto.getPreco(), tamanhoId);
            pedido.getItems().add(item);
        }

        pedidoRepository.save(pedido);
        return new PedidoDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> meusPedidos() {
        User user = userService.authenticated();
        List<Pedido> pedidos = pedidoRepository.findByCliente(user);
        return pedidos.stream()
                .map(PedidoDTO::new)
                .collect(Collectors.toList());
    }
}
