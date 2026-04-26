package com.room.management.mapper;

import com.room.management.dto.response.PaymentResponseDto;
import com.room.management.entity.room.Payments;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "reservation.id", target = "reservationId")
    PaymentResponseDto toDto(Payments payment);

    default List<PaymentResponseDto> toDtoList(List<Payments> payments) {
        return payments.stream().map(this::toDto).toList();
    }
}
