/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unipar.sistema.vendas.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Marcelo
 */
public class CarrinhoDeCompras {
    private List<Item> itens;
    
    public CarrinhoDeCompras(){
        itens = new ArrayList<>();
    }
    
    public void adicionarProduto(Produto produto, int quantidade) {
        for (Item i : itens) {
            if (i.getProduto().getId() == produto.getId()) {
                i.setQuantidade(quantidade + i.getQuantidade());
                return;
            }
        }
        itens.add(new Item(produto, quantidade));
    }
    public void removerProduto(int produtoId) {
        for (Item i : itens) {
            if (i.getProduto().getId() == produtoId) {
                itens.remove(i);
                return;
            }
        }
    }
    public double calcularTotal(){
        double total = 0.0;
        
        for (Item iten : itens) {
          total += iten.getQuantidade() * iten.getProduto().getPreco();
        }
        
        return total;
    }

    public List<Item> getItens() {
        return itens;
    }
    
    
}
