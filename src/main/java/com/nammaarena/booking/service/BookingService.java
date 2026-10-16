package com.nammaarena.booking.service;

import com.nammaarena.booking.client.TurfServiceClient;
import com.nammaarena.booking.dto.*;
import com.nammaarena.booking.entity.Booking;
import com.nammaarena.booking.enums.BookingStatus;
import com.nammaarena.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final TurfServiceClient turfServiceClient;

    @Transactional
    public BookingResponse createBooking(BookingRequest request, Long userId) {
        System.out.println("Creating booking for user: " + userId);
        System.out.println("Request: " + request);
        
        // Validate booking date is not in the past
        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot book in the past");
        }
        
        // Simple validation - check for existing bookings on same turf, sport, and date
        List<Booking> existingBookings = bookingRepository.findByTurfIdAndSportAndBookingDate(
            request.getTurfId(), 
            request.getSport(), 
            request.getBookingDate()
        );
        
        System.out.println("Found " + existingBookings.size() + " existing bookings for this turf/sport/date");
        
        // Check for time conflicts manually
        boolean hasConflict = existingBookings.stream().anyMatch(booking -> 
            isTimeOverlap(request.getStartTime(), request.getEndTime(), 
                         booking.getStartTime(), booking.getEndTime())
        );
        
        if (hasConflict) {
            throw new RuntimeException("Time slot already booked for this turf and sport");
        }
        
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setTurfId(request.getTurfId());
        booking.setSport(request.getSport());
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        
        // Calculate duration automatically
        int duration = calculateDuration(request.getStartTime(), request.getEndTime());
        booking.setDuration(duration);
        booking.setPrice(request.getPrice());
        booking.setStatus(BookingStatus.PENDING);
        System.out.println("Booking created with PENDING status");
        
        booking = bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    public List<BookingResponse> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<BookingResponse> getTurfBookings(Long turfId) {
        return bookingRepository.findByTurfId(turfId)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<OwnerBookingResponse> getOwnerBookings(Long ownerId) {
        try {
            System.out.println("Fetching owner bookings for owner ID: " + ownerId);
            
            List<Object[]> results = bookingRepository.findOwnerBookingsWithDetails(ownerId);
            System.out.println("Found " + results.size() + " bookings for owner");
            
            return results.stream()
                .map(this::mapToOwnerBookingResponse)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching owner bookings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch owner bookings: " + e.getMessage());
        }
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public long getBookingCount() {
        return bookingRepository.count();
    }

    @Transactional
    public BookingResponse updateBookingStatus(Long bookingId, BookingStatus status, String paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus(status);
        if (paymentId != null) {
            booking.setPaymentId(paymentId);
        }
        
        booking = bookingRepository.save(booking);
        return mapToResponse(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        bookingRepository.delete(booking);
    }

    private boolean isTimeOverlap(String start1, String end1, String start2, String end2) {
        try {
            int start1Minutes = timeToMinutes(start1);
            int end1Minutes = timeToMinutes(end1);
            int start2Minutes = timeToMinutes(start2);
            int end2Minutes = timeToMinutes(end2);
            
            return start1Minutes < end2Minutes && end1Minutes > start2Minutes;
        } catch (Exception e) {
            return false;
        }
    }
    
    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
    
    private int calculateDuration(String startTime, String endTime) {
        try {
            int startMinutes = timeToMinutes(startTime);
            int endMinutes = timeToMinutes(endTime);
            
            int durationMinutes = endMinutes - startMinutes;
            return Math.max(1, (int) Math.ceil(durationMinutes / 60.0)); // Round up to nearest hour
        } catch (Exception e) {
            return 1; // Default to 1 hour if calculation fails
        }
    }

    private BookingResponse mapToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setTurfId(booking.getTurfId());
        response.setSport(booking.getSport());
        response.setBookingDate(booking.getBookingDate());
        response.setStartTime(booking.getStartTime());
        response.setEndTime(booking.getEndTime());
        response.setDuration(booking.getDuration());
        response.setPrice(booking.getPrice());
        response.setStatus(booking.getStatus());
        response.setPaymentId(booking.getPaymentId());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
    
    private OwnerBookingResponse mapToOwnerBookingResponse(Object[] row) {
        OwnerBookingResponse response = new OwnerBookingResponse();
        response.setId(((Number) row[0]).longValue());
        response.setUserId(((Number) row[1]).longValue());
        response.setUserName((String) row[2]);
        response.setTurfId(((Number) row[3]).longValue());
        response.setTurfName((String) row[4]);
        response.setSport((String) row[5]);
        response.setBookingDate(((java.sql.Date) row[6]).toLocalDate());
        response.setStartTime((String) row[7]);
        response.setEndTime((String) row[8]);
        response.setDuration((Integer) row[9]);
        response.setPrice((BigDecimal) row[10]);
        response.setStatus(BookingStatus.valueOf((String) row[11]));
        response.setPaymentId((String) row[12]);
        response.setCreatedAt(((java.sql.Timestamp) row[13]).toLocalDateTime());
        return response;
    }
}