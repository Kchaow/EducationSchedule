package org.letunov.domainModel;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
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
}
