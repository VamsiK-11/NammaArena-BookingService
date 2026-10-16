package com.nammaarena.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nammaarena.booking.dto.*;
import com.nammaarena.booking.enums.BookingStatus;
import com.nammaarena.booking.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void createBooking_Success() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setTurfId(1L);
        request.setSport("Football");
        request.setBookingDate(LocalDate.now().plusDays(1));
        request.setPrice(new BigDecimal("100"));

        BookingResponse response = new BookingResponse();
        response.setSport("Football");

        when(bookingService.createBooking(any(BookingRequest.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/api/bookings")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void getUserBookings_Success() throws Exception {
        when(bookingService.getUserBookings(1L)).thenReturn(Arrays.asList(new BookingResponse()));

        mockMvc.perform(get("/api/bookings/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllBookings_Success() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(Arrays.asList(new BookingResponse()));

        mockMvc.perform(get("/api/bookings/admin/all"))
                .andExpect(status().isOk());
    }

    @Test
    void getTurfBookings_Success() throws Exception {
        when(bookingService.getTurfBookings(1L)).thenReturn(Arrays.asList(new BookingResponse()));

        mockMvc.perform(get("/api/bookings/turf/1"))
                .andExpect(status().isOk());
    }
}