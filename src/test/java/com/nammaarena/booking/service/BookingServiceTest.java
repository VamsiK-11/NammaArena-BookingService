package com.nammaarena.booking.service;

import com.nammaarena.booking.client.TurfServiceClient;
import com.nammaarena.booking.dto.BookingRequest;
import com.nammaarena.booking.entity.Booking;
import com.nammaarena.booking.enums.BookingStatus;
import com.nammaarena.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TurfServiceClient turfServiceClient;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        testBooking = new Booking();
        testBooking.setId(1L);
        testBooking.setUserId(1L);
        testBooking.setTurfId(1L);
        testBooking.setSport("Football");
        testBooking.setBookingDate(LocalDate.now().plusDays(1));
        testBooking.setStartTime("10:00");
        testBooking.setEndTime("12:00");
        testBooking.setPrice(new BigDecimal("100"));
        testBooking.setStatus(BookingStatus.PENDING);

        bookingRequest = new BookingRequest();
        bookingRequest.setTurfId(1L);
        bookingRequest.setSport("Football");
        bookingRequest.setBookingDate(LocalDate.now().plusDays(1));
        bookingRequest.setStartTime("10:00");
        bookingRequest.setEndTime("12:00");
        bookingRequest.setPrice(new BigDecimal("100"));
    }

    @Test
    void createBooking_Success() {
        when(bookingRepository.findByTurfIdAndSportAndBookingDate(any(), any(), any()))
            .thenReturn(Arrays.asList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        var response = bookingService.createBooking(bookingRequest, 1L);

        assertNotNull(response);
        assertEquals("Football", response.getSport());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_PastDate_ThrowsException() {
        bookingRequest.setBookingDate(LocalDate.now().minusDays(1));

        assertThrows(RuntimeException.class, 
            () -> bookingService.createBooking(bookingRequest, 1L));
    }

    @Test
    void getUserBookings_Success() {
        when(bookingRepository.findByUserId(1L)).thenReturn(Arrays.asList(testBooking));

        var response = bookingService.getUserBookings(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void updateBookingStatus_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        var response = bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED, null);

        assertNotNull(response);
        assertEquals(BookingStatus.CONFIRMED, testBooking.getStatus());
    }

    @Test
    void getOwnerBookings_Success() {
        Object[] mockRow = {
            1L, 1L, "User Name", 1L, "Turf Name", "Football",
            Date.valueOf(LocalDate.now()), "10:00", "12:00", 2,
            new BigDecimal("100"), "PENDING", "payment123",
            Timestamp.valueOf(LocalDateTime.now())
        };
        
        when(bookingRepository.findOwnerBookingsWithDetails(1L))
            .thenReturn(Collections.singletonList(mockRow));

        var response = bookingService.getOwnerBookings(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void deleteBooking_Success() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(testBooking);
    }

    @Test
    void deleteBooking_NotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.deleteBooking(1L));
    }

    @Test
    void getTurfBookings_Success() {
        when(bookingRepository.findByTurfId(1L)).thenReturn(Arrays.asList(testBooking));

        var response = bookingService.getTurfBookings(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getAllBookings_Success() {
        when(bookingRepository.findAll()).thenReturn(Arrays.asList(testBooking));

        var response = bookingService.getAllBookings();

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBookingCount_Success() {
        when(bookingRepository.count()).thenReturn(5L);

        long count = bookingService.getBookingCount();

        assertEquals(5L, count);
    }

    @Test
    void updateBookingStatus_NotFound_ThrowsException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED, null));
    }

    @Test
    void createBooking_TimeConflict_ThrowsException() {
        Booking conflictingBooking = new Booking();
        conflictingBooking.setStartTime("11:00");
        conflictingBooking.setEndTime("13:00");
        
        when(bookingRepository.findByTurfIdAndSportAndBookingDate(any(), any(), any()))
            .thenReturn(Arrays.asList(conflictingBooking));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(bookingRequest, 1L));
    }
}