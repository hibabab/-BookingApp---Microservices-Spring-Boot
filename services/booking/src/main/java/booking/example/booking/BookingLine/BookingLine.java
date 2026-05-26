package booking.example.booking.BookingLine;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "booking_lines")
public class BookingLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long roomId;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;





}
