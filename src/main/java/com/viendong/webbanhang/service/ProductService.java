package com.viendong.webbanhang.service;
import com.viendong.webbanhang.model.OrderDetail;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.repository.OrderDetailRepository;
import com.viendong.webbanhang.repository.ProductRepository;
import com.viendong.webbanhang.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
@Transactional

public class ProductService {

    private  ProductRepository productRepository;
    private final UserRepository userRepository; // Add UserRepository

    private final OrderDetailRepository orderDetailRepository;


    public ProductService (ProductRepository productRepository, OrderDetailRepository orderDetailRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> getProductsByBrand(Long brandId) {
        return productRepository.findByBrandId(brandId);
    }

    public int calculateTotalQuantitySold() {
        return orderDetailRepository.findAll().stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }

    public long countProducts() {
        return productRepository.count();
    }

    public Page<Product> searchProductsByName(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable);
    }
    // Phân trang sản phẩm
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    public Page<Product> filterProducts(List<String> categories, List<String> priceRanges, List<String> brands, Pageable pageable) {
        return productRepository.findByCategoryAndPriceRangeAndBrand(categories, priceRanges, brands, pageable);
    }
    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    // Retrieve a product by its id
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    // Add a new product to the database
    public Product addProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new IllegalStateException("Product with name '" + product.getName() + "' already exists.");
        }
        return productRepository.save(product);
    }
    // Update an existing product
    public Product updateProduct(@NonNull Product product) {
        Product existingProduct = productRepository.findById(product.getId()) .orElseThrow(() -> new IllegalStateException("Product with ID " + product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setImage(product.getImage());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setBrand(product.getBrand());
        return productRepository.save(existingProduct);
    }
    // Delete a product by its id
    public void deleteProductById(Long id) {
        if(!productRepository.existsById(id)){
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }
    Page<Product> getAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo -1,2);
        return this.productRepository.findAll(pageable);
    }

    public OrderDetailRepository getOrderDetailRepository() {
        return orderDetailRepository;
    }

    public double calculateTotalRevenue() {
        return productRepository.findAll().stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }
    public long countAllProducts() {
        return productRepository.count(); // Sử dụng phương thức count() đã có trong JpaRepository
    }

    public long countFavoriteProducts(Long userId) {
        // Find the user by ID
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            // Assuming the user has a 'favoriteProducts' set containing all their favorite products
            return user.get().getFavoriteProducts().size();
        }
        return 0;
    }
}
