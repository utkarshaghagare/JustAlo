package com.JustAlo.Repo;

import com.JustAlo.Entity.OrdinaryTrip;
import com.JustAlo.Entity.Trip;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;

public interface OrdinaryTripRepository extends JpaRepository<OrdinaryTrip, Long> {

    List<OrdinaryTrip> findAllByTripId(long trip);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrdinaryTrip o WHERE o.trip = :trip")
    void deleteAllByTrip(Trip trip);

    @Query("SELECT o.stopnumber FROM OrdinaryTrip o WHERE o.trip = :trip AND o.stopname = :stop")
    Integer findStopnumberByStopname(@Param("trip")Trip trip, @Param("stop") String stop);

    @Query("select count(b) from OrdinaryTrip b where b.trip = :trip")
    long countByTrip(@NonNull Trip trip);

    @Query("SELECT o.amount FROM OrdinaryTrip o WHERE o.trip=:trip AND o.stopname = :stop")
    double findAmountByStopName(@Param("stop") String stop,@Param("trip") Trip trip);

@Query("select stopname from OrdinaryTrip b where b.trip = :trip")
    List<String> findAllStopNames(@NonNull Trip trip);

    @Query("SELECT ot.stopname FROM OrdinaryTrip ot WHERE ot.trip = :trip AND ot.stopnumber BETWEEN :startStop AND :endStop ORDER BY ot.stopnumber")
    List<String> findStopsBetween(@Param("trip") Trip trip, @Param("startStop") Integer startStop, @Param("endStop") Integer endStop);
}