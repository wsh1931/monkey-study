package com.monkey.monkeyUtils.pojo.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class sysDictData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer dictSort;
    private String dictLabel;
    private Integer dictValue;
    private String dictType;
    private Integer status;
}