package com.ecampus.dto;

public record BatchViewDTO(
        Long bchid,
        String ayrname,
        String spldesc,
        String bchname,
        Integer bchcapacity,
        Integer schemeyear
) {}
