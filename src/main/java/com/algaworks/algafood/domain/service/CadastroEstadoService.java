package com.algaworks.algafood.domain.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.algafood.domain.exception.EntidadeEmUsoException;
import com.algaworks.algafood.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algafood.domain.exception.RecursoNaoEncontradaException;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.EstadoRepository;

@Service
public class CadastroEstadoService {

	@Autowired
	EstadoRepository estadoRepository;

	public Estado salvar(Estado estado) {
		return estadoRepository.salvar(estado);
	}

	public Estado buscar(Long estadoId) {
		return estadoRepository.buscar(estadoId);
	}

	public Estado atualizar(Long estadoId, Estado estado) {
		Estado estadoAtual = estadoRepository.buscar(estadoId);
		
		if (estadoAtual == null) {
			throw new RecursoNaoEncontradaException(
					String.format("Não existe cadastro de estado com código %d", estadoId));
		}
		
		BeanUtils.copyProperties(estado, estadoAtual, "id");
		
		estadoAtual = estadoRepository.salvar(estadoAtual);
		
		return estadoAtual;
	}

	public List<Estado> listar() {
		return estadoRepository.listar();
	}
	
	public void excluir(Long estadoId) {
		try {
			estadoRepository.remover(estadoId);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de estado com o código %d", estadoId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Estado de código %d não pode ser removida, pois está em uso", estadoId));
		}
	}

}
