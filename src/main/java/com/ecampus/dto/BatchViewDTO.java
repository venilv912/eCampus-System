package com.ecampus.dto;

public record BatchViewDTO(
        Long bchid,
        String ayrname,
        String prgname,
        String bchname,
        Integer bchcapacity,
        Integer schemeyear
) {}
