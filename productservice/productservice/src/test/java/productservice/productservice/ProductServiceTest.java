package productservice.productservice;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productservice.productservice.model.Product;
import productservice.productservice.repository.ProductRepository;
import productservice.productservice.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        // Arrange
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setProductId(1L);
        products.add(product1);

        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(1, result.size());
        assertEquals(product1, result.get(0));
    }

    @Test
    public void testSearchProducts_Found() {
        // Arrange
        String keyword = "Test Product";
        Product product = new Product();
        product.setProductName("Test Product");
        List<Product> products = new ArrayList<>();
        products.add(product);
        
        when(productRepository.findByProductNameContainingIgnoreCaseOrCategory_CategoryNameContainingIgnoreCase(keyword, keyword))
            .thenReturn(products);

        // Act
        List<Product> result = productService.searchProducts(keyword);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    public void testGetProductById_ProductExists() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.getProductById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
    }

    @Test
    public void testGetProductById_ProductDoesNotExist() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProductById(productId);
        });
        assertEquals("Product not found with id: " + productId, exception.getMessage());
    }

    @Test
    public void testSave_Product() {
        // Arrange
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("New Product");

        // Act
        productService.save(product);

        // Assert
        verify(productRepository).save(product);
    }

    @Test
    public void testDeleteProductById() {
        // Arrange
        Long productId = 1L;

        // Act
        productService.deleteProductById(productId);

        // Assert
        verify(productRepository).deleteById(productId);
    }

    @Test
    public void testUpdateProduct() {
        // Arrange
        Long productId = 1L;
        Product existingProduct = new Product();
        existingProduct.setProductId(productId);
        existingProduct.setProductName("Existing Product");

        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Product");
        updatedProduct.setProductDescription("Updated Description");
        updatedProduct.setPrice(100.0);
        updatedProduct.setDiscountPrice(90.0);
        updatedProduct.setQuantity(10);
        updatedProduct.setImage("updated_image.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Act
        productService.updateProduct(productId, updatedProduct);

        // Assert
        verify(productRepository).updateProduct(
            eq(productId),
            eq(updatedProduct.getProductName()),
            eq(updatedProduct.getProductDescription()),
            eq(updatedProduct.getPrice()),
            eq(updatedProduct.getDiscountPrice()),
            eq(updatedProduct.getQuantity()),
            eq(updatedProduct.getImage())
        );
    }

    @Test
    public void testSearchAndFilterProducts_Asc() {
        // Arrange
        String keyword = "Test";
        double minPrice = 10.0;
        double maxPrice = 100.0;
        String sortOrder = "asc";
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductName("Test Product");
        products.add(product);

        when(productRepository.filterAndSortProductsAsc(keyword, minPrice, maxPrice)).thenReturn(products);

        // Act
        List<Product> result = productService.searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    public void testSearchAndFilterProducts_Desc() {
        // Arrange
        String keyword = "Test";
        double minPrice = 10.0;
        double maxPrice = 100.0;
        String sortOrder = "desc";
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductName("Test Product");
        products.add(product);

        when(productRepository.filterAndSortProductsDesc(keyword, minPrice, maxPrice)).thenReturn(products);

        // Act
        List<Product> result = productService.searchAndFilterProducts(keyword, minPrice, maxPrice, sortOrder);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }
    
    
    @Test
    public void testFindById_ProductExists() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        product.setProductId(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Product result = productService.findById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
    }

    @Test
    public void testFindById_ProductDoesNotExist() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            productService.findById(productId);
        });
    }

    @Test
    public void testGetProductsByCategoryId() {
        // Arrange
        Long categoryId = 1L;
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1L);
        products.add(product);
        
        when(productRepository.findByCategory_CategoryId(categoryId)).thenReturn(products);

        // Act
        List<Product> result = productService.getProductsByCategoryId(categoryId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

    @Test
    public void testGetProductsBySellerId() {
        // Arrange
        Long sellerId = 1L;
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId(1L);
        products.add(product);
        
        when(productRepository.findProductsBySellerId(sellerId)).thenReturn(products);

        // Act
        List<Product> result = productService.getProductsBySellerId(sellerId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
    }

}

