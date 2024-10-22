package productservice.productservice.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import productservice.productservice.model.Category;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Custom query to find a category by its name
    Category findByCategoryName(String categoryName);

}
