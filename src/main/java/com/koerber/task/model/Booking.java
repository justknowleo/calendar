package com.koerber.task.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.MINUTES;

@Getter
public class Booking implements Comparable<Booking> {
    private LocalDateTime bookingTime;
    private String employeeId;
    private LocalDate meetingDay;
    private LocalTime startTime;
    private LocalTime finishTime;
    private short durationMinutes;

    public Booking(
            LocalDateTime bookingTime,
            String employeeId,
            LocalDate meetingDay,
            LocalTime startTime,
            float durationHours
    ){
        this.bookingTime = bookingTime;
        this.employeeId = employeeId;
        this.meetingDay = meetingDay;
        this.startTime = startTime;
        this.durationMinutes = (short) (durationHours*60);
        this.finishTime = startTime.plusMinutes(this.durationMinutes);
    }

    public boolean isWithinOfficeHours(LocalTime openingHour,
                   LocalTime closingHour){
                return !openingHour.isAfter(startTime) &&
                        !closingHour.isBefore(finishTime) &&
                        durationMinutes< MINUTES.between(openingHour, closingHour);
    }
    public boolean intervenesWith(Booking anotherBooking){
        return (this.startTime.isBefore(anotherBooking.finishTime) && (this.finishTime.isAfter(anotherBooking.startTime)));
    }

    @Override
    public int compareTo(Booking obj) {
        return bookingTime.compareTo(obj.bookingTime);
    }

    @Override
    public boolean equals(Object obj) { if (this == obj)
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Booking other = (Booking) obj;
        if (!bookingTime.equals(other.bookingTime))
            return false;
        if (!employeeId.equals(other.employeeId))
            return false;
        if (!meetingDay.equals(other.meetingDay))
            return false;
        if (!startTime.equals(other.startTime))
            return false;
        if (durationMinutes != other.durationMinutes)
            return false;
        return true;
    }
}
