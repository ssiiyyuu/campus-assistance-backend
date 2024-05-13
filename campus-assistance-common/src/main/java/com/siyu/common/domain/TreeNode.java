package com.siyu.common.domain;

import java.util.List;

public interface TreeNode<T extends TreeNode<?, ID>, ID> {

    ID getId();

    ID getParentId();

    void setChildList(List<T> childList);
}
