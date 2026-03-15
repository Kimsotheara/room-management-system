package com.room.management.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ImageDataRequestDto {

    @NotEmpty(message = "At least one image is required")
    private List<String> images;
}
