package org.letunov.domainModel;

import lombok.Data;

import java.io.Serializable;

@Data
public class AbstractEntity implements Serializable
{
    private long id;
}
