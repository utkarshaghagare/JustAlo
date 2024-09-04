package com.JustAlo.Service;

import com.JustAlo.Entity.*;
import com.JustAlo.Model.*;
import com.JustAlo.Repo.*;
import com.JustAlo.Security.JwtAuthenticationFilter;
import com.razorpay.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
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

    @Autowired
    private  PaymentService paymentService;


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
        seats.setTotal_seats(trip.getBus().getTotal_seats());
        seats.setLast_row_seats(trip.getBus().getLast_row_seats());
        seats.setLayout(trip.getBus().getLayout());
        seats.setNo_of_row(trip.getBus().getNo_of_row());
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








//----------------------------bookSeat code ------------------
//    public String bookSeat(TicketBooking ticketBooking, Trip trip) throws Exception {
//        List<Integer> availableSeats=findSeats(ticketBooking.getStart(),ticketBooking.getEnd(),trip).available;
//
//        double amount=0;
//        String razorpayOrderId = null;
//
//
//        //   Order order= paymentService.bookedTicket( totalamount);
//        for (Passenger_details passenger : ticketBooking.getPassengers()) {
//
//            if(availableSeats.contains(passenger.getSeat_no())){
//                if("Ordinary".equals(trip.getType())){
//                    List<String> allavailableStops = bookingRepository.findAllAvailableStop(trip,passenger.getSeat_no());
//                    List<String> requestedStops = ordinaryTripRepository.findStopsBetween(trip,ordinaryTripRepository.findStopnumberByStopname(trip,ticketBooking.getStart()),ordinaryTripRepository.findStopnumberByStopname(trip, ticketBooking.getEnd())-1);
//
//                    allavailableStops.removeAll(requestedStops);
//                    List<String> stop = new ArrayList<>(allavailableStops);
////                    Booking booking= new Booking(trip, passenger.getSeat_no(),stop );
////                    booking.setTrip(trip);
////                    booking.setSeatno(passenger.getSeat_no());
////                    booking.setStarting_stop(ticketBooking.getStart());
////                    booking.setEnding_stop(ticketBooking.getEnd());
////                    User user= userDao.getById(ticketBooking.getUser_id());
////                    booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
////                    booking.setAmount(setamount(trip, ticketBooking.getStart(), ticketBooking.getEnd()));
////                    booking.setDate(Date.valueOf(LocalDate.now()));
////                    booking.setStatus("PENDING");  // Set status as pending until payment is confirmed
//                 //   booking.setRazorpay_booking_id(orderId);  // Save Razorpay order ID
//
//                  //  bookingRepository.save(booking);
//
//                    List<Booking> prev_bookings= bookingRepository.findByTripAndSeatnumber(trip, passenger.getSeat_no());
//                    int i=0;
//                    for(Booking prev_booking: prev_bookings){
//                        if(prev_booking.getPassenger()==null){
//                            prev_booking.setTrip(trip);
//                            // prev_booking.setSeatno(passenger.getSeat_no());
//                            prev_booking.setStarting_stop(ticketBooking.getStart());
//                            prev_booking.setEnding_stop(ticketBooking.getEnd());
//                            User user1= userDao.getById(ticketBooking.getUser_id());
//                            prev_booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user1.getId()))));
//                            prev_booking.setAmount(setamount(trip,ticketBooking.getStart(),ticketBooking.getEnd()));
//                           prev_booking.setDate(Date.valueOf(LocalDate.now()));
//                            prev_booking.setAvailableStops(stop);
//                            prev_booking.setStatus("BOOKED");
//                            bookingRepository.save(prev_booking);
//                            amount+=setamount(trip,ticketBooking.getStart(),ticketBooking.getEnd());
//                            i++;
//                        }
//                        else{
//                            if(i==0){
//                                Booking booking = new Booking(trip,passenger.getSeat_no(),stop);
//                                booking.setStarting_stop(ticketBooking.getStart());
//                                booking.setEnding_stop(ticketBooking.getEnd());
//                                User user1= userDao.getById(ticketBooking.getUser_id());
//                                booking.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user1.getId()))));
//                                booking.setAmount(setamount(trip,ticketBooking.getStart(),ticketBooking.getEnd()));
//                                booking.setDate(Date.valueOf(LocalDate.now()));
//                                booking.setStatus("BOOKED");
//                                bookingRepository.save(prev_booking);
//                                amount+=setamount(trip,ticketBooking.getStart(),ticketBooking.getEnd());
//                                i++;
//                            }
//                            prev_booking.setAvailableStops(stop);
//                            bookingRepository.save(prev_booking);
//                        }
//                    }
//                }
//                else{
//                    Booking prev_bookings= bookingRepository.findByTripAndSeat(trip, passenger.getSeat_no());
//
//                    if(prev_bookings.getPassenger()==null){
//                        prev_bookings.setTrip(trip);
//                        prev_bookings.setSeatno(passenger.getSeat_no());
//                        prev_bookings.setStarting_stop(ticketBooking.getStart());
//                        prev_bookings.setEnding_stop(ticketBooking.getEnd());
//                        User user= userDao.getById(ticketBooking.getUser_id());
//                        prev_bookings.setPassenger(passengerRepository.findById(passenger.getId()).orElse(passengerRepository.save(new Passenger(passenger.getName(),passenger.getAge(),user.getId()))));
//                        prev_bookings.setAmount(trip.getAmount());
//                        prev_bookings.setDate(Date.valueOf(LocalDate.now()));
//                        prev_bookings.setStatus("BOOKED");
//
//
//                     //   prev_bookings.setRazorpay_booking_id(orderId); // Save Razorpay order ID
//
//                        bookingRepository.save(prev_bookings);
//                        amount= ticketBooking.getAmount();
//                    }
//                    else{
//                        throw new Exception("Seat"+passenger.getSeat_no()+" is All ready book");
//                    }
//                }
//
//
//            }
//            else {
//                throw new Exception("SEAT " + passenger.getSeat_no() + " UNAVAILABLE");
//            }
//        }
////       double totalAmount = amount;
//
//        // Create Razorpay order after calculating totalAmount
//        //double amount;
//        Order order = paymentService.bookedTicket(amount);
//        razorpayOrderId = order.get("id");
//
//        // Update razorpay_booking_id in each booking
//        for (Passenger_details passenger : ticketBooking.getPassengers()) {
//            Booking booking = bookingRepository.findByTripAndSeat(trip, passenger.getSeat_no());
//            if (booking != null && booking.getPassenger() != null) {
//                booking.setRazorpay_booking_id(razorpayOrderId);
//                bookingRepository.save(booking);
//            }
//        }
//
////        ticketBooking.setRazorpay_booking_id(razorpayOrderId);
////       paymentService.saveTransaction(razorpayOrderId, amount, "INR", "created", null);
//
//
//
//        return "BOOKED";
//                //order.get(orderId);
//    }
//
//    //working check
//    private Double setamount(Trip trip, String start, String end) {
//        Double startAmount = ordinaryTripRepository.findAmountByStopName(start, trip);
//
//        // Fetch the amount for the end stop
//        Double endAmount = ordinaryTripRepository.findAmountByStopName(end, trip);
//
//        // Check if both amounts are non-null (to avoid NullPointerException)
//        if (startAmount != null && endAmount != null) {
//            // Subtract the start stop amount from the end stop amount to get the total amount
//            return endAmount - startAmount;
//        } else {
//            // Handle the case where one or both amounts are null (e.g., return 0 or throw an exception)
//            return 0.0; // or throw new IllegalArgumentException("Start or end stop amount not found");
//        }
//        //return ordinaryTripRepository.findAmountByStopName(end,trip)-ordinaryTripRepository.findAmountByStopName(start,trip);
//    }

    public String bookSeat(TicketBooking ticketBooking, Trip trip) throws Exception {
        List<Integer> availableSeats = findSeats(ticketBooking.getStart(), ticketBooking.getEnd(), trip).available;
        double totalAmount = 0;
        String razorpayOrderId = null;

        // Retrieve the current user from the security context
        String email = JwtAuthenticationFilter.CURRENT_USER;
        User currentUser = userDao.findByEmail(email);

        for (Passenger_details passenger : ticketBooking.getPassengers()) {
            int seatNumber = passenger.getSeat_no();

            if (availableSeats.contains(seatNumber)) {
                Booking booking;
                if ("Ordinary".equals(trip.getType())) {
                    // Logic for Ordinary trip
                    List<String> allAvailableStops = bookingRepository.findAllAvailableStop(trip, seatNumber);
                    List<String> requestedStops = ordinaryTripRepository.findStopsBetween(
                            trip,
                            ordinaryTripRepository.findStopnumberByStopname(trip, ticketBooking.getStart()),
                            ordinaryTripRepository.findStopnumberByStopname(trip, ticketBooking.getEnd()) - 1
                    );

                    allAvailableStops.removeAll(requestedStops);
                    List<String> stop = new ArrayList<>(allAvailableStops);

                    List<Booking> prevBookings = bookingRepository.findByTripAndSeatnumber(trip, seatNumber);
                    int i = 0;
                    for (Booking prevBooking : prevBookings) {
                        if (prevBooking.getPassenger() == null) {
                            prevBooking.setTrip(trip);
                            prevBooking.setStarting_stop(ticketBooking.getStart());
                            prevBooking.setEnding_stop(ticketBooking.getEnd());
                            prevBooking.setPassenger(
                                    passengerRepository.findById(passenger.getId())
                                            .orElse(passengerRepository.save(new Passenger(passenger.getName(), passenger.getAge(), currentUser.getId()))));
                            prevBooking.setAmount(setAmount(trip, ticketBooking.getStart(), ticketBooking.getEnd()));
                            prevBooking.setDate(Date.valueOf(LocalDate.now()));
                            prevBooking.setAvailableStops(stop);
                            prevBooking.setStatus("BOOKED");
                            bookingRepository.save(prevBooking);
                            totalAmount += setAmount(trip, ticketBooking.getStart(), ticketBooking.getEnd());
                            i++;
                        } else {
                            if (i == 0) {
                                booking = new Booking(trip, seatNumber, stop);
                                booking.setStarting_stop(ticketBooking.getStart());
                                booking.setEnding_stop(ticketBooking.getEnd());
                                booking.setPassenger(
                                        passengerRepository.findById(passenger.getId())
                                                .orElse(passengerRepository.save(new Passenger(passenger.getName(), passenger.getAge(), currentUser.getId()))));
                                booking.setAmount(setAmount(trip, ticketBooking.getStart(), ticketBooking.getEnd()));
                                booking.setDate(Date.valueOf(LocalDate.now()));
                                booking.setStatus("BOOKED");
                                bookingRepository.save(booking);
                                totalAmount += setAmount(trip, ticketBooking.getStart(), ticketBooking.getEnd());
                                i++;
                            }
                            prevBooking.setAvailableStops(stop);
                            bookingRepository.save(prevBooking);
                        }
                    }
                } else if ("Luxury".equals(trip.getType())) {
                    // Logic for Luxury trip
                    Booking luxuryBooking = bookingRepository.findByTripAndSeat(trip, seatNumber);

                    if (luxuryBooking.getPassenger() == null) {
                        luxuryBooking.setTrip(trip);
                        luxuryBooking.setSeatno(seatNumber);
                        luxuryBooking.setStarting_stop(ticketBooking.getStart());
                        luxuryBooking.setEnding_stop(ticketBooking.getEnd());
                        luxuryBooking.setPassenger(
                                passengerRepository.findById(passenger.getId())
                                        .orElse(passengerRepository.save(new Passenger(passenger.getName(), passenger.getAge(), currentUser.getId()))));
                        luxuryBooking.setAmount(trip.getAmount());
                        luxuryBooking.setDate(Date.valueOf(LocalDate.now()));
                        luxuryBooking.setStatus("BOOKED");

                        bookingRepository.save(luxuryBooking);
                        totalAmount += luxuryBooking.getAmount();
                    } else {
                        throw new Exception("Seat " + seatNumber + " is already booked.");
                    }
                }
            } else {
                throw new Exception("Seat " + seatNumber + " is unavailable.");
            }
        }

        // Create Razorpay order after calculating total amount
        // Create Razorpay order after calculating total amount
        Order order = paymentService.bookedTicket(totalAmount);
        razorpayOrderId = order.get("id");

        User user = userDao.findByEmail(JwtAuthenticationFilter.CURRENT_USER);
        Transaction transaction = paymentService.saveTransaction(razorpayOrderId, totalAmount,  "INR", "created", user);

        // Update Razorpay booking ID in each booking
        for (Passenger_details passenger : ticketBooking.getPassengers()) {
            Booking booking = bookingRepository.findByTripAndSeat(trip, passenger.getSeat_no());
            if (booking != null && booking.getPassenger() != null) {
                booking.setRazorpay_booking_id(razorpayOrderId);
                bookingRepository.save(booking);
            }
        }

        return "BOOKED";
    }


    // Helper method to calculate the amount based on the difference between last and first stop
    private Double setAmount(Trip trip, String start, String end) {
        return ordinaryTripRepository.findAmountByStopName(end, trip) -
                ordinaryTripRepository.findAmountByStopName(start, trip);
    }


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
        return passengerRepository.findAllByUser(byEmail.getId());
    }

    public JourneyDetails getdetails(Trip trip, String stopName, Integer remaining) {
        List<Booking> bookings= bookingRepository.findAllByTrip(trip);
        int in = 0, out = 0,bookedSeatsCount=0;
        for (Booking b: bookings){
            if(b.getStarting_stop()==stopName){
                in++;
            } else if (b.getEnding_stop()==stopName) {
                out++;
            }
        }
        remaining+=in;
        remaining-=out;
        return new JourneyDetails(stopName,in,out,remaining,bookedSeatsCount);

    }

    public Integer getpassengercount(Trip t) {
        int response=0;
        List<Booking> bookings= bookingRepository.findAllByTrip(t);
        for(Booking b: bookings){
            if("BOOKED".equals(b.getStatus())){
                response++;
            }
        }
        return  response;
    }
}




