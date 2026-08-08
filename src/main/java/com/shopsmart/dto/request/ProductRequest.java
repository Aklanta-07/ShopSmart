package com.shopsmart.dto.request;

import java.math.BigDecimal;

import com.shopsmart.entity.ProductUnit;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must not exceed 50 characters")
    @Pattern(regexp = "^[A-Z0-9-_]+$", message = "SKU must contain only uppercase letters, numbers, hyphens, and underscores")
    private String sku;

    @Size(max = 100, message = "Barcode must not exceed 100 characters")
    private String barcode;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Price format invalid")
    private BigDecimal price;

    @NotNull(message = "Cost price is required")
    @DecimalMin(value = "0.0", message = "Cost price cannot be negative")
    @Digits(integer = 15, fraction = 4, message = "Cost price format invalid")
    private BigDecimal costPrice;

    @DecimalMin(value = "0.0", message = "Tax rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Tax rate cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Tax rate format invalid")
    private BigDecimal taxRate = BigDecimal.ZERO;

    @NotNull(message = "Unit is required")
    private ProductUnit unit = ProductUnit.PIECE;

    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel = 10;

    @Min(value = 0, message = "Max stock level cannot be negative")
    private Integer maxStockLevel;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Boolean isActive = true;
}