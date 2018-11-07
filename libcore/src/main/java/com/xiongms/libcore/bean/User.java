package com.xiongms.libcore.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 用户信息
 *
 * @author xiongms
 * @time 2018-08-23 14:38
 */
public class User implements Serializable {


    /**
     * phone : 15123117189
     * tkn : A71F81D3629B725ED266520E5BE453D6
     * tknExpireTime : 2019-09-10 11:59:16
     * storeList : [{"storeId":48,"storeName":"借款","roleType":2,"roleTypeName":"店长"},{"storeId":50,"storeName":"重庆西餐厅","roleType":2,"roleTypeName":"店长"},{"storeId":3,"storeName":"永辉超市","roleType":2,"roleTypeName":"店长"},{"storeId":87,"storeName":"测试123","roleType":2,"roleTypeName":"店长"}]
     */

    private String phone;
    private String tkn;
    private String userName;
    private String tknExpireTime;
    private List<StoreListBean> storeList;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTkn() {
        return tkn;
    }

    public void setTkn(String tkn) {
        this.tkn = tkn;
    }

    public String getTknExpireTime() {
        return tknExpireTime;
    }

    public void setTknExpireTime(String tknExpireTime) {
        this.tknExpireTime = tknExpireTime;
    }

    public List<StoreListBean> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<StoreListBean> storeList) {
        this.storeList = storeList;
    }

    public static class StoreListBean implements Serializable {
        /**
         * storeId : 48
         * storeName : 借款
         * roleType : 2
         * roleTypeName : 店长
         */

        private int storeId;
        private String storeName;
        private int roleType;
        private String roleTypeName;

        public int getStoreId() {
            return storeId;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }

        public String getStoreName() {
            return storeName;
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

        public String getRoleTypeName() {
            return roleTypeName;
        }

        public void setRoleTypeName(String roleTypeName) {
            this.roleTypeName = roleTypeName;
        }
    }
}
