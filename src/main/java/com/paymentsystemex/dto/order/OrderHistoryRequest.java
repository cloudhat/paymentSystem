package com.paymentsystemex.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@Getter
@Setter
public class OrderHistoryRequest {
    @Builder
    public OrderHistoryRequest(LocalDateTime createAtStartDate, LocalDateTime createAtEndDate, Integer pageNum, Boolean descending) {

        if(createAtStartDate != null){
            this.createAtStartDate = createAtStartDate;
        }
        if(createAtEndDate != null){
            this.createAtEndDate = createAtEndDate;
        }
        if(descending != null){
            this.descending = descending;
        }
        if(pageNum != null){
            this.pageNum = pageNum;
        }

        if (createAtStartDate.isBefore(MAX_SEARCH_DATE) || createAtEndDate.isBefore(MAX_SEARCH_DATE)) {
            throw new IllegalArgumentException("Start date or end date cannot be earlier than two years ago");
        }

        if (createAtStartDate.isAfter(createAtEndDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    public static final int PAGE_SIZE = 10;
    private static final LocalDateTime MAX_SEARCH_DATE = LocalDateTime.now().minusYears(2);

    private LocalDateTime createAtStartDate = LocalDateTime.now().minusMonths(1);
    private LocalDateTime createAtEndDate = LocalDateTime.now();
    private Integer pageNum = 0;
    private Boolean descending = true;

    public PageRequest getPageRequest() {


        return PageRequest.of(this.pageNum, PAGE_SIZE);
    }

}
