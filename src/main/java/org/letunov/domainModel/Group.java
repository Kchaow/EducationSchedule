package org.letunov.domainModel;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class Group extends AbstractEntity
{
    private String name;
}
