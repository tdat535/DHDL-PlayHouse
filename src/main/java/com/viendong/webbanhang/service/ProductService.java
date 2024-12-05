package com.viendong.webbanhang.service;

import com.viendong.webbanhang.model.OrderDetail;
import com.viendong.webbanhang.model.Product;
import com.viendong.webbanhang.model.User;
import com.viendong.webbanhang.repository.OrderDetailRepository;
import com.viendong.webbanhang.repository.ProductRepository;
import com.viendong.webbanhang.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.viendong.webbanhang.ProductNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, OrderDetailRepository orderDetailRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userRepository = userRepository;
    }


    public Optional<Product> findById(Long productId) {
        return productRepository.findById(productId);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public String saveImage(MultipartFile imageFile) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/images/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image file", e);
        }
    }

    // Lấy sản phẩm với phân trang
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable); // Find all products with pagination
    }

    public Page<Product> searchProducts(String keyword, List<String> categories, List<String> brands, Pageable pageable) {
        return productRepository.findByKeywordAndFilters(keyword, categories, brands, pageable);
    }

    public Page<Product> filterProducts(List<String> categories, List<String> brands, Pageable pageable) {
        return productRepository.findByFilters(categories, brands, pageable);
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

    // Get all products without pagination
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product, RedirectAttributes redirectAttributes) {
        if (productRepository.existsByName(product.getName())) {
            redirectAttributes.addFlashAttribute("error",
                    "Sản phẩm '" + product.getName() + "' đã tồn tại.");
            return null; // Không lưu sản phẩm nếu đã tồn tại
        }
        return productRepository.save(product);
    }

    public void updateProduct(Long id, Product product) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setBrand(product.getBrand());
        existingProduct.setImage(product.getImage());
        existingProduct.setDescription(product.getDescription());

        productRepository.save(existingProduct); // Save the updated product
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    public double calculateTotalRevenue() {
        return productRepository.findAll().stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }

    public long countAllProducts() {
        return productRepository.count();
    }

    public long countFavoriteProducts(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getFavoriteProducts().size();
        }
        return 0;
    }

    public boolean hasEnoughQuantity(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            int availableQuantity = productOpt.get().getQuantity();
            return availableQuantity >= quantity; // Kiểm tra có đủ số lượng không
        }
        return false; // Sản phẩm không tồn tại
    }

    public boolean isQuantityTooLow(Long productId, int minimumQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            int availableQuantity = productOpt.get().getQuantity();
            return availableQuantity <= minimumQuantity; // Kiểm tra nếu số lượng còn lại <= mức tối thiểu
        }
        return true; // Nếu sản phẩm không tồn tại, xem như không đủ
    }

    public void reduceQuantity(Long productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new IllegalArgumentException("Product not found"));
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Not enough quantity for product: " + product.getName());
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }
}
