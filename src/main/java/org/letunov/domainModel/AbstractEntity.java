package org.letunov.domainModel;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Objects;

@EqualsAndHashCode
@Data
public class AbstractEntity implements Serializable
{
    private long id;
}
