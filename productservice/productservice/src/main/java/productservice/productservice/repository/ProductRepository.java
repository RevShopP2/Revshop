package productservice.productservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import productservice.productservice.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom search method to search by product name or category name
    List<Product> findByProductNameContainingIgnoreCaseOrCategory_CategoryNameContainingIgnoreCase(String productName, String categoryName);

    // Corrected method to find products by category ID
    List<Product> findByCategory_CategoryId(Long categoryId);

    @Query(value = "SELECT * FROM product WHERE seller_id = :sellerId", nativeQuery = true)
    List<Product> findProductsBySellerId(@Param("sellerId") Long sellerId);

    @Modifying
    @Query(value = "UPDATE product SET product_name = :productName, product_description = :productDescription, price = :price, discount_price = :discountPrice, quantity = :quantity, image = :image WHERE product_id = :productId", nativeQuery = true)
    void updateProduct(
            @Param("productId") Long productId,
            @Param("productName") String productName,
            @Param("productDescription") String productDescription,
            @Param("price") double price,
            @Param("discountPrice") double discountPrice,
            @Param("quantity") int quantity,
            @Param("image") String image
    );
    @Query("SELECT p FROM Product p WHERE " + "(UPPER(p.productName) LIKE UPPER(CONCAT('%', :keyword, '%')) " + "OR UPPER(p.category.categoryName) LIKE UPPER(CONCAT('%', :keyword, '%'))) " + "AND p.price BETWEEN :minPrice AND :maxPrice " + "ORDER BY p.price ASC")
	List<Product> filterAndSortProductsAsc(String keyword, double minPrice, double maxPrice);

	@Query("SELECT p FROM Product p WHERE " + "(UPPER(p.productName) LIKE UPPER(CONCAT('%', :keyword, '%')) " +  "OR UPPER(p.category.categoryName) LIKE UPPER(CONCAT('%', :keyword, '%'))) " + "AND p.price BETWEEN :minPrice AND :maxPrice " + "ORDER BY p.price DESC")
	    List<Product> filterAndSortProductsDesc(String keyword, double minPrice, double maxPrice);
}