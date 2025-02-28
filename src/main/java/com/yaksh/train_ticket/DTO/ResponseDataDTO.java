package com.yaksh.train_ticket.DTO;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yaksh.train_ticket.enums.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDataDTO {
    private boolean status;
    private ResponseStatus responseStatus;

    private String message;
    private Object data;

    public ResponseDataDTO(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
    public ResponseDataDTO(boolean status,ResponseStatus responseStatus, String message) {
        this.responseStatus=responseStatus;
        this.status = status;
        this.message = message;
    }
    public ResponseDataDTO(boolean status, String message,Object data) {
        this.data=data;
        this.status = status;
        this.message = message;
    }
}
