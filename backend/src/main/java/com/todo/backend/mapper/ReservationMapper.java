package com.todo.backend.mapper;

import com.todo.backend.dto.reservation.ReservationDto;
import com.todo.backend.dto.reservation.ResponseReservationDto;
import com.todo.backend.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    Reservation toEntity(ReservationDto reservationDto);
    ReservationDto toDto(Reservation reservation);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(ReservationDto reservationDto, @MappingTarget Reservation reservation);

    ResponseReservationDto toResponseDto(Reservation reservation);
}
