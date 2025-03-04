package com.alwi.ecommerce.service;

import com.alwi.ecommerce.dto.request.ProductRequest;
import com.alwi.ecommerce.dto.response.ProductResponse;
import com.alwi.ecommerce.exception.DataNotFoundException;
import com.alwi.ecommerce.exception.FileInvalidException;
import com.alwi.ecommerce.model.Category;
import com.alwi.ecommerce.model.Product;
import com.alwi.ecommerce.repository.CategoryRepository;
import com.alwi.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Value("${file.IMAGE_DIR}")
    private String imageDirectory;
    private static final String[] allowedFileTypes = {"image/jpeg", "image/png", "image/jpg"};

    public Page<ProductResponse> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> product = productRepository.findAll(pageable);

            return product.map(ProductService::convertToResponse);
        } catch(DataNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error find all cart",e);
        }
    }
    private String generateUniqueFileName(String originalFileName){
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID() + extension;
    }
    private static void validateFile(MultipartFile file){
        long maxFileSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxFileSize) {
            throw new FileInvalidException("File size exceeds the maximum limit of 5MB");
        }
        String fileType = file.getContentType();
        boolean isValidType = false;
        for (String type : allowedFileTypes) {
            if (Objects.equals(fileType, type)) {
                isValidType = true;
                break;
            }
        }
        if (!isValidType) {
            throw new FileInvalidException("Invalid file type. Only " + Arrays.toString(allowedFileTypes) + " are allowed.");
        }
    }
    public byte[] getImageById(Long id){
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(()-> new DataNotFoundException("Product not found"));
            if (product.getImage()!= null && !product.getImage().isEmpty()) {
                Path imagePath = Path.of(imageDirectory, product.getImage());
                return Files.readAllBytes(imagePath);
            }
            throw new FileInvalidException("Image not found");
        } catch(FileInvalidException | DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Failed to get image",e);
        }
    }
    public static Boolean checkQty(int qty){
        return qty <= 0;
    }
    @Transactional
    public ProductResponse create(ProductRequest productRequest){
        try{
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found with ID: " + productRequest.getCategoryId()));

            Product product = new Product();

            product.setName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setCategory(category);
            product.setQty(productRequest.getQty());
            product.setDisabled(checkQty(productRequest.getQty()));
            if(productRequest.getImage() != null && !productRequest.getImage().isEmpty()) {
                MultipartFile file = productRequest.getImage();
                validateFile(file);
                String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
                String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                Path fullPath = Path.of(imageDirectory, datePath,uniqueFileName);
                Path imagePath = Path.of(datePath, uniqueFileName);

                Files.createDirectories(fullPath.getParent());
                Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(imagePath.toString().replace("\\","/"));

            }
            Product savedProduct = productRepository.save(product);
            return convertToResponse(savedProduct);
        }catch(FileInvalidException | DataNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create product",e);
        }
    }
    @Transactional
    public ProductResponse update(Long id, ProductRequest productRequest){
        try{
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Product not found with ID: " + productRequest.getId()));

            if  (productRequest.getProductName() != null) product.setName(productRequest.getProductName());
            if (productRequest.getDescription() != null) product.setDescription(productRequest.getDescription());
            if  (productRequest.getPrice() != null) product.setPrice(productRequest.getPrice());
            if  (productRequest.getCategoryId() != null) {
                Category category = categoryRepository.findById(productRequest.getCategoryId())
                        .orElseThrow(() -> new DataNotFoundException("Category not found with ID: " + productRequest.getCategoryId()));
                product.setCategory(category);
            }
            if (productRequest.getQty() >0) product.setQty(productRequest.getQty());
            product.setDisabled(checkQty(product.getQty()));

            if(productRequest.getImage() != null && !productRequest.getImage().isEmpty()) {
                MultipartFile file = productRequest.getImage();
                validateFile(file);
                String uniqueFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
                String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                Path fullPath = Path.of(imageDirectory, datePath,uniqueFileName);
                Path imagePath = Path.of(datePath, uniqueFileName);

                Files.createDirectories(fullPath.getParent());
                Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
                product.setImage(imagePath.toString().replace("\\","/"));

            }

            Product savedProduct = productRepository.save(product);
            return convertToResponse(savedProduct);
        }catch(FileInvalidException | DataNotFoundException e){
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product",e);
        }
    }
    public ProductResponse findById(Long id){
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(()-> new DataNotFoundException("Product not found with ID: " + id));
            return convertToResponse(product);
        } catch(DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Failed to find product by id",e);
        }
    }
    public void delete(Long id){
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(()-> new DataNotFoundException("Product not found with ID: " + id));
            if (product.getImage()!= null && !product.getImage().isEmpty()) {
                Path imagePath = Path.of(imageDirectory, product.getImage());
                Files.deleteIfExists(imagePath);
            }
            productRepository.deleteById(id);
        } catch(DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Failed to delete product",e);
        }
    }
    public Page<ProductResponse> findByFilters(int page, int size, String category, Double minPrice, Double maxPrice,
                                               String name) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Category cat = categoryRepository.findByName(category).orElse(null);
            Page<Product> products = productRepository.findByFilters(cat, minPrice, maxPrice, name, pageable);

            return products.map(ProductService::convertToResponse);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find product by filters",e);
        }

    }
    public void updateQty(Long id, int qty){
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(()-> new DataNotFoundException("Product not found with ID: " + id));
            product.setQty(qty);
            product.setDisabled(checkQty(qty));
            productRepository.save(product);
        } catch(DataNotFoundException e){
            throw e;
        }catch (Exception e) {
            throw new RuntimeException("Failed to update product qty",e);
        }
    }

    public static ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setProductName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());

        response.setCategoryName(product.getCategory().getName());
        response.setCategoryId(product.getCategory().getId());
        response.setImage(product.getImage());
        response.setQty(product.getQty());
        response.setDisabled(product.getDisabled());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }

}
