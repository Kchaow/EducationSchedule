package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends AbstractEntity
{
    private String name;
}
