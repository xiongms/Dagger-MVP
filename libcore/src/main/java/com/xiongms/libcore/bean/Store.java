package com.xiongms.libcore.bean;

import java.io.Serializable;

/**
 * 店铺信息
 *
 * @author xiongms
 * @time 2018-08-27 16:57
 */
public class Store implements Serializable {

    private int userId;

    private int storeId;
    private String storeName;

    /**
     * 0:普通用户 1 老板 2 店长 3 财务 4 店员
     */
    private int roleType;
    private String storeLogo;
    private String roleTypeName;
    /**
     * 1 借款店铺  2 非借款店铺
     */
    private int cooperationType;

    private boolean isShowBalanceModule;
    private boolean isShowShopLoanModule;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isShowBalanceModule() {
        return isShowBalanceModule;
    }

    public void setShowBalanceModule(boolean showBalanceModule) {
        isShowBalanceModule = showBalanceModule;
    }

    public boolean isShowShopLoanModule() {
        return isShowShopLoanModule;
    }

    public void setShowShopLoanModule(boolean showShopLoanModule) {
        isShowShopLoanModule = showShopLoanModule;
    }

    public int getCooperationType() {
        return cooperationType;
    }

    public void setCooperationType(int cooperationType) {
        this.cooperationType = cooperationType;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName == null ? "" : storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getRoleType() {
        return roleType;
    }

    public void setRoleType(int roleType) {
        this.roleType = roleType;
    }

    public String getStoreLogo() {
        return storeLogo == null ? "" : storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getRoleTypeName() {
        return roleTypeName == null ? "" : roleTypeName;
    }

    public void setRoleTypeName(String roleTypeName) {
        this.roleTypeName = roleTypeName;
    }


    @Override
    public String toString() {
        return storeName;
    }


    public boolean equals(Store store) {
        if(store == null) {
            return false;
        }
        return this.userId == store.getUserId()
                && this.roleType == store.getRoleType()
                && this.roleType == store.getRoleType()
                && this.cooperationType == store.getCooperationType()
                && this.isShowBalanceModule == store.isShowBalanceModule()
                && this.isShowShopLoanModule == store.isShowShopLoanModule()
                && this.getStoreName().equals(store.getStoreName())
                && this.getStoreLogo().equals(store.getStoreLogo())
                && this.getRoleTypeName().equals(store.getRoleTypeName());
    }
}
