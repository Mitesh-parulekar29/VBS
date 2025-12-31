package com.vbs.demo.Dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayDto
{
    String username;
    double balance;

    public void setName(String name) {

    }
}
