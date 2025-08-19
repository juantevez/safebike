package com.safe.bike.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    private int brandId;
    private String name;
    private LocalDateTime createdAt;

}
