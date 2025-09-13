/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package br.unipar.sistema.vendas.service;

import br.unipar.sistema.vendas.domain.CarrinhoDeCompras;
import br.unipar.sistema.vendas.domain.Item;
import br.unipar.sistema.vendas.domain.Produto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Marcelo
 */
public class VendaServiceTest {
    
    private DescontoService descontoService;
    private PagamentoService pagamentoService;
    private NotaFiscalService notaFiscalService;

    private CarrinhoDeCompras carrinhoDeCompras;
    private VendaService vendaService;
    
    @BeforeEach
    public void setUp() {
        descontoService = Mockito.mock(DescontoService.class);
        pagamentoService = Mockito.mock(PagamentoService.class);
        notaFiscalService = Mockito.mock(NotaFiscalService.class);

        carrinhoDeCompras = new CarrinhoDeCompras();
        vendaService = new VendaService(descontoService, pagamentoService, notaFiscalService);
    }

    @Test
    public void testAdicionarProduto() {
        Produto p1 = new Produto(1, "test1", 208.77, 10);

        carrinhoDeCompras.adicionarProduto(p1, 5);
        assertEquals(1, carrinhoDeCompras.getItens().size());

        Item itemCarrinho = carrinhoDeCompras.getItens().get(0);
        assertEquals(p1.getId(), itemCarrinho.getProduto().getId());
        assertEquals(p1.getPreco(), itemCarrinho.getProduto().getPreco());
        assertEquals(p1.getNome(), itemCarrinho.getProduto().getNome());
        assertEquals(p1.getEstoque(), itemCarrinho.getProduto().getEstoque());

        assertEquals(5, itemCarrinho.getQuantidade());

    }

    @Test
    public void testRemoverProduto() {
        carrinhoDeCompras.getItens().clear();

        Produto p1 = new Produto(1, "test1", 200.0, 10);
        Produto p2 = new Produto(2, "test2", 200.0, 10);

        carrinhoDeCompras.adicionarProduto(p1, 5);
        carrinhoDeCompras.adicionarProduto(p2, 9);

        carrinhoDeCompras.removerProduto(p1.getId());
        assertEquals(1, carrinhoDeCompras.getItens().size());

        carrinhoDeCompras.removerProduto(p2.getId());
        assertEquals(0, carrinhoDeCompras.getItens().size());
    }

    @Test
    public void testCalcularTotal() {
        carrinhoDeCompras.getItens().clear();

        Produto p1 = new Produto(1, "test1", 208.0, 10);
        Produto p2 = new Produto(2, "test2", 200.0, 10);
        Produto p3 = new Produto(1, "test1", 208.0, 10);

        carrinhoDeCompras.adicionarProduto(p1, 5);
        carrinhoDeCompras.adicionarProduto(p2, 9);
        carrinhoDeCompras.adicionarProduto(p3, 1);

        double totalCarrinho = carrinhoDeCompras.calcularTotal();

        assertEquals(3048.0, totalCarrinho);
    }

    @Test
    public void testDesconto10(){
        carrinhoDeCompras.getItens().clear();
        Produto p1 = new Produto(1, "Celular", 100.00, 10);
        carrinhoDeCompras.adicionarProduto(p1,1);

        when(descontoService.aplicarDesconto(100.0))
                .thenReturn(90.0);
        when(pagamentoService.processarPagamento(90.0))
                .thenReturn(true);

        boolean sucesso =  vendaService.realizarVenda(carrinhoDeCompras);

        assertTrue(sucesso);
        verify(pagamentoService).processarPagamento(90);
    }

    @Test
    public void testDesconto0(){
        carrinhoDeCompras.getItens().clear();
        Produto p1 = new Produto(1, "Celular", 100.00, 10);
        carrinhoDeCompras.adicionarProduto(p1, 1);

        when(descontoService.aplicarDesconto(100.0))
                .thenReturn(100.0);
        when(pagamentoService.processarPagamento(100))
            .thenReturn(true);

        boolean sucesso = vendaService.realizarVenda(carrinhoDeCompras);

        assertTrue(sucesso);
        verify(pagamentoService).processarPagamento(100);
    }

    @Test
    public void testEstoqueInsuficiente(){
        carrinhoDeCompras.getItens().clear();
        Produto p1 = new Produto(1, "Celular", 100.00, 1);
        carrinhoDeCompras.adicionarProduto(p1, 2);

        when(descontoService.aplicarDesconto(200))
                .thenReturn(180.0);

        boolean sucesso = vendaService.realizarVenda(carrinhoDeCompras);

        assertFalse(sucesso);
        verify(pagamentoService, never()).processarPagamento(180);
    }

    @Test
    public void testProcessarPagamentoSucesso() {
        carrinhoDeCompras.getItens().clear();
        Produto p1 = new Produto(1, "Celular", 100.00, 10);
        carrinhoDeCompras.adicionarProduto(p1, 2);

        when(descontoService.aplicarDesconto(200))
                .thenReturn(180.0);
        when(pagamentoService.processarPagamento(180.0))
                .thenReturn(true);

        boolean sucesso = vendaService.realizarVenda(carrinhoDeCompras);
        assertTrue(sucesso);
        assertEquals(8 ,carrinhoDeCompras.getItens().get(0).getProduto().getEstoque());

        ArrayList<Produto> produtos = new ArrayList<>();
        produtos.add(p1);
        verify(notaFiscalService).emitirNota(180.0, produtos);

    }

    @Test
    public void testProcessarPagamentoFalho() {
        carrinhoDeCompras.getItens().clear();
        Produto p1 = new Produto(1, "Celular", 100.00, 10);
        carrinhoDeCompras.adicionarProduto(p1, 2);

        when(descontoService.aplicarDesconto(200))
                .thenReturn(180.0);
        when(pagamentoService.processarPagamento(180.0))
                .thenReturn(false);

        boolean sucesso = vendaService.realizarVenda(carrinhoDeCompras);
        assertFalse(sucesso);

        ArrayList<Produto> produtos = new ArrayList<>();
        produtos.add(p1);
        verify(notaFiscalService, never()).emitirNota(180.0, produtos);

    }

    @Test
    public void testProcessarPagamentoExcecao() {
        carrinhoDeCompras.getItens().clear();
        Produto p1 = new Produto(1, "Celular", 100.00, 10);
        carrinhoDeCompras.adicionarProduto(p1, 2);

        when(descontoService.aplicarDesconto(200))
                .thenReturn(180.0);
        when(pagamentoService.processarPagamento(180.0))
                .thenThrow(new RuntimeException());

        boolean sucesso = vendaService.realizarVenda(carrinhoDeCompras);
        assertFalse(sucesso);
        assertNotEquals(8, p1.getEstoque());

        ArrayList<Produto> produtos = new ArrayList<>();
        produtos.add(p1);
        verify(notaFiscalService, never()).emitirNota(180.0, produtos);

    }
    
}
