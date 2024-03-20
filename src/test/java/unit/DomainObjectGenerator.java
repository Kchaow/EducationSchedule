package unit;

import org.letunov.domainModel.*;
import org.letunov.service.dto.EducationDayDto;
import org.letunov.service.dto.ScheduleDto;
import org.letunov.service.dto.SubjectDto;
import org.letunov.service.dto.UserNamesDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

//Сделать приемлемым
public class DomainObjectGenerator
{
    public ScheduleDto convertToScheduleDto(List<EducationDay> educationDayList)
    {
        ScheduleDto scheduleDto = new ScheduleDto();
        List<EducationDayDto> educationDayDtoList = new ArrayList<>();
        for (EducationDay educationDay : educationDayList)
        {
            List<Long> groupIds = new ArrayList<>();
            educationDay.getGroup().forEach(x -> groupIds.add(x.getId()));
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(educationDay.getSubject().getId());
            subjectDto.setName(educationDay.getSubject().getName());
            UserNamesDto userNamesDto = new UserNamesDto();
            userNamesDto.setId(educationDay.getUser().getId());
            userNamesDto.setFirstName(educationDay.getUser().getFirstName());
            userNamesDto.setLastName(educationDay.getUser().getLastName());
            userNamesDto.setMiddleName(educationDay.getUser().getMiddleName());
            EducationDayDto educationDayDto = EducationDayDto.builder()
                    .id(educationDay.getId())
                    .userNamesDto(userNamesDto)
                    .subject(subjectDto)
                    .audience(educationDay.getAudience())
                    .classNumber(educationDay.getClassNumber())
                    .groupsId(groupIds)
                    .dayOfWeek(educationDay.getDayOfWeek().getValue())
                    .weekNumber(educationDay.getWeekNumber())
                    .build();
            educationDayDtoList.add(educationDayDto);
        }
        scheduleDto.setClasses(educationDayDtoList);
        return scheduleDto;
    }

    public List<EducationDay> getEducationDayList(int size)
    {
        List<EducationDay> educationDayList = new ArrayList<>();

        final int maxClassNumber = 6;
        final Map<Integer, String> firstNames = getFirstName();
        final Map<Integer, String> lastNames = getLastName();
        final Map<Integer, String> middleNames = getMiddleName();
        final Map<Integer, String> groups = getGroup();
        final Map<Integer, String> subjects = getSubject();
        Role role = new Role();
        role.setId(3);
        role.setName("teacher");
        int dayOfWeek = 1;

        Random random = new Random();
        int weekNumber = 1;

        for (int i = 0; i < size; i++)
        {
            if (dayOfWeek == 7)
            {
                i--;
                weekNumber++;
                dayOfWeek = 1;
                continue;
            }
            int totalClassNumberAtDay = random.nextInt(2, 7);
            int classNumber = maxClassNumber - totalClassNumberAtDay + 1;
            while (totalClassNumberAtDay > 0)
            {
                List<Group> educationDayGroups = new ArrayList<>();
                int groupNumber = random.nextInt(1, 4);
                for (Map.Entry<Integer, String> entry : groups.entrySet())
                {
                    if (groupNumber <= 0) break;
                    Group newGroup = new Group();
                    newGroup.setName(entry.getValue());
                    newGroup.setId(entry.getKey());
                    educationDayGroups.add(newGroup);
                    groupNumber--;
                }

                int subjectId = random.nextInt(0, subjects.size());
                Subject subject = new Subject();
                subject.setId(subjectId);
                subject.setName(subjects.get(subjectId));

                int audience = random.nextInt(0, 100);

                int userId = random.nextInt(0, firstNames.size());
                User user = new User();
                user.setId(userId);
                user.setFirstName(firstNames.get(userId));
                user.setLastName(lastNames.get(userId));
                user.setMiddleName(middleNames.get(userId));
                user.setPassword("1234");
                user.setEmail(lastNames.get(userId) + "@" + "gmail.com");
                user.setLogin(lastNames.get(userId));
                user.setRole(role);

                EducationDay educationDay = new EducationDay();
                educationDay.setId(i);
                educationDay.setAudience(audience);
                educationDay.setClassNumber(classNumber);
                educationDay.setDayOfWeek(DayOfWeek.of(dayOfWeek));
                educationDay.setWeekNumber(weekNumber);
                educationDay.setSubject(subject);
                educationDay.setUser(user);
                educationDay.setGroup(educationDayGroups);
                educationDayList.add(educationDay);

                classNumber++;
                totalClassNumberAtDay--;
            }
            dayOfWeek++;
        }

        return educationDayList;





//        List<EducationDay> educationDayList = new ArrayList<>();
//
//        Role role = new Role();
//        role.setId(3);
//        role.setName("teacher");
//
//        User user = new User();
//        user.setId(2);
//        user.setFirstName("firstName");
//        user.setLastName("lastName");
//        user.setMiddleName("middleName");
//        user.setLogin("login");
//        user.setEmail("email");
//        user.setPassword("password");
//        user.setGroup(null);
//        user.setRole(role);
//
//        Group group = new Group();
//        group.setId(1);
//        group.setName("group");
//        List<Group> groups = new ArrayList<>();
//        groups.add(group);
//
//        int weekNumber = 1;
//        int maxClass = 6;
//
//        Subject subject1 = new Subject();
//        subject1.setId(1);
//        subject1.setName("firstSubject");
//
//        Subject subject2 = new Subject();
//        subject2.setId(2);
//        subject2.setName("secondSubject");
//
//        LocalDate date = LocalDate.of(2024, 3, 4);
//        for (int i = 0, classNumber = 0; i < size; i++, classNumber++)
//        {
//            if (date.getDayOfWeek() == DayOfWeek.SUNDAY)
//            {
//                weekNumber++;
//                date = date.plusDays(1);
//            }
//            if (classNumber > maxClass)
//                classNumber = 1;
//            EducationDay educationDay = new EducationDay();
//            educationDay.setId(i+1);
//            educationDay.setWeekNumber(weekNumber);
//            educationDay.setDate(date);
//            educationDay.setUser(user);
//            educationDay.setAudience(255 + i);
//            educationDay.setClassNumber(classNumber);
//            educationDay.setGroup(groups);
//            if (i % 2 == 0)
//                educationDay.setSubject(subject1);
//            else
//                educationDay.setSubject(subject2);
//            date = date.plusDays(1);
//            educationDayList.add(educationDay);
//        }
//        return educationDayList;
    }

    private Map<Integer, String> getFirstName()
    {
        final Map<Integer, String> firstName = new HashMap<>();
        firstName.put(0, "Карл");
        firstName.put(2, "Степан");
        firstName.put(3, "Максим");
        firstName.put(4, "Олег");
        firstName.put(5, "Арина");
        firstName.put(6, "Елизавета");
        firstName.put(7, "Ирина");
        firstName.put(8, "Алексей");
        firstName.put(9, "Александр");
        firstName.put(10, "Ольга");
        firstName.put(11, "Владимир");
        firstName.put(12, "Муад'диб");
        firstName.put(13, "Ян");
        firstName.put(14, "Вячеслав");
        firstName.put(15, "Николай");
        return firstName;
    }

    private Map<Integer, String> getLastName()
    {
        final Map<Integer, String> lastName = new HashMap<>();
        lastName.put(0, "Одуванчиков");
        lastName.put(1, "Жириновский");
        lastName.put(2, "Ходоренко");
        lastName.put(3, "Гослинг");
        lastName.put(4, "Арабов");
        lastName.put(5, "Адамова");
        lastName.put(6, "Попов");
        lastName.put(7, "Попова");
        lastName.put(8, "Каримов");
        lastName.put(9, "Кальянов");
        lastName.put(10, "Живов");
        lastName.put(11, "Ушников");
        lastName.put(12, "Пушкин");
        lastName.put(13, "Палкин");
        lastName.put(14, "Сталкин");
        lastName.put(15, "Скалкина");
        return lastName;
    }

    private Map<Integer, String> getMiddleName()
    {
        final Map<Integer, String> middleName = new HashMap<>();
        middleName.put(0, "Степановна");
        middleName.put(1, "Карлович");
        middleName.put(2, "Максимович");
        middleName.put(3, "Олеговна");
        middleName.put(4, "Ариновна");
        middleName.put(5, "Елизаветовна");
        middleName.put(6, "Иринина");
        middleName.put(7, "Алексеевна");
        middleName.put(8, "Александровна");
        middleName.put(9, "Ольгин");
        middleName.put(10, "Владимирович");
        middleName.put(11, "Янович");
        middleName.put(12, "Матвеевич");
        middleName.put(13, "Сергеевич");
        middleName.put(14, "Романовна");
        middleName.put(15, "Юрьевич");
        return middleName;
    }

    private Map<Integer, String> getGroup()
    {
        final Map<Integer, String> group = new HashMap<>();
        group.put(0, "БСБО-01-21");
        group.put(1, "БСБО-02-21");
        group.put(2, "БСБО-03-21");
        group.put(3, "БСБО-04-21");
        return group;
    }

    private Map<Integer, String> getSubject()
    {
        final Map<Integer, String> subject = new HashMap<>();
        subject.put(0, "Технологии непрерывного цикла разработки программного обеспечения");
        subject.put(1, "Алгоритмы компонентов цифровой обработки данных");
        subject.put(2, "Методы и средства моделирования и проектирования программного обеспечения");
        subject.put(3, "Администрирование инфраструктуры виртуализыции сетевых функций");
        subject.put(4, "Модели и методы принятия решений");
        subject.put(5, "Веб программирование");
        return subject;
    }
}
