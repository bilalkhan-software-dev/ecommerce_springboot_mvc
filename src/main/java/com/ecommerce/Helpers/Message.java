package com.ecommerce.Helpers;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String content;
    private MessageType messageColorType;
}
