/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unipar.sistema.vendas.service;

import br.unipar.sistema.vendas.domain.CarrinhoDeCompras;
import br.unipar.sistema.vendas.domain.Item;
import br.unipar.sistema.vendas.domain.Produto;

import java.util.ArrayList;

/**
 *
 * @author Marcelo
 */
public class VendaService {
    private DescontoService descontoService;
    private PagamentoService pagamentoService;
    private NotaFiscalService notaFiscalService;

    public VendaService(DescontoService descontoService, PagamentoService pagamentoService, NotaFiscalService notaFiscalService) {
        this.descontoService = descontoService;
        this.pagamentoService = pagamentoService;
        this.notaFiscalService = notaFiscalService;
    }
    
    boolean realizarVenda(CarrinhoDeCompras carrinho){
        double total = carrinho.calcularTotal();
        double totalComDesconto = descontoService.aplicarDesconto(total);
        
        for(Item item : carrinho.getItens()){
            if(item.getProduto().getEstoque() < item.getQuantidade()){
                return false;
            }
        };

        boolean pagamentoAprovado;

        try {
            pagamentoAprovado = pagamentoService.processarPagamento(totalComDesconto);
        }catch (RuntimeException ex) {
            return false;
        }


        if(pagamentoAprovado){
            ArrayList<Produto> produtos = new ArrayList<>();

            carrinho.getItens().forEach(i ->{
                i.getProduto().reduzirEstoque(i.getQuantidade());
                produtos.add(i.getProduto());
            });

            notaFiscalService.emitirNota(totalComDesconto, produtos);

            return true;
        }

        return false;
    }
}
