package com.urbanease.service;

import com.urbanease.dto.BookingRequest;
import com.urbanease.model.Booking;
import com.urbanease.model.BookingStatus;
import com.urbanease.model.Service;
import com.urbanease.model.User;
import com.urbanease.model.UserRole;
import com.urbanease.repository.BookingRepository;
import com.urbanease.repository.ServiceRepository;
import com.urbanease.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final NotificationService notificationService;

    @Transactional
    public Booking createBooking(BookingRequest request, User customer) {
        User provider = userRepository.findById(request.getProviderId())
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        if (!provider.getRole().equals(UserRole.SERVICE_PROVIDER)) {
            throw new RuntimeException("Selected user is not a service provider");
        }

        if (!provider.isAvailable()) {
            throw new RuntimeException("Provider is not available at the moment");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setProvider(provider);
        booking.setService(service);
        booking.setServiceLocation(request.getServiceLocation());
        booking.setScheduledTime(request.getScheduledTime());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setDescription(request.getDescription());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notifyProviderAboutNewBooking(provider, savedBooking);

        log.info("Created new booking: {} for customer: {} with provider: {}", 
                savedBooking.getId(), customer.getId(), provider.getId());

        return savedBooking;
    }

    @Transactional
    public Booking acceptBooking(Long bookingId, User provider) {
        Booking booking = bookingRepository.findByIdAndProvider(bookingId, provider)
                .orElseThrow(() -> new RuntimeException("Booking not found or you are not the provider"));

        if (!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new RuntimeException("Booking cannot be accepted in current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setStartTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notifyCustomerAboutBookingAcceptance(booking.getCustomer(), savedBooking);

        log.info("Provider {} accepted booking: {}", provider.getId(), bookingId);
        return savedBooking;
    }

    @Transactional
    public Booking rejectBooking(Long bookingId, User provider) {
        Booking booking = bookingRepository.findByIdAndProvider(bookingId, provider)
                .orElseThrow(() -> new RuntimeException("Booking not found or you are not the provider"));

        if (!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new RuntimeException("Booking cannot be rejected in current status: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notifyCustomerAboutBookingRejection(booking.getCustomer(), savedBooking);

        log.info("Provider {} rejected booking: {}", provider.getId(), bookingId);
        return savedBooking;
    }

    @Transactional
    public Booking startService(Long bookingId, User provider) {
        Booking booking = bookingRepository.findByIdAndProvider(bookingId, provider)
                .orElseThrow(() -> new RuntimeException("Booking not found or you are not the provider"));

        if (!booking.getStatus().equals(BookingStatus.ACCEPTED)) {
            throw new RuntimeException("Service can only be started from ACCEPTED status");
        }

        booking.setStatus(BookingStatus.IN_PROGRESS);
        booking.setStartTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notifyCustomerAboutServiceStart(booking.getCustomer(), savedBooking);

        log.info("Provider {} started service for booking: {}", provider.getId(), bookingId);
        return savedBooking;
    }

    @Transactional
    public Booking completeService(Long bookingId, User provider) {
        Booking booking = bookingRepository.findByIdAndProvider(bookingId, provider)
                .orElseThrow(() -> new RuntimeException("Booking not found or you are not the provider"));

        if (!booking.getStatus().equals(BookingStatus.IN_PROGRESS)) {
            throw new RuntimeException("Service can only be completed from IN_PROGRESS status");
        }

        booking.setStatus(BookingStatus.COMPLETED);
        booking.setEndTime(LocalDateTime.now());

        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notifyCustomerAboutServiceCompletion(booking.getCustomer(), savedBooking);

        log.info("Provider {} completed service for booking: {}", provider.getId(), bookingId);
        return savedBooking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId, User customer) {
        Booking booking = bookingRepository.findByIdAndCustomer(bookingId, customer)
                .orElseThrow(() -> new RuntimeException("Booking not found or you are not the customer"));

        if (booking.getStatus().equals(BookingStatus.COMPLETED)) {
            throw new RuntimeException("Cannot cancel completed booking");
        }

        if (booking.getStatus().equals(BookingStatus.IN_PROGRESS)) {
            throw new RuntimeException("Cannot cancel booking that is in progress");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking savedBooking = bookingRepository.save(booking);

        notificationService.notifyProviderAboutBookingCancellation(booking.getProvider(), savedBooking);

        log.info("Customer {} cancelled booking: {}", customer.getId(), bookingId);
        return savedBooking;
    }

    public List<Booking> getCustomerBookings(User customer, BookingStatus status) {
        if (status != null) {
            return bookingRepository.findByCustomerAndStatus(customer, status);
        }
        return bookingRepository.findByCustomer(customer);
    }

    public List<Booking> getProviderBookings(User provider, BookingStatus status) {
        if (status != null) {
            return bookingRepository.findByProviderAndStatus(provider, status);
        }
        return bookingRepository.findByProvider(provider);
    }

    public Booking getBookingById(Long bookingId, User user) {
        if (user.getRole().equals(UserRole.CUSTOMER)) {
            return bookingRepository.findByIdAndCustomer(bookingId, user)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
        } else if (user.getRole().equals(UserRole.SERVICE_PROVIDER)) {
            return bookingRepository.findByIdAndProvider(bookingId, user)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));
        }
        throw new RuntimeException("Unauthorized access");
    }
}
