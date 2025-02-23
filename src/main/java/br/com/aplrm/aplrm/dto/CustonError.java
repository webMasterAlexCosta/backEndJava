package br.com.aplrm.aplrm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter@NoArgsConstructor@AllArgsConstructor
public class CustonError {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;
    private String trace;



}
