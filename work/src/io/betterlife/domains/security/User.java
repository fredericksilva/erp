package io.betterlife.domains.security;

import io.betterlife.domains.BaseObject;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Author: Lawrence Liu(xqinliu@cn.ibm.com)
 * Date: 10/31/14
 */

@Entity
@NamedQueries({
    @NamedQuery(name = "User.getById", query = "SELECT u FROM User u WHERE u.id = :id "),
    @NamedQuery(name = User.GetByUserNameAndPasswordQuery,
        query = "SELECT u from User u WHERE u.username =: username AND u.password = :password")
})
public class User extends BaseObject {

    public static final String GetByUserNameAndPasswordQuery = "User.getByUsernameAndPassword";

    public void setUsername(String username) {
        setValue("username",username);
    }

    public String getUsername() {
        return getValue("username");
    }

    public void setPassword(String password) {
        setValue("password", password);
    }

    public String getPassword() {
        return getValue("password");
    }

}
