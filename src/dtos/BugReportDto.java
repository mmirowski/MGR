package dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class BugReportDto implements Serializable {

    private double bugReportIDNumber;
    private String subject;
    private String message;
    private String bugReporter;

}
