package org.letunov.domainModel;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
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
