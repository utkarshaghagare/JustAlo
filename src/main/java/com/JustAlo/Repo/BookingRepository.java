package com.JustAlo.Repo;

import com.JustAlo.Entity.Booking;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByTrip(Trip trip);

   // Booking findByTripandSeatno(Trip trip, Integer seatno);
   @Query("SELECT b FROM Booking b WHERE b.trip = :trip AND b.seatno = :seatNo")
   Booking findByTripAndSeatnumber(@Param("trip") Trip trip, @Param("seatNo") Integer seatNo);

    @Query("select count(b) from Booking b where b.trip = :trip")
    long countByTrip(@NonNull Trip trip);

    @Query("SELECT b FROM Booking b WHERE b.passenger.user = :byEmail")
    List<Booking> findAllByPassenger_User_Id(@Param("byEmail")User byEmail);


    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.status = false WHERE b.trip = :trip AND b.seatno = :seatno")
    void reserveSeat(@Param("trip") Trip trip, @Param("seatno") Integer seatno);

}