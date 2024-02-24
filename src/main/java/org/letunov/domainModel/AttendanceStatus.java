package org.letunov.domainModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttendanceStatus extends AbstractEntity
{
    private String name;
}