package org.letunov.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserNamesDto
{
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
}
