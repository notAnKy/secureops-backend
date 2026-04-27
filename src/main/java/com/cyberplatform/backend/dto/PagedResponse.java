package com.cyberplatform.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> {

    private List<T> content;       // the actual data for this page
    private int currentPage;       // 0-based page index
    private int totalPages;        // total number of pages
    private long totalElements;    // total number of records
    private int size;              // page size (records per page)
    private boolean first;         // is this the first page?
    private boolean last;          // is this the last page?
}