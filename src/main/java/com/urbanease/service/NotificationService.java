package com.urbanease.service;

import com.urbanease.model.Booking;
import com.urbanease.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void notifyProviderAboutNewBooking(User provider, Booking booking) {
        log.info("Notifying provider {} about new booking {}: {}", 
                provider.getId(), booking.getId(), booking.getService().getName());
        
    }

    public void notifyCustomerAboutBookingAcceptance(User customer, Booking booking) {
        log.info("Notifying customer {} about booking acceptance: {}", 
                customer.getId(), booking.getId());
        
    }

    public void notifyCustomerAboutBookingRejection(User customer, Booking booking) {
        log.info("Notifying customer {} about booking rejection: {}", 
                customer.getId(), booking.getId());
        
    }

    public void notifyCustomerAboutServiceStart(User customer, Booking booking) {
        log.info("Notifying customer {} about service start: {}", 
                customer.getId(), booking.getId());
        
    }

    public void notifyCustomerAboutServiceCompletion(User customer, Booking booking) {
        log.info("Notifying customer {} about service completion: {}", 
                customer.getId(), booking.getId());
        
    }

    public void notifyProviderAboutBookingCancellation(User provider, Booking booking) {
        log.info("Notifying provider {} about booking cancellation: {}", 
                provider.getId(), booking.getId());
        
    }
}
