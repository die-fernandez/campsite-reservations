package dto;

import com.pacific.volcano.campsitereservations.api.DateRangeValidatable;
import com.pacific.volcano.campsitereservations.validation.ValidAvailabilityRange;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@ValidAvailabilityRange
public class DateRangeDto implements DateRangeValidatable {
    private LocalDate from;
    private LocalDate to;

}
