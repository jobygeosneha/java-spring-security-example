package io.example.domain.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PageRequest {

    @Min(value = 1, message = "Paging must start with page 1")
    private long page;
    @Min(value = 1, message = "You can request minimum 1 records")
    @Max(value = 100, message = "You can request maximum 100 records")
    private long limit;

}
