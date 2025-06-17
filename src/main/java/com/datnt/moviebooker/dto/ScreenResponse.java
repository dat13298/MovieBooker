package com.datnt.moviebooker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScreenResponse {
    private Long id;
    private String name;
    private String theaterName;

}
