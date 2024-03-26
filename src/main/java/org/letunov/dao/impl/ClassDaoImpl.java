package org.letunov.dao.impl;

import lombok.Getter;
import lombok.Setter;
import org.letunov.dao.*;
import org.letunov.domainModel.*;
import org.letunov.domainModel.Class;
import org.letunov.exceptions.TheDependentEntityIsPreservedBeforeTheIndependentEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;

@Repository
@Transactional
public class ClassDaoImpl implements ClassDao
{
    final private JdbcTemplate jdbcTemplate;
    final private UserDao userDao;
    final private SubjectDao subjectDao;
    final private GroupDao groupDao;
    final private ScheduleTemplateDao scheduleTemplateDao;
    public ClassDaoImpl(JdbcTemplate jdbcTemplate, UserDao userDao, SubjectDao subjectDao, GroupDao groupDao, ScheduleTemplateDao scheduleTemplateDao)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.subjectDao = subjectDao;
        this.groupDao = groupDao;
        this.scheduleTemplateDao = scheduleTemplateDao;
    }

    @Override
    public Class findByWeekNumberAndSubjectIdAndAudienceNumberAndDayOfWeeKAndClassNumber(int weekNumber, long subjectId, int audienceNumber, DayOfWeek dayOfWeek,
                                                                                         int classNumber, ScheduleTemplate scheduleTemplate)
    {
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.day_of_week, e.class_number, e.audience,
                       e.subject_id, gr.group_id, e.schedule_template_id
                       FROM class e
                       LEFT JOIN class_group gr ON e.id = gr.class_id
                       WHERE e.week_number = ? AND e.schedule_template_id = ? AND e.subject_id = ? AND e.audience = ? AND e.day_of_week = ? AND e.class_number = ?
                       ORDER BY e.day_of_week ASC
                """;
        List<Class> classList = jdbcTemplate.query(query, new ClassExtractor(), weekNumber, subjectId, audienceNumber, dayOfWeek.getValue(), classNumber , scheduleTemplate.getId());
        if (classList == null)
            return null;
        try
        {
            return fillDependence(classList).getFirst();
        }
        catch (NoSuchElementException e)
        {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Class> findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(int weekNumber)
    {
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.day_of_week, e.class_number, e.audience,
                       e.subject_id, gr.group_id, e.schedule_template_id
                       FROM class e
                       LEFT JOIN class_group gr ON e.id = gr.class_id
                       JOIN schedule_template s ON e.schedule_template_id = s.id
                       WHERE e.week_number = ? AND s.is_active = TRUE
                       ORDER BY e.day_of_week ASC
                """;
        List<Class> classList = jdbcTemplate.query(query, new ClassExtractor(), weekNumber);
        if (classList == null)
            return null;
        return fillDependence(classList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Class> findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(int weekNumber, ScheduleTemplate scheduleTemplate)
    {
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.day_of_week, e.class_number, e.audience,
                       e.subject_id, gr.group_id, e.schedule_template_id
                       FROM class e
                       LEFT JOIN class_group gr ON e.id = gr.class_id
                       JOIN schedule_template s ON e.schedule_template_id = s.id
                       WHERE e.week_number = ? AND s.id = ?
                       ORDER BY e.day_of_week ASC
                """;
        List<Class> classList = jdbcTemplate.query(query, new ClassExtractor(), weekNumber, scheduleTemplate.getId());
        if (classList == null)
            return null;
        return fillDependence(classList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Class> findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(int weekNumber, User user)
    {
        if (user == null)
            throw new NullPointerException("user arg cannot be null");
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.day_of_week, e.class_number, e.audience,
                       e.subject_id, gr.group_id, e.schedule_template_id
                       FROM class e
                       LEFT JOIN class_group gr ON e.id = gr.class_id
                       JOIN schedule_template s ON e.schedule_template_id = s.id
                       WHERE e.week_number = ? AND e.user_id = ? AND s.is_active = TRUE
                       ORDER BY e.day_of_week ASC,
                       class_number ASC
                """;
        List<Class> classList = jdbcTemplate.query(query, new ClassExtractor(), weekNumber, user.getId());
        if (classList == null)
            return null;
        return fillDependence(classList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Class> findByWeekNumberAndTeacherOrderByDayOfWeekAscClassNumberAsc(int weekNumber, User user, ScheduleTemplate scheduleTemplate)
    {
        if (user == null)
            throw new NullPointerException("user arg cannot be null");
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.day_of_week, e.class_number, e.audience,
                       e.subject_id, gr.group_id, e.schedule_template_id
                       FROM class e
                       LEFT JOIN class_group gr ON e.id = gr.class_id
                       JOIN schedule_template s ON e.schedule_template_id = s.id
                       WHERE e.week_number = ? AND e.user_id = ? AND s.is_active = TRUE AND s.id = ?
                       ORDER BY e.day_of_week ASC,
                       class_number ASC
                """;
        List<Class> classList = jdbcTemplate.query(query, new ClassExtractor(), weekNumber, user.getId(), scheduleTemplate.getId());
        if (classList == null)
            return null;
        return fillDependence(classList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Class> findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(int weekNumber, Group group)
    {
        if (group == null)
            throw new NullPointerException("group arg cannot be null");
        List<Class> classList = findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(weekNumber);
        if (classList == null)
            return null;

        for (int i = 0; i < classList.size(); i++)
        {
            if (!classList.get(i).getGroup().contains(group))
            {
                classList.remove(i);
                i--;
            }
        }
        return classList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Class> findByWeekNumberAndGroupOrderByDayOfWeekAscClassNumberAsc(int weekNumber, Group group, ScheduleTemplate scheduleTemplate)
    {
        if (group == null)
            throw new NullPointerException("group arg cannot be null");
        List<Class> classList = findByWeekNumberOrderByDayOfWeekAscClassNumberAsc(weekNumber, scheduleTemplate);
        if (classList == null)
            return null;

        for (int i = 0; i < classList.size(); i++)
        {
            if (!classList.get(i).getGroup().contains(group))
            {
                classList.remove(i);
                i--;
            }
        }
        return classList;
    }

    @Override
    @Transactional(readOnly = true)
    public Class findById(long id)
    {
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.day_of_week, e.class_number, e.audience,
                       e.subject_id, gr.group_id, e.schedule_template_id
                       FROM class e
                       LEFT JOIN class_group gr ON e.id = gr.class_id
                       WHERE e.id = ?
                """;
        List<Class> educationDayList = jdbcTemplate.query(query, new ClassExtractor(), id);
        if (educationDayList == null)
            return null;
        fillDependence(educationDayList);
        if (educationDayList.isEmpty())
            return null;
        return educationDayList.getFirst();
    }

    @Override
    @Transactional
    public void deleteById(long id)
    {
        final String query = "DELETE FROM class WHERE id = ?";
        jdbcTemplate.update(query, id);

    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Class save(Class clazz)
    {
        if (clazz == null)
            throw new NullPointerException("clazz arg cannot be null");
        else if (clazz.getWeekNumber() <= 0)
            throw new NullPointerException("clazz weekNumber cannot be less or equal 0");
        else if (clazz.getDayOfWeek() == null)
            throw new NullPointerException("clazz date cannot be null");
        else if (clazz.getSubject() == null)
            throw new NullPointerException("clazz subject cannot be null");
        else if (clazz.getClassNumber() <= 0)
            throw new NullPointerException("clazz classNumber cannot be less or equal 0");
        else if (clazz.getGroup() == null || clazz.getGroup().isEmpty())
            throw new NullPointerException("clazz group cannot be null or empty");
        else if (clazz.getScheduleTemplate() == null)
            throw new NullPointerException("clazz scheduleTemplate cannot be null or empty");
        for (Group group : clazz.getGroup())
        {
            if (groupDao.findById(group.getId()) == null)
                throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent clazz entity before an independent group entity");
        }
        if (clazz.getUser() != null && userDao.findById(clazz.getUser().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent clazz entity before an independent user entity");
        if (clazz.getScheduleTemplate() != null && scheduleTemplateDao.findById(clazz.getScheduleTemplate().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent clazz entity before an independent scheduleTemplate entity");
        if (clazz.getId() != 0 && findById(clazz.getId()) != null)
        {
            final String query = """
                    UPDATE class SET week_number = ?, user_id = ?, day_of_week = ?, class_number = ?,
                                      audience = ?, subject_id = ?, schedule_template_id = ? WHERE id = ?;
                    """;
            jdbcTemplate.update(query, clazz.getWeekNumber(), clazz.getUser() == null ? null : clazz.getUser().getId(),
                    clazz.getDayOfWeek().getValue(), clazz.getClassNumber(), clazz.getAudience() <= 0 ? null : clazz.getAudience(),
                    clazz.getSubject().getId(), clazz.getScheduleTemplate().getId(), clazz.getId());
            if (clazz.getGroup() != null && !clazz.getGroup().isEmpty())
            {
                String groupQuery = "SELECT id, group_id, class_id FROM class_group WHERE group_id = ? AND class_id = ?";
                for (Group group : clazz.getGroup())
                {
                    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(groupQuery, group.getId(), clazz.getId());
                    if (sqlRowSet.first())
                    {
                        long id = sqlRowSet.getLong("id");
                        String groupUpdate = "UPDATE class_group SET group_id = ?, class_id = ? WHERE id = ?";
                        jdbcTemplate.update(groupUpdate, group.getId(), clazz.getId(), id);
                    }
                    String groupRelationSave = "INSERT INTO class_group(group_id, class_id) VALUES(?, ?)";
                    jdbcTemplate.update(groupRelationSave, group.getId(), clazz.getId());
                }
                String allRelationsQuery = """
                        SELECT class_group.id, gr.name group_name, group_id, class_id
                        FROM class_group
                        JOIN "group" gr ON gr.id = group_id
                        WHERE class_id = ?
                        """;
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(allRelationsQuery, clazz.getId());
                while (sqlRowSet.next())
                {
                    Group group = new Group();
                    group.setId(sqlRowSet.getInt("group_id"));
                    group.setName(sqlRowSet.getString("group_name"));
                    if (clazz.getGroup().contains(group))
                    {
                        String deleteRelationQuery = "DELETE FROM class_group WHERE id = ?";
                        jdbcTemplate.update(deleteRelationQuery, sqlRowSet.getInt("id"));
                    }
                }
            }
            return findById(clazz.getId());
        }
        final String query = """
                INSERT INTO class(week_number, user_id, day_of_week, class_number, audience, subject_id, schedule_template_id)
                VALUES(?,?,?,?,?,?,?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementSaveCreator preparedStatementSaveCreator = new PreparedStatementSaveCreator();
        preparedStatementSaveCreator.setClazz(clazz);
        preparedStatementSaveCreator.setQuery(query);
        jdbcTemplate.update(preparedStatementSaveCreator, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        final String groupRelationSave = "INSERT INTO class_group(group_id, class_id) VALUES(?, ?)";
        jdbcTemplate.batchUpdate(groupRelationSave, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException
            {
                Group group = clazz.getGroup().get(i);
                ps.setLong(1, group.getId());
                ps.setLong(2, id);
            }

            @Override
            public int getBatchSize()
            {
                return clazz.getGroup().size();
            }
        });
        return findById(id);
    }

    private List<Class> fillDependence(List<Class> classList)
    {
        Map<Long, User> userMap = new HashMap<>();
        Map<Long, Group> groupMap = new HashMap<>();
        Map<Long, Subject> subjectMap = new HashMap<>();
        Map<Long, ScheduleTemplate> scheduleTemplateMap = new HashMap<>();
        User user;
        Group group;
        Subject subject;
        ScheduleTemplate scheduleTemplate;
        for (Class clazz : classList)
        {
            if (clazz.getUser() != null)
            {
                long userId = clazz.getUser().getId();
                user = userMap.get(userId);
                if (user == null)
                {
                    user = userDao.findById(userId);
                    userMap.put(userId, user);
                    clazz.setUser(user);
                }
                else
                {
                    clazz.setUser(user);
                }
            }

            if (clazz.getSubject() != null)
            {
                long subjectId = clazz.getSubject().getId();
                subject = subjectMap.get(subjectId);
                if (subject == null)
                {
                    subject = subjectDao.findById(subjectId);
                    subjectMap.put(subjectId, subject);
                }
                clazz.setSubject(subject);
            }

            if (clazz.getGroup() != null)
            {
                List<Group> groupList = clazz.getGroup();
                for (int i = 0; i < groupList.size(); i++)
                {
                    long groupId = groupList.get(i).getId();
                    group = groupMap.get(groupId);
                    if (group == null)
                    {
                        group = groupDao.findById(groupId);
                        groupMap.put(groupId, group);
                    }
                    clazz.getGroup().set(i, group);
                }
            }

            long scheduleTemplateId = clazz.getScheduleTemplate().getId();
            scheduleTemplate = scheduleTemplateMap.get(scheduleTemplateId);
            if (scheduleTemplate == null)
            {
                scheduleTemplate = scheduleTemplateDao.findById(scheduleTemplateId);
                scheduleTemplateMap.put(scheduleTemplateId, scheduleTemplate);
            }
            clazz.setScheduleTemplate(scheduleTemplate);
        }
        return classList;
    }

    private static class ClassExtractor implements ResultSetExtractor<List<Class>>
    {
        @Override
        public List<Class> extractData(ResultSet rs) throws SQLException, DataAccessException
        {
            Map<Long, Set<Group>> classGroup = new HashMap<>();
            Map<Long, Class> classMap = new HashMap<>();
            Class clazz;
            while (rs.next())
            {
                long educationDayId = rs.getLong("id");
                clazz = classMap.get(educationDayId);
                if (clazz == null)
                {
                    clazz = new Class();
                    clazz.setId(educationDayId);
                    clazz.setDayOfWeek(DayOfWeek.of(rs.getInt("day_of_week")));
                    clazz.setAudience(rs.getInt("audience"));
                    clazz.setClassNumber(rs.getInt("class_number"));
                    clazz.setWeekNumber(rs.getInt("week_number"));
                    ScheduleTemplate scheduleTemplate = new ScheduleTemplate();
                    scheduleTemplate.setId(rs.getLong("schedule_template_id"));
                    clazz.setScheduleTemplate(scheduleTemplate);
                    long userId = rs.getLong("user_id");
                    if (userId != 0)
                    {
                        User user = new User();
                        user.setId(userId);
                        clazz.setUser(user);
                    }
                    long subjectId = rs.getLong("subject_id");
                    if (subjectId != 0)
                    {
                        Subject subject = new Subject();
                        subject.setId(subjectId);
                        clazz.setSubject(subject);
                    }
                    classMap.put(educationDayId, clazz);
                }

                long groupId = rs.getLong("group_id");
                if (groupId != 0) {
                    Set<Group> groupSet = classGroup.get(educationDayId);
                    if (groupSet == null) {
                        groupSet = new HashSet<>();
                        Group group = new Group();
                        group.setId(groupId);
                        groupSet.add(group);
                        classGroup.put(educationDayId, groupSet);
                    } else {
                        Group group = new Group();
                        group.setId(groupId);
                        groupSet.add(group);
                    }
                    clazz.setGroup(new ArrayList<>(groupSet));
                }
            }
            return new ArrayList<>(classMap.values());
        }
    }

    @Setter
    @Getter
    private static class PreparedStatementSaveCreator implements PreparedStatementCreator
    {
        private Class clazz;
        private String query;
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException
        {
            PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
            preparedStatement.setInt(1, clazz.getWeekNumber());
            if (clazz.getUser() != null)
                preparedStatement.setLong(2, clazz.getUser().getId());
            else
                preparedStatement.setNull(2, Types.NULL);
            preparedStatement.setInt(3, clazz.getDayOfWeek().getValue());
            preparedStatement.setInt(4, clazz.getClassNumber());
            if (clazz.getAudience() > 0)
                preparedStatement.setInt(5, clazz.getAudience());
            else
                preparedStatement.setNull(5, Types.NULL);
            preparedStatement.setLong(6, clazz.getSubject().getId());
            preparedStatement.setLong(7, clazz.getScheduleTemplate().getId());
            return preparedStatement;
        }
    }
}
