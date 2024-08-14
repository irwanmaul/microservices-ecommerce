package com.irwan.bvk.product.model;

import com.github.slugify.Slugify;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "tbl_product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String productName;
    private String slug;
}
