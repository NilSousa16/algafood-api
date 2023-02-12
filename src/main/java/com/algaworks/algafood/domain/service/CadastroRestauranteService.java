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
import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;

@Service
public class CadastroRestauranteService {

	@Autowired
	RestauranteRepository restauranteRepository;

	@Autowired
	CozinhaRepository cozinhaRepository;

	public Restaurante salvar(Restaurante restaurante) {
		Long cozinhaId = restaurante.getCozinha().getId();
		Cozinha cozinha = cozinhaRepository.buscar(cozinhaId);

		if (cozinha == null) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de cozinha com código %d", cozinhaId));
		}

		restaurante.setCozinha(cozinha);

		return restauranteRepository.salvar(restaurante);
	}

	public Restaurante buscar(Long id) {
		return restauranteRepository.buscar(id);
	}

	public List<Restaurante> listar() {
		return restauranteRepository.listar();
	}

	public Restaurante atualizar(Long restauranteId, Restaurante restaurante) {
		Restaurante restauranteAtual = restauranteRepository.buscar(restauranteId);
		Cozinha cozinha = cozinhaRepository.buscar(restaurante.getCozinha().getId());

		if (restauranteAtual == null) {
			throw new RecursoNaoEncontradaException(
					String.format("Não existe cadastro de restaurante com código %d", restauranteId));
		}

		if (cozinha == null) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de cozinha com código %d",
							restaurante.getCozinha().getId()));
		}

		restaurante.setCozinha(cozinha);

		BeanUtils.copyProperties(restaurante, restauranteAtual, "id");

		restauranteAtual = restauranteRepository.salvar(restauranteAtual);

		return restauranteAtual;
	}
	
	public void excluir(Long restauranteId) {
		try {
			restauranteRepository.remover(restauranteId);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de restaurante com o código %d", restauranteId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Restaurante de código %d não pode ser removida, pois está em uso", restauranteId));
		}
	}
}
