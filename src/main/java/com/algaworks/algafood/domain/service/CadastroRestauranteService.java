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
		// Optional - return tipo 1 com lançamento da exceção
		Cozinha cozinha = cozinhaRepository.findById(cozinhaId).orElseThrow(() -> new EntidadeNaoEncontradaException(
				String.format("Não existe cadastro de cozinha com código %d", cozinhaId)));

		restaurante.setCozinha(cozinha);

		return restauranteRepository.save(restaurante);
	}

	public Optional<Restaurante> buscar(Long id) {
		return restauranteRepository.findById(id);
	}

	public List<Restaurante> listar() {
		return restauranteRepository.findAll();
	}

	public Restaurante atualizar(Long restauranteId, Restaurante restaurante) {
		Optional<Restaurante> restauranteAtual = restauranteRepository.findById(restauranteId);
		// Optional - return tipo 2
		Optional<Cozinha> cozinha = cozinhaRepository.findById(restaurante.getCozinha().getId());

		if (!restauranteAtual.isPresent()) {
			throw new RecursoNaoEncontradaException(
					String.format("Não existe cadastro de restaurante com código %d", restauranteId));
		}

		if (cozinha.isEmpty()) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe cadastro de cozinha com código %d", restaurante.getCozinha().getId()));
		}

		restaurante.setCozinha(cozinha.get());

		BeanUtils.copyProperties(restaurante, restauranteAtual.get(), "id");

		Restaurante restauranteSalvo = restauranteRepository.save(restauranteAtual.get());

		return restauranteSalvo;
	}

	public void excluir(Long restauranteId) {
		try {
			restauranteRepository.deleteById(restauranteId);
		} catch (EmptyResultDataAccessException e) {
			throw new EntidadeNaoEncontradaException(
					String.format("Não existe um cadastro de restaurante com o código %d", restauranteId));
		} catch (DataIntegrityViolationException e) {
			throw new EntidadeEmUsoException(
					String.format("Restaurante de código %d não pode ser removida, pois está em uso", restauranteId));
		}
	}
}
