package learn.springboot.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private String fromName;
    private String toName;
    private Integer amount;
}
