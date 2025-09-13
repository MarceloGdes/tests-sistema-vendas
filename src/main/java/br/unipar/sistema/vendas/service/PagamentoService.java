/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unipar.sistema.vendas.service;

/**
 *
 * @author Marcelo
 */
public interface PagamentoService {
    boolean processarPagamento(double valor) throws RuntimeException;
}
