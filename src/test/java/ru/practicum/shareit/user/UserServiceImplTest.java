package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;
    private final UserMapper mapper;

    @AfterEach
    void init() {
        TransactionTemplate.execute(transactionStatus -> {
            em.createQuery("Delete from User")
                    .executeUpdate();
            transactionStatus.flush();
            return null;
        });

    }


    @Test
    void saveUser() {
        // given
        UserDto userDto = makeUserDto("some@email.com", "Пётр");

        // when
        service.create(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getAllUsers() {
        // given
        List<UserDto> sourceUsers = Arrays.asList(
                makeUserDto("ivan@email", "Ivan"),
                makeUserDto("petr@email", "Petr"),
                makeUserDto("vasilii@email", "Vasilii"));

        for (UserDto user : sourceUsers) {
            service.create(user);
        }

        // when
        List<User> targetUsers = service.getAll();

        // then
        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void updateUser() {
        // given
        UserDto userDto = makeUserDto("some@email.com", "Пётр");

        // when
        UserDto createdUser = service.create(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userBeforeUpdate = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        createdUser.setName("Иван");

        service.update(createdUser);

        // then
        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userAfterUpdate = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(userAfterUpdate.getId(), notNullValue());
        assertThat(userAfterUpdate.getId(), equalTo(userBeforeUpdate.getId()));
        assertThat(userAfterUpdate.getName(), equalTo(createdUser.getName()));
        assertThat(userAfterUpdate.getEmail(), equalTo(userBeforeUpdate.getEmail()));
    }

    @Test
    void getUser() {
        // given
        UserDto userDto = makeUserDto("some@email.com", "Пётр");

        // when
        UserDto createdUser = service.create(userDto);

        // then
        User user = mapper.toUser(service.getUser(createdUser.getId()));

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            User notFoundUser = mapper.toUser(service.getUser(createdUser.getId() + 1));
        });

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void deleteUser() {
        // given
        UserDto userDto = makeUserDto("some@email.com", "Пётр");

        // when
        UserDto createdUser = service.create(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", createdUser.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        service.delete(createdUser.getId());

        Assertions.assertThrows(NoResultException.class, () -> {
            TypedQuery<User> typedQuery = em.createQuery("Select u from User u where u.id = :id", User.class);
            User deletedUser = typedQuery.setParameter("id", createdUser.getId())
                    .getSingleResult();
        });
    }

    private UserDto makeUserDto(String email, String name) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }

}