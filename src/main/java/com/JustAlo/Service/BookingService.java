package com.JustAlo.Service;

import com.JustAlo.Entity.Booking;
import com.JustAlo.Entity.Passenger;
import com.JustAlo.Entity.Trip;
import com.JustAlo.Entity.User;
import com.JustAlo.Model.JourneyDetails;
import com.JustAlo.Model.Passenger_details;
import com.JustAlo.Model.Seats;
import com.JustAlo.Model.TicketBooking;
import com.JustAlo.Repo.*;
import com.JustAlo.Security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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
//    public List<Integer> findSeats(String start, String destination, Trip trip){
//        List<Booking> bookingOfTrip= bookingRepository.findAllByTrip(trip);
//
//        List<Integer> availableSeats= new ArrayList<>();
//
//        for(Booking booking: bookingOfTrip){
//            if(booking.getPassenger()==null ){
//                availableSeats.add(booking.getSeatno());
//            }
//            else {
//                if(checkAvailability(start, booking.getEnding_stop(),trip) && booking.getStatus()){
//                    availableSeats.add(booking.getSeatno());
//                }
//               else if (checkAvailability(booking.getStarting_stop(), destination,trip) && booking.getStatus()){
//                    availableSeats.add(booking.getSeatno());
//                }
//
//            }
//        }
//        return availableSeats;
//    }

    public Seats findSeats(String start, String destination, Trip trip){
        List<Booking> bookingOfTrip= bookingRepository.findAllByTrip(trip);

        List<Integer> availableSeats= new ArrayList<>();
        List<Integer> your= new ArrayList<>();
        List<Integer> vendor= new ArrayList<>();

        for(Booking booking: bookingOfTrip){
            if(booking.getPassenger()==null ){
                availableSeats.add(booking.getSeatno());
            }
            else if("Ordinary".equals(trip.getType())&& checka(booking,start, destination,trip)){
                availableSeats.add(booking.getSeatno());
            }
            else{
                User current_user=userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER);

                if(current_user!= null && Objects.equals(booking.getPassenger().getUser(), current_user.getId())&& !Objects.equals(booking.getStatus(), "CANCELLED")){
                    your.add(booking.getSeatno());
                } else if (current_user==null && booking.getPassenger().getUser()==null&& !Objects.equals(booking.getStatus(), "CANCELLED")) {
                    vendor.add(booking.getSeatno());
                }
            }
        }
        Seats seats= new Seats(availableSeats,your,vendor);
        return seats;
    }

    private boolean checka(Booking booking, String start, String destination, Trip trip) {
        List<String> avail_stop= booking.getAvailableStops();
        List<String> req_stp= ordinaryTripRepository.findStopsBetween(trip,ordinaryTripRepository.findStopnumberByStopname(trip,start),ordinaryTripRepository.findStopnumberByStopname(trip,destination));
        return new HashSet<>(avail_stop).containsAll(req_stp);
    }

//    public boolean checkAvailability(String start, String ending_stop, Trip trip) {
//        if ("Ordinary".equals(trip.getType())) {
//            int startStopNumber = (start != null)?ordinaryTripRepository.findStopnumberByStopname(trip, start):1;
//            int endStopNumber = (ending_stop != null) ? ordinaryTripRepository.findStopnumberByStopname(trip, ending_stop) : 1;
//
//            return startStopNumber >= endStopNumber;
//        }
//        return false;
//    }
//
//    public long getSTopNumberByTrip(Trip trip){
//        return ordinaryTripRepository.countByTrip(trip);
//    }
//


    //check if it throw exception if id of a passenger is not given i.e it is new passenger - DOESN'T THROW EXCEPTION
    //findseats give correct seats- GIVES CORRECT SEATS

//    public String bookSeat(TicketBooking ticketBooking, Trip trip) throws Exception {
//        //Find available seats
//        List<Integer> availableSeats=findSeats(ticketBooking.getStart(),ticketBooking.getEnd(),trip);
//
//        //for each passengers
//        for (Passenger_details passenger: ticketBooking.getPassengers()){
//
//            if(availableSeats.contains(passenger.getSeat_no())){
//
//                Booking booking= bookingRepository.findByTripAndSeatnumber(trip,passenger.getSeat_no());
//
//                if(booking.getPassenger()!=null && "Ordinary".equals(trip.getType())){
//                    List<String> allStops = ordinaryTripRepository.findAllStopNames(trip);
//                    List<String> requestedStops = ordinaryTripRepository.findStopsBetween(trip,ordinaryTripRepository.findStopnumberByStopname(trip,ticketBooking.getStart()),ordinaryTripRepository.findStopnumberByStopname(trip, ticketBooking.getEnd()));
//                    Booking book= new Booking(trip,booking.getSeatno(),allStops);
//
//                    User user= userDao.getById(ticketBooking.getUser_id());
//                    book.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
//
//                    book.setStarting_stop(ticketBooking.getStart());
//                    book.setEnding_stop(ticketBooking.getEnd());
////                  set amount
//                    book.setAmount(setamount(ticketBooking.getStart(),ticketBooking.getEnd()));
//
////                    if(!(ordinaryTripRepository.findStopnumberByStopname(ticketBooking.getEnd())<getSTopNumberByTrip(trip))){
////                        book.setStatus(false);
////                    }
//                    //razorpay id set remaining;
//                    bookingRepository.save(book);
//                    booking.setStatus(false);
//                    bookingRepository.save(booking);
//                }
//                else{
//                    //can be setted automatically
//                    User user= userDao.getById(ticketBooking.getUser_id());
//                    booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
//
//                    booking.setStarting_stop(ticketBooking.getStart());
//                    booking.setEnding_stop(ticketBooking.getEnd());
//
//                    //onlyy for ordinary-
//                    if("Ordinary".equals(trip.getType())){
//                        booking.setAmount(setamount(ticketBooking.getStart(),ticketBooking.getEnd()));
////                        if(!(ordinaryTripRepository.findStopnumberByStopname(ticketBooking.getEnd())<getSTopNumberByTrip(trip))){
////                            booking.setStatus(false);
////                        }
//                    }
//                    else{
//                        //only for luxury
//                        booking.setAmount(trip.getAmount());
//                      //   booking.setStatus(false);
//                    }
//                    //razorpay id set remaining;
//                    bookingRepository.save(booking);
////
//                }
//
//            }
//            //these exception is important to be shown but now showing api working properly.
//            else{
//                throw new Exception("SEAT " + passenger.getSeat_no() +" UNAVAILABLE");
//            }
//        }
//return "Booked";
//    }

    public String bookSeat(TicketBooking ticketBooking, Trip trip) throws Exception {
        List<Integer> availableSeats=findSeats(ticketBooking.getStart(),ticketBooking.getEnd(),trip).available;

        for (Passenger_details passenger : ticketBooking.getPassengers()) {

            if(availableSeats.contains(passenger.getSeat_no())){
                if("Ordinary".equals(trip.getType())){
                    List<String> allavailableStops = bookingRepository.findAllAvailableStop(trip,passenger.getSeat_no());
                    List<String> requestedStops = ordinaryTripRepository.findStopsBetween(trip,ordinaryTripRepository.findStopnumberByStopname(trip,ticketBooking.getStart()),ordinaryTripRepository.findStopnumberByStopname(trip, ticketBooking.getEnd())-1);

                    allavailableStops.removeAll(requestedStops);
                    List<String> stop = new ArrayList<>(allavailableStops);
//                    Booking booking= new Booking(trip, passenger.getSeat_no(),stop );
//                    booking.setTrip(trip);
//                    booking.setSeatno(passenger.getSeat_no());
//                    booking.setStarting_stop(ticketBooking.getStart());
//                    booking.setEnding_stop(ticketBooking.getEnd());
//                    User user= userDao.getById(ticketBooking.getUser_id());
//                    booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
//
//                    bookingRepository.save(booking);

                    List<Booking> prev_bookings= bookingRepository.findByTripAndSeatnumber(trip, passenger.getSeat_no());
                    int i=0;
                    for(Booking prev_booking: prev_bookings){
                        if(prev_booking.getPassenger()==null){
                            prev_booking.setTrip(trip);
                            // prev_booking.setSeatno(passenger.getSeat_no());
                            prev_booking.setStarting_stop(ticketBooking.getStart());
                            prev_booking.setEnding_stop(ticketBooking.getEnd());
                            User user1= userDao.getById(ticketBooking.getUser_id());
                            prev_booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user1.getId()))));
                            prev_booking.setAmount(setamount(trip,ticketBooking.getStart(),ticketBooking.getEnd()));
                            prev_booking.setAvailableStops(stop);
                            prev_booking.setStatus("BOOKED");
                            bookingRepository.save(prev_booking);
                        }
                        else{
                            if(i==0){
                                Booking booking= new Booking(trip,passenger.getSeat_no(),stop);
                                booking.setStarting_stop(ticketBooking.getStart());
                                booking.setEnding_stop(ticketBooking.getEnd());
                                User user1= userDao.getById(ticketBooking.getUser_id());
                                booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user1.getId()))));
                                booking.setAmount(setamount(trip,ticketBooking.getStart(),ticketBooking.getEnd()));
                                booking.setStatus("BOOKED");
                                bookingRepository.save(prev_booking);
                                i++;
                            }
                            prev_booking.setAvailableStops(stop);
                            bookingRepository.save(prev_booking);
                        }
                    }
                }
                else{
                    Booking prev_bookings= bookingRepository.findByTripAndSeat(trip, passenger.getSeat_no());

                    if(prev_bookings.getPassenger()==null){
                        prev_bookings.setTrip(trip);
                        prev_bookings.setSeatno(passenger.getSeat_no());
                        prev_bookings.setStarting_stop(ticketBooking.getStart());
                        prev_bookings.setEnding_stop(ticketBooking.getEnd());
                        User user= userDao.getById(ticketBooking.getUser_id());
                        prev_bookings.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
                        prev_bookings.setAmount(trip.getAmount());
                        prev_bookings.setStatus("BOOKED");
                        bookingRepository.save(prev_bookings);
                    }
                    else{
                        throw new Exception("Already booked");
                    }
                }
//                Booking booking = bookingRepository.findByTripAndSeatnumber(trip, passenger.getSeat_no());
//
//                List<String> requestedStops = ordinaryTripRepository.findStopNamesBetween(ticketBooking.getStart(), ticketBooking.getEnd());
//
//                if (isSeatAvailableForStops(booking, requestedStops)) {
//                    booking.getAvailableStops().removeAll(requestedStops);
//                    booking.setPassenger(passengerRepository.findById(passenger.getId())
//                            .orElse(passengerRepository.save(new Passenger(passenger.getName(), passenger.getAge(), userDao.getById(ticketBooking.getUser_id())))));
//                    booking.setStarting_stop(ticketBooking.getStart());
//                    booking.setEnding_stop(ticketBooking.getEnd());
//                    booking.setAmount(setAmount(ticketBooking.getStart(), ticketBooking.getEnd()));
//                    bookingRepository.save(booking);
//                }
            }
            else {
                throw new Exception("SEAT " + passenger.getSeat_no() + " UNAVAILABLE");
            }
        }

        return "Booked";
    }

    //working check
    private Double setamount(Trip trip, String start, String end) {
        return ordinaryTripRepository.findAmountByStopName(end,trip)-ordinaryTripRepository.findAmountByStopName(start,trip);
    }

//


    public void cancelTicket(Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            if ("Ordinary".equals(booking.getTrip().getType())) {
                int seat_no = booking.getSeatno();
                List<String> allavailableStops = bookingRepository.findAllAvailableStop(booking.getTrip(), booking.getSeatno());
                List<String> requestedStops = ordinaryTripRepository.findStopsBetween(booking.getTrip(), ordinaryTripRepository.findStopnumberByStopname(booking.getTrip(), booking.getStarting_stop()), ordinaryTripRepository.findStopnumberByStopname(booking.getTrip(), booking.getEnding_stop()) - 1);
                allavailableStops.addAll(requestedStops);
                booking.setAvailableStops(allavailableStops);
                booking.setStatus("CANCELLED");
                bookingRepository.save(booking);
            } else {
                booking.setStatus("CANCELLED");
                bookingRepository.save(booking);
                Booking prev_bookings = new Booking();
                prev_bookings.setTrip(booking.getTrip());
                prev_bookings.setSeatno(booking.getSeatno());
                bookingRepository.save(prev_bookings);
            }
        }
    }
    public List<Passenger> getPassengers(User byEmail) {
        return passengerRepository.findAllByUser(byEmail);
    }

    public JourneyDetails getdetails(Trip trip, String stopName, Integer remaining) {
        List<Booking> bookings= bookingRepository.findAllByTrip(trip);
        int in = 0, out = 0;
        for (Booking b: bookings){
            if(b.getStarting_stop()==stopName){
                in++;
            } else if (b.getEnding_stop()==stopName) {
                out++;
            }
        }
        remaining+=in;
        remaining-=out;
        return new JourneyDetails(in,out,remaining);

    }
}




