package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BugReportDto {

    private double bugReportIDNumber;
    private String subject;
    private String message;
    private String bugReporter;

}
