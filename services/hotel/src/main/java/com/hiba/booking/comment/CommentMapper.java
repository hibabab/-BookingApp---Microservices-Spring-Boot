package com.hiba.booking.comment;

import com.hiba.booking.hotel.Hotel;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CommentMapper {

    public Comment toEntity(CommentDto dto, Hotel hotel) {
        if (dto == null) {
            return null;
        }

        return Comment.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .description(dto.description())
                .authorType(dto.authorType())
                .date(dto.date() != null ? dto.date() : LocalDate.now())
                .hotel(hotel)
                .build();
    }

    public CommentDto toDto(Comment entity) {
        if (entity == null) {
            return null;
        }

        return new CommentDto(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDescription(),
                entity.getAuthorType(),
                entity.getDate()
        );
    }
}
