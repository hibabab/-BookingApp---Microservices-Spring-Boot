package com.hiba.booking.comment;

import com.hiba.booking.hotel.Hotel;
import com.hiba.booking.hotel.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final HotelRepository hotelRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createComment(Long hotelId, CommentDto commentDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found with ID: " + hotelId));

        Comment comment = commentMapper.toEntity(commentDto, hotel);
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toDto(savedComment);
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));

        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByHotelId(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new RuntimeException("Hotel not found with ID: " + hotelId);
        }

        return commentRepository.findByHotelId(hotelId)
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + id));

        existingComment.setFirstName(commentDto.firstName());
        existingComment.setLastName(commentDto.lastName());
        existingComment.setDescription(commentDto.description());
        existingComment.setAuthorType(commentDto.authorType());

        if (commentDto.date() != null) {
            existingComment.setDate(commentDto.date());
        }

        Comment updatedComment = commentRepository.save(existingComment);
        return commentMapper.toDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with ID: " + id);
        }
        commentRepository.deleteById(id);
    }
}
