package com.hiba.booking.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long hotelId,
            @Valid @RequestBody CommentDto commentDto) {

        CommentDto createdComment = commentService.createComment(hotelId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments() {
        List<CommentDto> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<CommentDto>> getCommentsByHotelId(@PathVariable Long hotelId) {
        List<CommentDto> comments = commentService.getCommentsByHotelId(hotelId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentDto commentDto) {

        CommentDto updatedComment = commentService.updateComment(id, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
