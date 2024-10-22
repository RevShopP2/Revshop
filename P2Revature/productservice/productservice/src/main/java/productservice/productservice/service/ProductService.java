package productservice.productservice.service;


import jakarta.transaction.Transactional;
import productservice.productservice.model.Category;
import productservice.productservice.model.Product;
import productservice.productservice.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Fetch all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Search for products by product name or category name
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCaseOrCategory_CategoryNameContainingIgnoreCase(keyword, keyword);
    }

    // Get a product by its ID, handling the case where it might not be found
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            return productOptional.get();
        } else {
            throw new RuntimeException("Product not found with id: " + productId);
            // You can also define a custom exception instead of RuntimeException
        }
    }

    // Get products by category ID
    public List<Product> getProductsByCategoryId(Long long1) {
        return productRepository.findByCategory_CategoryId(long1);
    }

    // Save the product to the database
    public void save(Product product) {
        productRepository.save(product);
    }

	public List<Product> getProductsBySellerId(Long sellerIdLong) {
		return productRepository.findProductsBySellerId(sellerIdLong);
	}

	public void deleteProductById(Long productId) {
		productRepository.deleteById(productId);
	}

	@Transactional
    public void updateProduct(Long productId, Product updatedProduct) {
        productRepository.updateProduct(
            productId,
            updatedProduct.getProductName(),
            updatedProduct.getProductDescription(),
            updatedProduct.getPrice(),
            updatedProduct.getDiscountPrice(),
            updatedProduct.getQuantity(),
            updatedProduct.getImage()
        );
    }

	public Product findById(Long productId) {
		// TODO Auto-generated method stub
		return productRepository.findById(productId).get();
	}
	public List<Product> searchAndFilterProducts(String keyword, double minPrice, double maxPrice, String sortOrder) {

        if (sortOrder.equalsIgnoreCase("asc")) {
            return productRepository.filterAndSortProductsAsc(keyword, minPrice, maxPrice);
        } 
        return productRepository.filterAndSortProductsDesc(keyword, minPrice, maxPrice);
}
}