package com.yaksh.train_ticket.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDataDTO {
    private boolean status;
    private String message;
    private Object data;

    public ResponseDataDTO(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}
