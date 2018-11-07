package com.xiongms.libcore.enums;

/**
 *
 * 店铺角色
 *
 *  0	普通用户
 *	1	老板
 *	2	店长
 *	3	财务
 *	4	店员
 */

public enum RoleTypeEnum {
    NORMAL(0), BOSS(1), MANAGER(2), FINANCE(3), SCLERK(4);

    private int value;

    RoleTypeEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
