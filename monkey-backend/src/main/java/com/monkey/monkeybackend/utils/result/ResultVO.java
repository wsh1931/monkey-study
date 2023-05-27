package com.monkey.monkeybackend.utils.result;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO {
    private int code;
    private String msg;
    private Object data;
}
