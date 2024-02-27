package org.letunov.domainModel;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
public class User extends AbstractEntity
{
    private String firstName;
    private String lastName;
    private String middleName;
    private String password;
    private String email;
    private String login;
    private Role role;
    private Group group;

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (! (o instanceof User user))
            return false;

        return super.equals(user) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) &&
                Objects.equals(middleName, user.middleName) && Objects.equals(password, user.password) &&
                Objects.equals(email, user.email) && Objects.equals(login, user.login) && Objects.equals(role, user.role) &&
                Objects.equals(group, user.group);
    }

    @Override
    public int hashCode()
    {
        int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hashCode(firstName);
        result = prime * result + Objects.hashCode(lastName);
        result = prime * result + Objects.hashCode(middleName);
        result = prime * result + Objects.hashCode(password);
        result = prime * result + Objects.hashCode(email);
        result = prime * result + Objects.hashCode(login);
        result = prime * result + Objects.hashCode(role);
        if (group != null)
        {
            result = prime * result + Long.hashCode(group.getId());
            result = prime * result + Objects.hashCode(group.getName());
        }
        return result;
    }
}
