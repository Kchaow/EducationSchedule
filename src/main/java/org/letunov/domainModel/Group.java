package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;
import java.util.Set;

@Data
public class Group extends AbstractEntity
{
    private String name;
    private Set<User> user;

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (! (o instanceof Group group))
            return false;

        return super.equals(group) && Objects.equals(name, group.name) && Objects.equals(user, group.user);
    }

    @Override
    public int hashCode()
    {
        int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hashCode(name);
        for (User el : user)
        {
            result = prime * result + Long.hashCode(el.getId());
            result = prime * result + Objects.hashCode(el.getFirstName());
            result = prime * result + Objects.hashCode(el.getLastName());
            result = prime * result + Objects.hashCode(el.getMiddleName());
            result = prime * result + Objects.hashCode(el.getEmail());
            result = prime * result + Objects.hashCode(el.getLogin());
            result = prime * result + Objects.hashCode(el.getPassword());
            result = prime * result + Objects.hashCode(el.getRole());
        }
        return result;
    }
}
