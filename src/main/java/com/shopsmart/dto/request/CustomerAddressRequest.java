package com.shopsmart.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAddressRequest {

    @NotBlank(message = "Address type is required")
    @Pattern(regexp = "^(BILLING|SHIPPING|BOTH)$", message = "Type must be BILLING, SHIPPING, or BOTH")
    private String type;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 200, message = "Address line 1 must not exceed 200 characters")
    private String addressLine1;

    @Size(max = 200, message = "Address line 2 must not exceed 200 characters")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^\\d{6}$", message = "Pincode must be 6 digits")
    private String pincode;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Builder.Default
    private Boolean isDefault = false;
}