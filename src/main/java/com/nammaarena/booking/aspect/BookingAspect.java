package com.nammaarena.booking.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class BookingAspect {

    @Before("execution(* com.nammaarena.booking.service.BookingService.createBooking(..))")
    public void auditBookingCreation(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2) {
            log.info("BOOKING_AUDIT: Booking creation by user ID: {}", args[1]);
        }
    }

    @Before("execution(* com.nammaarena.booking.service.BookingService.updateBookingStatus(..))")
    public void auditBookingStatusChange(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length >= 2) {
            log.info("BOOKING_AUDIT: Status change - Booking ID: {}, Status: {}", args[0], args[1]);
        }
    }

    @Around("execution(* com.nammaarena.booking.service.BookingService.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("BOOKING_PERFORMANCE: {} completed in {} ms", joinPoint.getSignature().toShortString(), executionTime);
            return result;
        } catch (Throwable throwable) {
            log.error("BOOKING_ERROR: {} failed - {}", joinPoint.getSignature().toShortString(), throwable.getMessage());
            throw throwable;
        }
    }
}