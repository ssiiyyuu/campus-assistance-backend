package com.siyu.common.utils;

import com.siyu.common.domain.TreeNode;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreeUtils {


    /**
     * 将扁平化的list封装为树形结构
     * @param list 扁平化的list
     * @return 封装好的树形list
     */
    public static <T extends TreeNode<T, ID>, ID> List<T> buildTree(List<T> list, @NotNull ID rootId) {
        Map<ID, List<T>> childMap = list.stream()
                .filter(item -> !rootId.equals(item.getId()))
                .collect(Collectors.groupingBy(TreeNode::getParentId));
        List<T> result = list.stream().peek(item -> item.setChildList(childMap.get(item.getId())))
                .filter(item -> rootId.equals(item.getParentId()))
                .collect(Collectors.toList());
        return result;
    }
}
