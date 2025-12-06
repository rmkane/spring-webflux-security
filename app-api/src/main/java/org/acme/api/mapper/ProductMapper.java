package org.acme.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import org.acme.api.dto.ProductRequest;
import org.acme.api.dto.ProductResponse;
import org.acme.persistence.model.Product;

/**
 * MapStruct mapper for Product entity and DTOs
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);
}
