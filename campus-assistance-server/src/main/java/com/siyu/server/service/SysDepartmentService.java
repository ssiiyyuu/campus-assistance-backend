package com.siyu.server.service;

import com.siyu.common.domain.entity.SysDepartment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.common.enums.DepartmentLevel;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import org.springframework.util.StringUtils;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:57
 */
public interface SysDepartmentService extends IService<SysDepartment> {

    /**
     * 获取上级部门代码
     */
    static String getParentCode(String code) {
        char[] chars = code.toCharArray();
        int searchEndIndex = chars.length-2;
        int i = searchEndIndex;
        for (; i >= 0; i--) {
            if (chars[i] == '-') {
                break;
            }
        }
        return i == searchEndIndex || i <=0 ? "" : code.substring(0, i+1);
    }

    /**
     * 获取部门级别
     */
    static int getLevel(String code) {
        if (!StringUtils.hasText(code)) {
            return 0;
        }
        char[] chars = code.toCharArray();
        int level = 0;
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == '-') {
                level++;
            }
        }
        return level;
    }

    /**
     * 根据给定level获取部门编码的指定level编码
     */
    static String getLevelCode(String code, int level) {
        int level1 = getLevel(code);
        if (level1 < level) {
            throw new BusinessException(ErrorStatus.SYS_ERROR);
        }
        if (level == level1) {
            return code;
        }

        String result = getParentCode(code);
        while (getLevel(result) > level) {
            result = getParentCode(result);
        }

        return result;
    }

    static String getLevelCode(String code, DepartmentLevel level) {
        return getLevelCode(code, level.getLevel());
    }

    /**
     * 检查parentDepartmentCode是否为targetDepartmentCode的父级编码
     * @param parentDepartmentCode
     * @param targetDepartmentCode
     * @return
     */
    static boolean isParentDepartment(String parentDepartmentCode, String targetDepartmentCode) {
        if(parentDepartmentCode.equals(targetDepartmentCode)) {
            return false;
        }
        return parentDepartmentCode.contains(targetDepartmentCode);
    }

    void deleteTree(String id);
}
