package com.modsensoftware.library_service.utils;

import com.modsensoftware.library_service.dtos.responses.LibraryBookQuantityDTO;
import com.modsensoftware.library_service.models.LibraryBookQuantity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookQuantityMapper {
    BookQuantityMapper INSTANCE = Mappers.getMapper(BookQuantityMapper.class);

    LibraryBookQuantityDTO toDto(LibraryBookQuantity libraryBookQuantity);
}
