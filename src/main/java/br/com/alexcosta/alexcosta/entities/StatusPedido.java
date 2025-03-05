package br.com.alexcosta.alexcosta.entities;

import java.io.Serializable;

public enum StatusPedido implements Serializable {
	esperandoPagamento,pago,enviado,entregue,cancelado
}
