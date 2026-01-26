package com.ecampus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class RegistrationDeadlineConfig {

    @Value("${registration.deadline.start}")
    private String start;

    @Value("${registration.deadline.end}")
    private String end;

    @Value("${addDrop.deadline.start}")
    private String startAddDrop;

    @Value("${addDrop.deadline.end}")
    private String endAddDrop;

    @Value("${drop.deadline.start}")
    private String startDrop;

    @Value("${drop.deadline.end}")
    private String endDrop;

    public LocalDate getStartDate() {
        return LocalDate.parse(start);
    }

    public LocalDate getEndDate() {
        return LocalDate.parse(end);
    }

    public LocalDate getAddDropStartDate() {
        return LocalDate.parse(startAddDrop);
    }

    public LocalDate getAddDropEndDate() {
        return LocalDate.parse(endAddDrop);
    }

    public LocalDate getDropStartDate() {
        return LocalDate.parse(startDrop);
    }

    public LocalDate getDropEndDate() {
        return LocalDate.parse(endDrop);
    }

    public boolean isWithinDeadline() {
        LocalDate today = LocalDate.now();
        return (today.isEqual(getStartDate()) || today.isAfter(getStartDate()))
                && (today.isEqual(getEndDate()) || today.isBefore(getEndDate()));
    }

    public boolean addDropisWithinDeadline() {
        LocalDate today = LocalDate.now();
        return (today.isEqual(getAddDropStartDate()) || today.isAfter(getAddDropStartDate()))
                && (today.isEqual(getAddDropEndDate()) || today.isBefore(getAddDropEndDate()));
    }

    public boolean dropisWithinDeadline() {
        LocalDate today = LocalDate.now();
        return (today.isEqual(getDropStartDate()) || today.isAfter(getDropStartDate()))
                && (today.isEqual(getDropEndDate()) || today.isBefore(getDropEndDate()));
    }
}