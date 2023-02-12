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
import com.algaworks.algafood.domain.model.Cidade;
import com.algaworks.algafood.domain.model.Estado;
import com.algaworks.algafood.domain.repository.CidadeRepository;
import com.algaworks.algafood.domain.repository.EstadoRepository;

@Service
public class CadastroCidadeService {

	@Autowired
	CidadeRepository cidadeRepository;

	@Autowired
	EstadoRepository estadoRepository;

	public Cidade salvar(Cidade cidade) {
		Long estadoId = cidade.getEstado().getId();
		Estado estado = estadoRepository.buscar(estadoId);

		if (estado == null) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de estado com código %d", estadoId));
		}

		cidade.setEstado(estado);

		return cidadeRepository.salvar(cidade);
	}

	public Cidade buscar(Long id) {
		return cidadeRepository.buscar(id);
	}

	public List<Cidade> listar() {
		return cidadeRepository.listar();
	}

	public Cidade atualizar(Long cidadeId, Cidade cidade) {
		Cidade cidadeAtual = cidadeRepository.buscar(cidadeId);
		Estado estado = estadoRepository.buscar(cidade.getEstado().getId());
		
		if (cidadeAtual == null) {
			throw new RecursoNaoEncontradaException(
					String.format("Não existe cadastro de cidade com código %d", cidadeId));
		}

		if (estado == null) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de estado com código %d",
							cidade.getEstado().getId()));
		}
		
		cidade.setEstado(estado);
		
		BeanUtils.copyProperties(cidade, cidadeAtual, "id");
		
		cidadeAtual = cidadeRepository.salvar(cidadeAtual);
		
		return cidadeAtual;
	}
	
	public void excluir(Long cidadeId) {
		try {
			cidadeRepository.remover(cidadeId);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de cidade com o código %d", cidadeId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Cidade de código %d não pode ser removida, pois está em uso", cidadeId));
		}
	}

}
