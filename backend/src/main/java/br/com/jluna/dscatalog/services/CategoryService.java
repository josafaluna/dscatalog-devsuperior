package br.com.jluna.dscatalog.services;

import br.com.jluna.dscatalog.dto.CategoryDTO;
import br.com.jluna.dscatalog.entities.Category;
import br.com.jluna.dscatalog.repositories.CategoryRepository;
import br.com.jluna.dscatalog.services.exceptions.CategoryNotFoundException;
import br.com.jluna.dscatalog.services.exceptions.DatabaseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * código antigo, retorna todas as categorias
     */
//    @Transactional(readOnly = true)
//    public List<CategoryDTO> findAll() {
//
//        List<Category> list = repository.findAll();
//        return list.stream().map(CategoryDTO::new).collect(Collectors.toList());
//        //return list.stream().map(x -> new CategoryDTO(x)).collect(Collectors.toList());
//    }
    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
        Page<Category> list = repository.findAll(pageRequest);
        return list.map(CategoryDTO::new);
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

    public void delete(Long id) {

        try {

            repository.deleteById(id);

        } catch (EmptyResultDataAccessException e) {
            throw new CategoryNotFoundException("ID NOT FOUND " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }

    }

}
