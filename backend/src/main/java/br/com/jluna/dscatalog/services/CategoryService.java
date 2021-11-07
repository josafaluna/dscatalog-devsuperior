package br.com.jluna.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.jluna.dscatalog.dto.CategoryDTO;
import br.com.jluna.dscatalog.entities.Category;
import br.com.jluna.dscatalog.repositories.CategoryRepository;
import br.com.jluna.dscatalog.services.exceptions.CategoryNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {

		List<Category> list = repository.findAll();
		return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {

		// Category entity = repository.findById(id).get();
		Category category = repository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found."));

		return new CategoryDTO(category);
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {

		Category entity = new Category();
		entity.setName(dto.getName());

		entity = repository.save(entity);

		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {

		try {

			var entity = repository.getOne(id);
			entity.setName(dto.getName());

			entity = repository.save(entity);

			return new CategoryDTO(entity);

		} catch (EntityNotFoundException e) {
			throw new CategoryNotFoundException("id NOT FOUND " + id);
		}

	}

}
