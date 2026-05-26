package booking.example.booking.booking;

import booking.example.booking.BookingLine.BookingLine;
import booking.example.booking.BookingLine.BookingLineRequest;
import booking.example.booking.BookingLine.BookingLineResponse;
import booking.example.booking.Payment.PaymentClient;
import booking.example.booking.Payment.PaymentRequest;
import booking.example.booking.customer.CustomerClient;
import booking.example.booking.room.RoomBookingRequest;
import booking.example.booking.room.RoomBookingResponse;
import booking.example.booking.room.RoomClient;
import booking.example.booking.BookingLine.BookingLineService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final BookingMapper mapper;
    private final CustomerClient customerClient;
    private final PaymentClient paymentClient;
    private final RoomClient roomClient;
    private final BookingLineService bookingLineService;

    @Transactional
    public BookingResponse createbooking(BookingRequest request) throws Exception {
        // Vérifier le client
        var customer = customerClient.findCustomerById(request.customerId())
                .orElseThrow(() -> new Exception("Cannot create booking: No customer exists with the provided ID"));

        // Créer l'entité Booking via le mapper
        var booking = mapper.toEntity(request);


       int prixTotal = 0;
        List<BookingLine> bookingLines = new ArrayList<>();


        for (RoomBookingRequest line : request.bookingLines()) {

            RoomBookingResponse response = roomClient.reserveRoom(line);
            prixTotal += response.price();


            BookingLineRequest lineRequest = new BookingLineRequest(
                    response.roomId(),
                    response.checkIn(),
                    response.checkOut()
            );


            BookingLineResponse createdLine = bookingLineService.createBookingLine(lineRequest);


            BookingLine bookingLine = BookingLine.builder()
                    .id(createdLine.id())
                    .roomId(createdLine.roomId())
                    .checkIn(createdLine.checkIn())
                    .checkOut(createdLine.checkOut())
                    .build();

            bookingLines.add(bookingLine);
        }


        booking.setBookingLines(bookingLines);
        booking.setTotalPrice(prixTotal);


        var savedBooking = repository.save(booking);

        var paymentRequest = new PaymentRequest(
                prixTotal,
                booking.getReference(),
                customer
        );
        paymentClient.requestBookingPayment(paymentRequest);
        return mapper.toResponse(savedBooking);

    }


}
