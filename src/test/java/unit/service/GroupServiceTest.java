package unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.letunov.dao.GroupDao;
import org.letunov.domainModel.Group;
import org.letunov.service.impl.GroupServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest
{
    @Mock
    GroupDao groupDao;

    @InjectMocks
    GroupServiceImpl groupService;

    @Test
    public void getGroupsNamesShouldConvertGroupsToNameList()
    {
        List<Group> groupList = new ArrayList<>();
        List<String> names = new ArrayList<>();
        Group group = new Group();
        group.setId(1);
        String name = "БСБО-01-21";
        group.setName(name);
        names.add(name);
        groupList.add(group);
        group = new Group();
        group.setId(2);
        name = "БСБО-02-21";
        group.setName(name);
        names.add(name);
        groupList.add(group);
        group = new Group();
        group.setId(3);
        name = "БСБО-03-21";
        group.setName(name);
        names.add(name);
        groupList.add(group);

        int page = 0;
        int size = 10;
        Pageable pageRequest = PageRequest.of(page, size);
        when(groupDao.findAllOrderByNameAsc(anyInt(), anyInt())).thenReturn(new PageImpl<>(groupList, pageRequest, groupList.size()));

        assertEquals(names, groupService.getGroupsNames());
    }
}
