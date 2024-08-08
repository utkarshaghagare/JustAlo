package com.JustAlo.Service;

import com.JustAlo.Entity.Booking;
import com.JustAlo.Entity.Passenger;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.User;
import com.JustAlo.Model.Passenger_details;
import com.JustAlo.Model.TicketBooking;
import com.JustAlo.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private OrdinaryTripRepository ordinaryTripRepository;

    @Autowired
    private UserDao userDao;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private TripRepository tripRepository;


    //list of seats and amount
    public List<Integer> findSeats(String start, String destination, Trip trip){
        List<Booking> bookingOfTrip= bookingRepository.findAllByTrip(trip);

        List<Integer> availableSeats= new ArrayList<>();

        for(Booking booking: bookingOfTrip){
            if(booking.getPassenger()==null && booking.getStatus()){
                availableSeats.add(booking.getSeatno());
            }
            else {
                if(checkAvailability(start, booking.getEnding_stop(),trip) && booking.getStatus()){
                    availableSeats.add(booking.getSeatno());
                }
            }
        }
        return availableSeats;
    }

    public boolean checkAvailability(String start, String ending_stop, Trip trip) {
        if ("Ordinary".equals(trip.getType())) {
            Integer startStopNumber = ordinaryTripRepository.findStopnumberByStopname(start);
            Integer endStopNumber = (ending_stop != null) ? ordinaryTripRepository.findStopnumberByStopname(ending_stop) : 1;

            return startStopNumber >= endStopNumber;
        }
        return false;
    }

    public long getSTopNumberByTrip(Trip trip){
        return ordinaryTripRepository.countByTrip(trip);
    }
    //check if it throw exception if id of a passenger is not given i.e it is new passenger - DOESN'T THROW EXCEPTION
    //findseats give correct seats- GIVES CORRECT SEATS
    public String bookSeat(TicketBooking ticketBooking, Trip trip) throws Exception {
        List<Integer> availableSeats=findSeats(ticketBooking.getStart(),ticketBooking.getEnd(),trip);
        for (Passenger_details passenger: ticketBooking.getPassengers()){
            if(availableSeats.contains(passenger.getSeat_no())){
                Booking booking= bookingRepository.findByTripAndSeatnumber(trip,passenger.getSeat_no());

                if(booking.getPassenger()!=null && "Ordinary".equals(trip.getType())){
                    Booking book= new Booking(trip,booking.getSeatno());
                    User user= userDao.getById(ticketBooking.getUser_id());
                    book.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
                    book.setStarting_stop(ticketBooking.getStart());
                    book.setEnding_stop(ticketBooking.getEnd());
//                  set amount
                    book.setAmount(setamount(ticketBooking.getStart(),ticketBooking.getEnd()));
                    if(!(ordinaryTripRepository.findStopnumberByStopname(ticketBooking.getEnd())<getSTopNumberByTrip(trip))){
                        book.setStatus(false);
                    }
                    //amount razorpay id set remaining;
                    bookingRepository.save(book);
                    booking.setStatus(false);
                    bookingRepository.save(booking);
                }
                else{
                    //can be setted automatically
                    User user= userDao.getById(ticketBooking.getUser_id());
                    booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
                    booking.setStarting_stop(ticketBooking.getStart());
                    booking.setEnding_stop(ticketBooking.getEnd());

                    //onlyy for ordinary
                    if("Ordinary".equals(trip.getType())){
                        booking.setAmount(setamount(ticketBooking.getStart(),ticketBooking.getEnd()));
                        if(!(ordinaryTripRepository.findStopnumberByStopname(ticketBooking.getEnd())<getSTopNumberByTrip(trip))){
                            booking.setStatus(false);
                        }
                    }
                    else{
                        //only for luxury
                        booking.setAmount(trip.getAmount());
                        booking.setStatus(false);
                    }
                    //razorpay id set remaining;
                    bookingRepository.save(booking);
//
                }

            }
            //these exception is important to be shown but now showing api working properly.
            else{
                throw new Exception("SEAT " + passenger.getSeat_no() +" UNAVAILABLE");
            }
        }
return "Booked";
    }

    //working check
    private Double setamount(String start, String end) {
        return ordinaryTripRepository.findAmountByStopName(end)-ordinaryTripRepository.findAmountByStopName(start);
    }

    public String reserveSeat(Long id, List<Integer> seats) {
        Trip trip= tripRepository.findById(id).orElse(null);
        if(trip!=null){
            for(int i: seats){
                bookingRepository.reserveSeat(trip,i);
            }
        }
        else{
            return"No such Trip present";
        }
        return "reserved"+ seats +"in Trip"+ trip;
    }
}
