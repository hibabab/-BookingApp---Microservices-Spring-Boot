package booking.example.booking.BookingLine;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingLineRepository extends JpaRepository<BookingLine, Integer> {
}
