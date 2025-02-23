package br.com.aplrm.aplrm.entities;

import java.io.Serializable;

public enum StatusPedido implements Serializable {
	esperandoPagamento,pago,enviado,entregue,cancelado
}
