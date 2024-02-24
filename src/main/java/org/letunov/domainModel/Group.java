package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class Group extends AbstractEntity
{
    private String name;
    private Set<User> user;
}
