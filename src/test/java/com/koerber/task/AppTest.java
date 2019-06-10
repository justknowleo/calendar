package com.koerber.task;

import static com.koerber.task.App.OUTPUT_FILE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.koerber.task.model.Booking;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    private final Booking TEST_BOOKING =  new Booking(LocalDateTime.of(LocalDate.of(2010,10,10),
            LocalTime.of(10,0)),
            "TEST",
            LocalDate.of(2010,10,20),
            LocalTime.of(9,0),
            2);
    private final Booking TEST_BOOKING_2 =  new Booking(LocalDateTime.of(LocalDate.of(2010,10,9),
            LocalTime.of(11,0)),
            "TEST2",
            LocalDate.of(2010,10,20),
            LocalTime.of(13,0),
            3);

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void parseBookingsInputShouldReturnCorrectBookingsList() throws IOException {
        ArrayList<Booking> expected = new ArrayList<>();
        expected.add(TEST_BOOKING);

        ArrayList<Booking> result = App.parseBookingsInput(new BufferedReader(new StringReader(String.format(
                "0900 1400%n" +
                "2010-10-10 10:00:00 TEST%n" +
                "2010-10-20 09:00 2%n" +
                "2010-10-09 11:00:00 TEST2%n" +
                "2010-10-20 13:00 3%n"))));

        assertEquals(result, expected);
    }

    @Test
    public void createMeetingsMapShouldCreateSortedMeetingsMap() throws IOException {
        ArrayList<Booking> bookings = new ArrayList<>();
        bookings.add(TEST_BOOKING_2);
        bookings.add(TEST_BOOKING);

        TreeMap resultMap = App.createMeetingsMap(bookings);

        ArrayList<Booking> resultBookings = (ArrayList<Booking>) resultMap.get(TEST_BOOKING.getMeetingDay());
        assertEquals(resultBookings.size(), 2);
        assertEquals(resultBookings.get(0), TEST_BOOKING);
        assertEquals(resultBookings.get(1), TEST_BOOKING_2);
    }

    @Test
    public void writeOutputShouldWriteToOutputFile() throws IOException {
        StringWriter stringWriter = new StringWriter();
        TreeMap<LocalDate, ArrayList<Booking>> bookingsMap = new TreeMap<>();
        ArrayList<Booking> entry = new ArrayList<>();
        entry.add(TEST_BOOKING);
        bookingsMap.put(TEST_BOOKING.getMeetingDay(), entry);
        String result =  String.format("2010-10-20%n" +
                "09:00 11:00 TEST%n");

        App.writeOutput(stringWriter, bookingsMap);

        assertEquals(stringWriter.toString(), result);
        assertEquals("Success, the meeting calendar has been saved to " + OUTPUT_FILE_NAME + "\n", outContent.toString());
    }

    @Test
    public void parseTimeStringShouldReturnCorrectTime() {
        String inputString = "0845";
        LocalTime expected = LocalTime.of(8,45);

        LocalTime result = App.parseTimeString(inputString);

        assertEquals(expected, result);
    }

    @Test
    public void parseMeetingBookingShouldReturnBooking() {
        String bookingString = "2010-10-10 10:00:00 TEST";
        String meetingString = "2010-10-20 09:00 2";
        Booking expected = TEST_BOOKING;

        Booking result = App.parseMeetingBooking(bookingString, meetingString);

        assertEquals(expected, result);
    }
}
