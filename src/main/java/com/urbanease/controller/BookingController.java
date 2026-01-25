package com.urbanease.controller;

import com.urbanease.dto.BookingRequest;
import com.urbanease.model.Booking;
import com.urbanease.model.BookingStatus;
import com.urbanease.model.User;
import com.urbanease.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management APIs")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<Booking> createBooking(
            @Valid @RequestBody BookingRequest request,
            @AuthenticationPrincipal User customer) {
        
        Booking booking = bookingService.createBooking(request, customer);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/accept")
    @Operation(summary = "Accept a booking (Provider only)")
    public ResponseEntity<Booking> acceptBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User provider) {
        
        Booking booking = bookingService.acceptBooking(bookingId, provider);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/reject")
    @Operation(summary = "Reject a booking (Provider only)")
    public ResponseEntity<Booking> rejectBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User provider) {
        
        Booking booking = bookingService.rejectBooking(bookingId, provider);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/start")
    @Operation(summary = "Start service (Provider only)")
    public ResponseEntity<Booking> startService(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User provider) {
        
        Booking booking = bookingService.startService(bookingId, provider);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/complete")
    @Operation(summary = "Complete service (Provider only)")
    public ResponseEntity<Booking> completeService(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User provider) {
        
        Booking booking = bookingService.completeService(bookingId, provider);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking (Customer only)")
    public ResponseEntity<Booking> cancelBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User customer) {
        
        Booking booking = bookingService.cancelBooking(bookingId, customer);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/my-bookings")
    @Operation(summary = "Get user's bookings")
    public ResponseEntity<List<Booking>> getMyBookings(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) BookingStatus status) {
        
        List<Booking> bookings;
        if (user.getRole().equals(com.urbanease.model.UserRole.CUSTOMER)) {
            bookings = bookingService.getCustomerBookings(user, status);
        } else if (user.getRole().equals(com.urbanease.model.UserRole.SERVICE_PROVIDER)) {
            bookings = bookingService.getProviderBookings(user, status);
        } else {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking details")
    public ResponseEntity<Booking> getBooking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal User user) {
        
        Booking booking = bookingService.getBookingById(bookingId, user);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending bookings (Provider only)")
    public ResponseEntity<List<Booking>> getPendingBookings(@AuthenticationPrincipal User provider) {
        
        if (!provider.getRole().equals(com.urbanease.model.UserRole.SERVICE_PROVIDER)) {
            return ResponseEntity.badRequest()
                    .body(null);
        }
        
        List<Booking> pendingBookings = bookingService.getProviderBookings(provider, BookingStatus.PENDING);
        return ResponseEntity.ok(pendingBookings);
    }
}
