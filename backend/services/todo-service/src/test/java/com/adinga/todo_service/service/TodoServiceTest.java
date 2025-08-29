package com.adinga.todo_service.service;

import com.adinga.todo_service.domain.Todo;
import com.adinga.todo_service.exception.TodoNotFoundException;
import com.adinga.todo_service.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    TodoRepository repo;

    TodoService service;

    @BeforeEach
    void setUp() {
        service = new TodoService(repo);
    }

    @Test
    void findById_found() {
        Todo t = new Todo();
        t.setId(1L); t.setTitle("hello");
        when(repo.findById(1L)).thenReturn(Optional.of(t));

        Todo got = service.findById(1L);

        assertThat(got.getId()).isEqualTo(1L);
        assertThat(got.getTitle()).isEqualTo("hello");
    }

    @Test
    void findById_notFound_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TodoNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_setsTitle_andSaves() {
        when(repo.save(any(Todo.class))).thenAnswer(inv -> {
            Todo x = inv.getArgument(0);
            x.setId(10L);
            return x;
        });

        Todo created = service.create("new title");

        verify(repo).save(any(Todo.class));
        assertThat(created.getId()).isEqualTo(10L);
        assertThat(created.getTitle()).isEqualTo("new title");
    }

    @Test
    void updatePartial_titleOnly_changesTitle() {
        Todo t = new Todo(); t.setId(1L); t.setTitle("old"); t.setCompleted(false);
        when(repo.findById(1L)).thenReturn(Optional.of(t));
        when(repo.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo updated = service.updatePartial(1L, "new", null);

        assertThat(updated.getTitle()).isEqualTo("new");
        assertThat(updated.getCompleted()).isFalse();
    }

    @Test
    void updatePartial_completedOnly_changesCompleted() {
        Todo t = new Todo(); t.setId(1L); t.setTitle("keep"); t.setCompleted(false);
        when(repo.findById(1L)).thenReturn(Optional.of(t));
        when(repo.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo updated = service.updatePartial(1L, null, true);

        assertThat(updated.getTitle()).isEqualTo("keep");
        assertThat(updated.getCompleted()).isTrue();
    }

    @Test
    void delete_callsRepository() {
        service.delete(7L);
        verify(repo).deleteById(7L);
    }
}
