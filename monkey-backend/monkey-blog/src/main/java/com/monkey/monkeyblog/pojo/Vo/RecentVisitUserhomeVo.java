package com.monkey.monkeyblog.pojo.Vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentVisitUserhomeVo {
    private Long id;
    private Long visitId;
    private String visitUsername;
    private String visitUserPhoto;
    private Date visitTime;
}
