package org.letunov.domainModel;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class AbstractEntity implements Serializable
{
    private long id;

    @Override
    public boolean equals(Object o)
    {
        if (o == this)
            return true;

        if (! (o instanceof AbstractEntity abstractEntity))
            return false;

        return id == abstractEntity.id;
    }

    @Override
    public int hashCode()
    {
        int prime = 31;
        return prime + Long.hashCode(id);
    }
}
