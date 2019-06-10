package com.koerber.task;

import com.koerber.task.model.Booking;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class App  {
    public static String INPUT_FILE_NAME = "input.txt";
    public static String OUTPUT_FILE_NAME = "output.txt";

    public static void main( String[] args ) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    INPUT_FILE_NAME));
            ArrayList<Booking> bookingList = parseBookingsInput(reader);
            reader.close();

            TreeMap<LocalDate, ArrayList<Booking>> bookingsMap = createMeetingsMap(bookingList);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(OUTPUT_FILE_NAME), "utf-8"));
            writeOutput(writer, bookingsMap);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.print(INPUT_FILE_NAME + " not found in current folder.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: move logic to services and create toString for Booking
        //TODO: tests
    }

    static ArrayList<Booking> parseBookingsInput(BufferedReader reader) throws IOException {
        ArrayList<Booking> bookingList = new ArrayList<>();
        String line = reader.readLine();

        LocalTime openingHour = parseTimeString(line.substring(0,4));
        LocalTime closingHour = parseTimeString(line.substring(5,9));

        line = reader.readLine();
        while (line != null) {
            String bookingLine = line;
            String meetingLine = reader.readLine();
            Booking booking = parseMeetingBooking(bookingLine,meetingLine);
            if(booking.isWithinOfficeHours(openingHour, closingHour)) {
                bookingList.add(booking);
            }
            line = reader.readLine();
        }
        //Sort inputs by submission date so the order of processing is correct
        Collections.sort(bookingList);
        return bookingList;
    }

    static TreeMap<LocalDate, ArrayList<Booking>> createMeetingsMap (ArrayList<Booking> bookingList){
        TreeMap<LocalDate, ArrayList<Booking>> bookingsMap = new TreeMap<>();
        Set<LocalDate> meetingDays = bookingList.stream()
                .map(Booking::getMeetingDay)
                .collect(Collectors.toSet());
        for(LocalDate meetingDay:meetingDays){
            bookingsMap.put(meetingDay, new ArrayList<>());
        }
        for (Booking booking:bookingList) {
            LocalDate meetingDay = booking.getMeetingDay();
            ArrayList<Booking> meetingsInThatDay = bookingsMap.get(meetingDay);
            boolean canAdd = true;
            for (Booking anotherBooking:meetingsInThatDay){
                if(booking.intervenesWith(anotherBooking)) {
                    canAdd = false;
                }
            }
            if (canAdd) {
                meetingsInThatDay.add(booking);
            }
        }
        //Sort daily meetings by start time
        for(ArrayList<Booking> daySchedule:bookingsMap.values()){
            daySchedule.sort((Comparator.comparing(Booking::getStartTime)));
        }
        return bookingsMap;
    }

    static void writeOutput(Writer writer, TreeMap<LocalDate, ArrayList<Booking>> bookingsMap) throws IOException {
        BufferedWriter output = new BufferedWriter(writer);
        for(LocalDate date: bookingsMap.keySet()){
            output.write(date.toString());
            output.newLine();
            for(Booking booking:bookingsMap.get(date)){
                output.write(booking.getStartTime()
                        + " " + booking.getFinishTime()
                        + " " + booking.getEmployeeId());
                output.newLine();
            }
        }
        output.close();
        System.out.print("Success, the meeting calendar has been saved to " + OUTPUT_FILE_NAME + "\n");
    }

    static LocalTime parseTimeString(String timeString){
        return LocalTime.of(Integer.valueOf(timeString.substring(0,2)),
                Integer.valueOf(timeString.substring(2,4)));
    }

    static Booking parseMeetingBooking(String bookingData, String meetingData){
        LocalDateTime bookingTime = LocalDateTime.parse(bookingData.substring(0,19), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String employeeId = bookingData.substring(20);
        LocalDate meetingDay = LocalDate.parse(meetingData.substring(0,10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalTime startTime = LocalTime.parse(meetingData.substring(11,16), DateTimeFormatter.ofPattern("HH:mm"));
        float durationHours = Float.valueOf(meetingData.substring(17));
        return new Booking(bookingTime,employeeId,meetingDay,startTime,durationHours);
    }
}
