package com.JustAlo.Repo;

import com.JustAlo.Entity.Booking;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByTrip(Trip trip);

   // Booking findByTripandSeatno(Trip trip, Integer seatno);
    @Query("SELECT b FROM Booking b WHERE b.trip = :trip AND b.seatno = :seatNo")
    List<Booking> findByTripAndSeatnumber(@Param("trip") Trip trip, @Param("seatNo") Integer seatNo);

    @Query("SELECT b FROM Booking b WHERE b.trip = :trip AND b.seatno = :seatNo")
    Booking findByTripAndSeat(@Param("trip") Trip trip, @Param("seatNo") Integer seatNo);
    @Query("select count(b) from Booking b where b.trip = :trip")
    long countByTrip(@NonNull Trip trip);

    @Query("SELECT b FROM Booking b WHERE b.passenger.user = :byEmail AND b.status= :status")
    List<Booking> findAllByPassenger_User_Id(@Param("byEmail") Long byEmail, @Param("status") String status);

    @Query("SELECT b.availableStops FROM Booking b WHERE b.trip = :trip AND b.seatno = :seatNo")
    List<String> findAllAvailableStop(Trip trip, Integer seatNo);
//    @Query("SELECT * FROM Booking b WHERE b.passenger.user = :byEmail ORDER BY b.date DESC ")
    @Query("SELECT b FROM Booking b WHERE b.passenger.user = :byEmail ORDER BY b.date DESC")
    List<Booking> findByPassengerOrderByBookingDateDesc(User byEmail);

    @Query("SELECT b FROM Booking b WHERE b.razorpay_booking_id = :razorpay_booking_id")
    List<Booking> findByRazorpayBookingId(@Param("razorpay_booking_id") String razorpay_booking_id);

    @Query(value = "SELECT COUNT(*) FROM booking WHERE trip_id = :tripId AND status = 'BOOKED'", nativeQuery = true)
    long countBookedSeatsByTripId(@Param("tripId") long tripId);


//    @Modifying
//    @Transactional
//    @Query("UPDATE Booking b SET b.status = false WHERE b.trip = :trip AND b.seatno = :seatno")
//    void reserveSeat(@Param("trip") Trip trip, @Param("seatno") Integer seatno);

}