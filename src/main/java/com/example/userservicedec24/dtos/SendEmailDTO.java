package com.example.userservicedec24.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEmailDTO {
    private String to;
    private String from;
    private String subject;
    private String body;
}
