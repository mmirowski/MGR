package dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ValuationMechanismDto {
    private int maxCapacity;
    private int freeSpaces;
    private boolean isCovered;
    private boolean isSecured;
    private boolean isSpecial;
}
