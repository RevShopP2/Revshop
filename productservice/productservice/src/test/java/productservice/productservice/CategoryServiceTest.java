package productservice.productservice;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productservice.productservice.model.Category;
import productservice.productservice.repository.CategoryRepository;
import productservice.productservice.service.CategoryService;

import java.util.Optional;

public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    public CategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCategoryById_CategoryExists() {
        // Arrange
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setCategoryName("Test Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
        assertEquals("Test Category", result.getCategoryName());
    }


    

    @Test
    public void testSave_Category() {
        // Arrange
        Category category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("New Category");

        // Act
        categoryService.save(category);

        // Assert
        verify(categoryRepository).save(category);
    }
    
    @Test
    public void testGetCategoryById_CategoryDoesNotExist() {
        // Arrange
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });

        assertEquals("Category not found with id: " + categoryId, exception.getMessage());
    }

}

