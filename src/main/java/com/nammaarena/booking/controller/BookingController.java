package com.nammaarena.booking.controller;

import com.nammaarena.booking.dto.*;
import com.nammaarena.booking.enums.BookingStatus;
import com.nammaarena.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BookingController {
    
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        System.out.println("Received booking request: " + request);
        try {
            Long userId = getCurrentUserId();
            System.out.println("User ID: " + userId);
            return ResponseEntity.ok(bookingService.createBooking(request, userId));
        } catch (Exception e) {
            System.err.println("Error in createBooking: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<BookingResponse>> getUserBookings() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/turf/{turfId}")
    public ResponseEntity<List<BookingResponse>> getTurfBookings(@PathVariable Long turfId) {
        return ResponseEntity.ok(bookingService.getTurfBookings(turfId));
    }

    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<OwnerBookingResponse>> getOwnerBookings() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(bookingService.getOwnerBookings(userId));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    private Long getCurrentUserId() {
        return Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(@PathVariable Long bookingId,
                                                              @RequestParam BookingStatus status,
                                                              @RequestParam(required = false) String paymentId) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(bookingId, status, paymentId));
    }

    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            long count = bookingService.getBookingCount();
            response.put("status", "success");
            response.put("bookingCount", count);
            response.put("message", "Database connection working");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}