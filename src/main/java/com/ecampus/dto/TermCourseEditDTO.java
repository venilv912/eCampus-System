package com.ecampus.dto;

public record TermCourseEditDTO(
        Long tcrid,
        Long tcrcrsid,
        String crscode,
        String crstitle,
        String credithours,
        Long tcrmarks,
        Long tcrfacultyid,
        String facultyName,
        Long tcrslot,
        String crstype
) {}
