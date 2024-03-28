package org.letunov.exceptions;

public class AccessDeniedException extends RuntimeException
{
    public AccessDeniedException(String message)
    {
        super(message);
    }
    public AccessDeniedException()
    {
        super();
    }
}
