package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.mapper.FormTypeMapper;
import com.monkey.monkeyUtils.pojo.FormType;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeycourse.service.CourseContentManageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/12/22 20:58
 * @version: 1.0
 * @description:
 */
@Service
public class CourseContentManageServiceImpl implements CourseContentManageService {
    @Resource
    private FormTypeMapper formTypeMapper;
    /**
     * 查询形式类型列表
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/12/22 21:01
     */
    @Override
    public R queryFormType() {
        QueryWrapper<FormType> formTypeQueryWrapper = new QueryWrapper<>();
        formTypeQueryWrapper.orderByAsc("sort");
        List<FormType> formTypes = formTypeMapper.selectList(formTypeQueryWrapper);
        Iterator<FormType> iterator = formTypes.listIterator();
        // 删除全部
        while (iterator.hasNext()) {
            FormType next = iterator.next();
            if (FormTypeEnum.FORM_TYPE_ALL.getCode().equals(next.getId())) {
                iterator.remove();
            }
        }
        return R.ok(formTypes);
    }
}
