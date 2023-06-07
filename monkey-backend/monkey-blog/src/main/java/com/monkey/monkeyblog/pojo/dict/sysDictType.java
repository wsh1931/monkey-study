package com.monkey.monkeyblog.pojo.dict;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class sysDictType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String dictName;
    private String dictType;
    private Integer status;
}
