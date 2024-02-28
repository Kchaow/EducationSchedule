package org.letunov.exceptions;

public class TheDependentEntityIsPreservedBeforeTheIndependentEntity extends RuntimeException
{
    public TheDependentEntityIsPreservedBeforeTheIndependentEntity(String message)
    {
        super(message);
    }
}
