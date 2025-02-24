package com.yaksh.train_ticket.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDataDTO {
    private boolean status;
    private String message;
    private Object data;
}
