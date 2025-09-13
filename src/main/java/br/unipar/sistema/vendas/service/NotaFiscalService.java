/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.unipar.sistema.vendas.service;

import br.unipar.sistema.vendas.domain.Produto;
import java.util.List;

/**
 *
 * @author Marcelo
 */
public interface NotaFiscalService {
    void emitirNota(double valor, List<Produto> produtos);
}
