package org.letunov.dao.impl;

import lombok.Getter;
import lombok.Setter;
import org.letunov.dao.EducationDayDao;
import org.letunov.dao.GroupDao;
import org.letunov.dao.SubjectDao;
import org.letunov.dao.UserDao;
import org.letunov.domainModel.EducationDay;
import org.letunov.domainModel.Group;
import org.letunov.domainModel.Subject;
import org.letunov.domainModel.User;
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
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.util.*;

@Repository
@Transactional
public class EducationDayDaoImpl implements EducationDayDao
{
    final private JdbcTemplate jdbcTemplate;
    final private UserDao userDao;
    final private SubjectDao subjectDao;
    final private GroupDao groupDao;
    public EducationDayDaoImpl(JdbcTemplate jdbcTemplate, UserDao userDao, SubjectDao subjectDao, GroupDao groupDao)
    {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
        this.subjectDao = subjectDao;
        this.groupDao = groupDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDay> findByWeekNumberOrderByDateAscClassNumberAsc(int weekNumber)
    {
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.date, e.class_number, e.audience,
                       e.subject_id, gr.group_id
                       FROM education_day e
                       LEFT JOIN education_day_group gr ON e.id = gr.education_day_id
                       WHERE e.week_number = ?
                       ORDER BY e.date ASC
                """;
        List<EducationDay> educationDayList = jdbcTemplate.query(query, new EducationDayExtractor(), weekNumber);
        if (educationDayList == null)
            return null;
        return fillDependence(educationDayList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDay> findByWeekNumberAndTeacherOrderByDateAscClassNumberAsc(int weekNumber, User user)
    {
        if (user == null)
            throw new NullPointerException("user arg cannot be null");
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.date, e.class_number, e.audience,
                       e.subject_id, gr.group_id
                       FROM education_day e
                       LEFT JOIN education_day_group gr ON e.id = gr.education_day_id
                       WHERE e.week_number = ? AND e.user_id = ?
                       ORDER BY e.date ASC,
                       class_number ASC
                """;
        List<EducationDay> educationDayList = jdbcTemplate.query(query, new EducationDayExtractor(), weekNumber, user.getId());
        if (educationDayList == null)
            return null;
        return fillDependence(educationDayList);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EducationDay> findByWeekNumberAndGroupOrderByDateAscClassNumberAsc(int weekNumber, Group group)
    {
        if (group == null)
            throw new NullPointerException("group arg cannot be null");
        List<EducationDay> educationDayList = findByWeekNumberOrderByDateAscClassNumberAsc(weekNumber);
        if (educationDayList == null)
            return null;

        for (int i = 0; i < educationDayList.size(); i++)
        {
            if (!educationDayList.get(i).getGroup().contains(group))
            {
                educationDayList.remove(i);
                i--;
            }
        }
        return educationDayList;
    }

    @Override
    @Transactional(readOnly = true)
    public EducationDay findById(long id)
    {
        final String query = """
                SELECT e.id, e.week_number, e.user_id, e.date, e.class_number, e.audience,
                       e.subject_id, gr.group_id
                       FROM education_day e
                       LEFT JOIN education_day_group gr ON e.id = gr.education_day_id
                       WHERE e.id = ?
                """;
        List<EducationDay> educationDayList = jdbcTemplate.query(query, new EducationDayExtractor(), id);
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
        final String query = "DELETE FROM education_day WHERE id = ?";
        jdbcTemplate.update(query, id);

    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EducationDay save(EducationDay educationDay)
    {
        if (educationDay == null)
            throw new NullPointerException("educationDay arg cannot be null");
        else if (educationDay.getWeekNumber() <= 0)
            throw new NullPointerException("educationDay weekNumber cannot be less or equal 0");
        else if (educationDay.getDate() == null)
            throw new NullPointerException("educationDay date cannot be null");
        else if (educationDay.getSubject() == null)
            throw new NullPointerException("educationDay subject cannot be null");
        else if (educationDay.getClassNumber() <= 0)
            throw new NullPointerException("educationDay classNumber cannot be less or equal 0");
        else if (educationDay.getGroup() == null || educationDay.getGroup().isEmpty())
            throw new NullPointerException("educationDay group cannot be null or empty");
        for (Group group : educationDay.getGroup())
        {
            if (groupDao.findById(group.getId()) == null)
                throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent educationDay entity before an independent group entity");
        }
        if (educationDay.getUser() != null && userDao.findById(educationDay.getUser().getId()) == null)
            throw new TheDependentEntityIsPreservedBeforeTheIndependentEntity("Trying to save a dependent educationDay entity before an independent user entity");
        if (educationDay.getId() != 0 && findById(educationDay.getId()) != null)
        {
            final String query = """
                    UPDATE education_day SET week_number = ?, user_id = ?, "date" = ?, class_number = ?,
                                      audience = ?, subject_id = ? WHERE id = ?;
                    """;
            jdbcTemplate.update(query, educationDay.getWeekNumber(), educationDay.getUser() == null ? null : educationDay.getUser().getId(),
                    educationDay.getDate(), educationDay.getClassNumber(), educationDay.getAudience() <= 0 ? null : educationDay.getAudience(),
                    educationDay.getSubject().getId(), educationDay.getId());
            if (educationDay.getGroup() != null && !educationDay.getGroup().isEmpty())
            {
                String groupQuery = "SELECT id, group_id, education_day_id FROM education_day_group WHERE group_id = ? AND education_day_id = ?";
                for (Group group : educationDay.getGroup())
                {
                    SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(groupQuery, group.getId(), educationDay.getId());
                    if (sqlRowSet.first())
                    {
                        long id = sqlRowSet.getLong("id");
                        String groupUpdate = "UPDATE education_day_group SET group_id = ?, education_day_id = ? WHERE id = ?";
                        jdbcTemplate.update(groupUpdate, group.getId(), educationDay.getId(), id);
                    }
                    String groupRelationSave = "INSERT INTO education_day_group(group_id, education_day_id) VALUES(?, ?)";
                    jdbcTemplate.update(groupRelationSave, group.getId(), educationDay.getId());
                }
                String allRelationsQuery = """
                        SELECT education_day_group.id, gr.name group_name, group_id, education_day_id
                        FROM education_day_group
                        JOIN "group" gr ON gr.id = group_id
                        WHERE education_day_id = ?
                        """;
                SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(allRelationsQuery, educationDay.getId());
                while (sqlRowSet.next())
                {
                    Group group = new Group();
                    group.setId(sqlRowSet.getInt("group_id"));
                    group.setName(sqlRowSet.getString("group_name"));
                    if (educationDay.getGroup().contains(group))
                    {
                        String deleteRelationQuery = "DELETE FROM education_day_group WHERE id = ?";
                        jdbcTemplate.update(deleteRelationQuery, sqlRowSet.getInt("id"));
                    }
                }
            }
            return findById(educationDay.getId());
        }
        final String query = """
                INSERT INTO education_day(week_number, user_id, "date", class_number, audience, subject_id)
                VALUES(?,?,?,?,?,?);
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementSaveCreator preparedStatementSaveCreator = new PreparedStatementSaveCreator();
        preparedStatementSaveCreator.setEducationDay(educationDay);
        preparedStatementSaveCreator.setQuery(query);
        jdbcTemplate.update(preparedStatementSaveCreator, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        final String groupRelationSave = "INSERT INTO education_day_group(group_id, education_day_id) VALUES(?, ?)";
        jdbcTemplate.batchUpdate(groupRelationSave, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException
            {
                Group group = educationDay.getGroup().get(i);
                ps.setLong(1, group.getId());
                ps.setLong(2, id);
            }

            @Override
            public int getBatchSize()
            {
                return educationDay.getGroup().size();
            }
        });
        return findById(id);
    }

    private List<EducationDay> fillDependence(List<EducationDay> educationDayList)
    {
        Map<Long, User> userMap = new HashMap<>();
        Map<Long, Group> groupMap = new HashMap<>();
        Map<Long, Subject> subjectMap = new HashMap<>();
        User user;
        Group group;
        Subject subject;
        for (EducationDay educationDay : educationDayList)
        {
            if (educationDay.getUser() != null)
            {
                long userId = educationDay.getUser().getId();
                user = userMap.get(userId);
                if (user == null)
                {
                    user = userDao.findById(userId);
                    userMap.put(userId, user);
                    educationDay.setUser(user);
                }
                else
                {
                    educationDay.setUser(user);
                }
            }

            if (educationDay.getSubject() != null)
            {
                long subjectId = educationDay.getSubject().getId();
                subject = subjectMap.get(subjectId);
                if (subject == null)
                {
                    subject = subjectDao.findById(subjectId);
                    subjectMap.put(subjectId, subject);
                    educationDay.setSubject(subject);
                }
                else
                {
                    educationDay.setSubject(subject);
                }
            }

            if (educationDay.getGroup() != null)
            {
                List<Group> groupList = educationDay.getGroup();
                for (int i = 0; i < groupList.size(); i++)
                {
                    long groupId = groupList.get(i).getId();
                    group = groupMap.get(groupId);
                    if (group == null)
                    {
                        group = groupDao.findById(groupId);
                        groupMap.put(groupId, group);
                        educationDay.getGroup().set(i, group);
                    }
                    else
                    {
                        educationDay.getGroup().set(i, group);
                    }
                }
            }
        }
        return educationDayList;
    }

    private static class EducationDayExtractor implements ResultSetExtractor<List<EducationDay>>
    {
        @Override
        public List<EducationDay> extractData(ResultSet rs) throws SQLException, DataAccessException
        {
            Map<Long, Set<Group>> educationDayGroup = new HashMap<>();
            Map<Long, EducationDay> educationDayMap = new HashMap<>();
            EducationDay educationDay;
            while (rs.next())
            {
                long educationDayId = rs.getLong("id");
                educationDay = educationDayMap.get(educationDayId);
                if (educationDay == null)
                {
                    educationDay = new EducationDay();
                    educationDay.setId(educationDayId);
                    DayOfWeek dayOfWeek = DayOfWeek.of(rs.getInt("week_number"));
                    educationDay.setDayOfWeek(dayOfWeek);
                    educationDay.setDate(rs.getDate("date").toLocalDate());
                    educationDay.setAudience(rs.getInt("audience"));
                    educationDay.setClassNumber(rs.getInt("class_number"));
                    educationDay.setWeekNumber(rs.getInt("week_number"));
                    long userId = rs.getLong("user_id");
                    if (userId != 0)
                    {
                        User user = new User();
                        user.setId(userId);
                        educationDay.setUser(user);
                    }
                    long subjectId = rs.getLong("subject_id");
                    if (subjectId != 0)
                    {
                        Subject subject = new Subject();
                        subject.setId(subjectId);
                        educationDay.setSubject(subject);
                    }
                    educationDayMap.put(educationDayId, educationDay);
                }

                long groupId = rs.getLong("group_id");
                if (groupId != 0) {
                    Set<Group> groupSet = educationDayGroup.get(educationDayId);
                    if (groupSet == null) {
                        groupSet = new HashSet<>();
                        Group group = new Group();
                        group.setId(groupId);
                        groupSet.add(group);
                        educationDayGroup.put(educationDayId, groupSet);
                    } else {
                        Group group = new Group();
                        group.setId(groupId);
                        groupSet.add(group);
                    }
                    educationDay.setGroup(new ArrayList<>(groupSet));
                }
            }
            return new ArrayList<>(educationDayMap.values());
        }
    }

    @Setter
    @Getter
    private static class PreparedStatementSaveCreator implements PreparedStatementCreator
    {
        private EducationDay educationDay;
        private String query;
        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException
        {
            PreparedStatement preparedStatement = con.prepareStatement(query, new String[] {"id"});
            preparedStatement.setInt(1, educationDay.getWeekNumber());
            if (educationDay.getUser() != null)
                preparedStatement.setLong(2, educationDay.getUser().getId());
            else
                preparedStatement.setNull(2, Types.NULL);
            preparedStatement.setDate(3, Date.valueOf(educationDay.getDate()));
            preparedStatement.setInt(4, educationDay.getClassNumber());
            if (educationDay.getAudience() > 0)
                preparedStatement.setInt(5, educationDay.getAudience());
            else
                preparedStatement.setNull(5, Types.NULL);
            preparedStatement.setLong(6, educationDay.getSubject().getId());
            return preparedStatement;
        }
    }
}
