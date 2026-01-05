package com.saebom.keebstation.domain.product;

import com.saebom.keebstation.domain.category.Category;
import com.saebom.keebstation.domain.option.ProductOption;
import com.saebom.keebstation.global.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product", indexes = {
        @Index(name = "idx_product_category_id", columnList = "category_id")
})
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "base_price", nullable = false)
    private long basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "varchar(20)")
    private ProductStatus status;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductOption> options = new ArrayList<>();

    public Product(Category category, String name, String description, long basePrice, ProductStatus status) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.status = status;
    }

}