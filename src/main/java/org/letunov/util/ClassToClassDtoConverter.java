package org.letunov.util;

import org.letunov.domainModel.Class;
import org.letunov.domainModel.Group;
import org.letunov.service.dto.ClassDto;
import org.letunov.service.dto.UserNamesDto;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.List;

public class ClassToClassDtoConverter implements Converter<Class, ClassDto>
{

    @Override
    public ClassDto convert(Class source)
    {
        UserNamesDto userNamesDto = null;
        if (source.getUser() != null)
            userNamesDto = UserNamesDto.builder()
                    .firstName(source.getUser().getFirstName())
                    .lastName(source.getUser().getLastName())
                    .middleName(source.getUser().getMiddleName())
                    .build();

        return ClassDto.builder()
                .id(source.getId())
                .classNumber(source.getClassNumber())
                .userNamesDto(userNamesDto)
                .groups(source.getGroup())
                .dayOfWeek(source.getDayOfWeek().getValue())
                .weekNumber(source.getWeekNumber())
                .audience(source.getAudience())
                .classNumber(source.getClassNumber())
                .subject(source.getSubject())
                .scheduleTemplateId(source.getScheduleTemplate().getId())
                .build();
    }
}
