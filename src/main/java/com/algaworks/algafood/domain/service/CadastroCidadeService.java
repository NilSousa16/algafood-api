package com.algaworks.algafood.domain.service;

import java.util.List;
import java.util.Optional;

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
		
		Estado estado = estadoRepository.findById(estadoId).orElseThrow(() -> new EntidadeNaoEncontradaException(
				String.format("Não existe cadastro de estado com código %d", estadoId)));

		cidade.setEstado(estado);
		
		return cidadeRepository.save(cidade); 
	}

	public Optional<Cidade> buscar(Long id) {
		return cidadeRepository.findById(id);
	}

	public List<Cidade> listar() {
		return cidadeRepository.findAll();
	}

	public Cidade atualizar(Long cidadeId, Cidade cidade) {
		Optional<Cidade> cidadeAtual = cidadeRepository.findById(cidadeId);
		Optional<Estado> estado = estadoRepository.findById(cidade.getEstado().getId());
		
		if (!cidadeAtual.isPresent()) {
			throw new RecursoNaoEncontradaException(
					String.format("Não existe cadastro de cidade com código %d", cidadeId));
		}

		if (!estado.isPresent()) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de estado com código %d",
							cidade.getEstado().getId()));
		}
		
		cidade.setEstado(estado.get());
		
		BeanUtils.copyProperties(cidade, cidadeAtual.get(), "id");
		
		Cidade cidadeSalva = cidadeRepository.save(cidadeAtual.get());
		
		return cidadeSalva;
	}
	
	public void excluir(Long cidadeId) {
		try {
			cidadeRepository.deleteById(cidadeId);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de cidade com o código %d", cidadeId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Cidade de código %d não pode ser removida, pois está em uso", cidadeId));
		}
	}

}
