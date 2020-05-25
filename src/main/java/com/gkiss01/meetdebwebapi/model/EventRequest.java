package com.gkiss01.meetdebwebapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {
    private Long id;

    @NotBlank(message = "Name is required!")
    private String name;

    @NotNull(message = "Date is required!")
    private OffsetDateTime date;

    @NotBlank(message = "Venue is required!")
    private String venue;

    @NotBlank(message = "Description is required!")
    @Size(min = 5, message = "Minimum description length is 5 characters!")
    private String description;
}
