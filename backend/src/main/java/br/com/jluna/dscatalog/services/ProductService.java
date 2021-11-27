package br.com.jluna.dscatalog.services;

import br.com.jluna.dscatalog.dto.ProductDTO;
import br.com.jluna.dscatalog.entities.Product;
import br.com.jluna.dscatalog.repositories.CategoryRepository;
import br.com.jluna.dscatalog.repositories.ProductRepository;
import br.com.jluna.dscatalog.services.exceptions.DatabaseException;
import br.com.jluna.dscatalog.services.exceptions.DsCatalogNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = repository.findById(id).orElseThrow(() -> new DsCatalogNotFoundException("Product not found"));
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        var entity = new Product();
        copyDtoToEntity(dto, entity);

        entity = repository.save(entity);

        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {

        try {

            var entity = repository.getOne(id);
            copyDtoToEntity(dto, entity);

            entity = repository.save(entity);

            return new ProductDTO(entity);

        } catch (EntityNotFoundException e) {
            throw new DsCatalogNotFoundException("id NOT FOUND " + id);
        }

    }

    public void delete(Long id) {

        try {

            repository.deleteById(id);

        } catch (EmptyResultDataAccessException e) {
            throw new DsCatalogNotFoundException("ID NOT FOUND " + id);

        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");

        }

    }

    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.setDate(dto.getDate());

        // limpando a lista
        entity.getCategories().clear();
        dto.getCategories().forEach(p -> entity.getCategories().add(categoryRepository.getOne(p.getId())));

//        for (CategoryDTO catDTO : dto.getCategories()) {
//            Category category = categoryRepository.getOne(catDTO.getId());
//            entity.getCategories().add(category);
//        }

    }

}