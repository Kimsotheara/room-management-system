package com.room.management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PageAbleResponse<T, D, S> {

    public PageAbleResponse(Page<T> page, List<D> list) {
        this(page, list, null);
    }

    public PageAbleResponse(Page<T> page, List<D> list, S addition) {
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.pageNumber = page.getNumber();
        this.row = list.size();
        this.list = list;
        this.addition = addition;

        Sort sort = page.getSort();
        for (Sort.Order order : sort) {
            this.sortProperty = order.getProperty();
            this.sortDirection = order.getDirection().isDescending() ? Sort.Direction.DESC.name() : Sort.Direction.ASC.name();
        }
    }

    // Factory method for responses without static data
    public static <T, D> PageAbleResponse<T, D, Void> withoutAddition(Page<T> page, List<D> list) {
        return new PageAbleResponse<>(page, list);
    }

    // Factory method for responses with static data
    public static <T, D, S> PageAbleResponse<T, D, S> withAddition(Page<T> page, List<D> list, S statics) {
        return new PageAbleResponse<>(page, list, statics);
    }

    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
    private int pageNumber;
    private int row;
    private String sortProperty;
    private String sortDirection;
    private List<D> list = new ArrayList<>();

    // Only include statics in JSON response if it's not null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private S addition;

    public int getPageNumber() {
        return pageNumber + 1; // Adjusting to 1-based index for API consumers
    }
}

