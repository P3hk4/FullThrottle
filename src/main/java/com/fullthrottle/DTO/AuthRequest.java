package com.fullthrottle.DTO;

public class AuthRequest { 

        private String login;
        private String password;
        private String email;
        private String name;
    
        public AuthRequest(String login, String password, String email, String name) {
            this.login = login;
            this.password = password;
            this.email = email;
            this.name = name;
        }
    
        public AuthRequest() {
        }

        public String getName() {
        return name;
    }

        public void setName(String name) {
        this.name = name;
    }

        public String getLogin() {
            return login;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
        this.email = email;
        }
    
        public void setLogin(String login) {
            this.login = login;
        }
    
        public String getPassword() {
            return password;
        }
    
        public void setPassword(String password) {
            this.password = password;
        }
}
