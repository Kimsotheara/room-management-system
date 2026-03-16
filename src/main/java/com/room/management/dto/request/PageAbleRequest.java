package com.room.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PageAbleRequest<T> {

    @Schema(description = "Page number (1-based index)", example = "1", defaultValue = "1")
    private int pageNumber = 1;

    @Schema(description = "Number of items per page", example = "10", defaultValue = "10")
    private int size = 10;

    @Schema(description = "Property name to sort by", example = "id", defaultValue = "id")
    private String sortProperty = "id";

    @Schema(description = "Sort direction", example = "desc", allowableValues = {"asc", "desc"}, defaultValue = "desc")
    private String sortDirection = "desc";

    @Schema(description = "Filter parameters for the query", required = true)
    @NotNull
    private T parameter;


    public Pageable getPageAble() {
        Sort sort;
        if (sortDirection.equalsIgnoreCase("asc"))
            sort = Sort.by(sortProperty).ascending();
        else
            sort = Sort.by(sortProperty).descending();

        return PageRequest.of(pageNumber > 0 ? pageNumber - 1 : 0, size, sort);
    }
}
