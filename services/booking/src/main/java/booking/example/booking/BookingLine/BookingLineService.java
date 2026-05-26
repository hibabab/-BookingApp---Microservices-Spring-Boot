package booking.example.booking.BookingLine;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingLineService {

    private final BookingLineRepository repository;

    // Créer une réservation
    public BookingLineResponse createBookingLine(BookingLineRequest request) {
        BookingLine bookingLine = BookingLineMapper.toEntity(request);
        BookingLine saved = repository.save(bookingLine);
        return BookingLineMapper.toResponse(saved);
    }

    // Récupérer toutes les réservations
    public List<BookingLineResponse> getAllBookingLines() {
        return repository.findAll()
                .stream()
                .map(BookingLineMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Récupérer une réservation par ID
    public BookingLineResponse getBookingLineById(Integer id) {
        BookingLine bookingLine = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("BookingLine not found"));
        return BookingLineMapper.toResponse(bookingLine);
    }


}
