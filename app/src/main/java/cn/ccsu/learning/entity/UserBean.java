package cn.ccsu.learning.entity;

public class UserBean {

    /**
     * role : {"rid":"2","rname":"teacher"}
     * mesUser : {"userId":"656a76f469604d868a29048255f19848","userName":"teacher","userPassword":"25f9e794323b453885f5181f1b624d0b","userRealname":"蒋团媛","userSex":"女","userSubordinate":"呵呵呵呵","userTel":"77889955","userEmail":""}
     */
    private RoleBean role;
    private MesUserBean mesUser;

    public RoleBean getRole() {
        return role;
    }

    public void setRole(RoleBean role) {
        this.role = role;
    }

    public MesUserBean getMesUser() {
        return mesUser;
    }

    public void setMesUser(MesUserBean mesUser) {
        this.mesUser = mesUser;
    }

    public static class RoleBean {
        /**
         * rid : 2
         * rname : teacher
         */

        private String rid;
        private String rname;

        public String getRid() {
            return rid;
        }

        public void setRid(String rid) {
            this.rid = rid;
        }

        public String getRname() {
            return rname;
        }

        public void setRname(String rname) {
            this.rname = rname;
        }
    }

    public static class MesUserBean {
        /**
         * userId : 656a76f469604d868a29048255f19848
         * userName : teacher
         * userPassword : 25f9e794323b453885f5181f1b624d0b
         * userRealname : 蒋团媛
         * userSex : 女
         * userSubordinate : 呵呵呵呵
         * userTel : 77889955
         * userEmail :
         */

        private String userId;
        private String userName;
        private String userPassword;
        private String userRealname;
        private String userSex;
        private String userSubordinate;
        private String userTel;
        private String userEmail;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }

        public String getUserRealname() {
            return userRealname;
        }

        public void setUserRealname(String userRealname) {
            this.userRealname = userRealname;
        }

        public String getUserSex() {
            return userSex;
        }

        public void setUserSex(String userSex) {
            this.userSex = userSex;
        }

        public String getUserSubordinate() {
            return userSubordinate;
        }

        public void setUserSubordinate(String userSubordinate) {
            this.userSubordinate = userSubordinate;
        }

        public String getUserTel() {
            return userTel;
        }

        public void setUserTel(String userTel) {
            this.userTel = userTel;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }
    }
}
