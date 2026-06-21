package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReservationReportResponseDto {

    private LocalDate from;
    private LocalDate to;

    private long total;
    private long confirmed;
    private long checkedIn;
    private long checkedOut;
    private long cancelled;
}
