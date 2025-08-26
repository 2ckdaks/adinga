package com.adinga.todo_service.service;

import com.adinga.todo_service.domain.Todo;
import com.adinga.todo_service.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adinga.todo_service.exception.TodoNotFoundException;

import java.util.List;

@Service
@Transactional
public class TodoService {

    private final TodoRepository repo;

    public TodoService(TodoRepository repo) {
        this.repo = repo;
    }

    /** 전체 목록 (비페이징) */
    @Transactional(readOnly = true)
    public List<Todo> findAll() {
        return repo.findAll();
    }

    /** 페이징/정렬 지원 목록 */
    @Transactional(readOnly = true)
    public Page<Todo> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /** 단건 조회 */
    @Transactional(readOnly = true)
    public Todo findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    /** 생성 */
    public Todo create(String title) {
        Todo t = new Todo();
        t.setTitle(title);
        return repo.save(t);
    }

    /** 부분 수정 */
    public Todo updatePartial(Long id, String title, Boolean completed) {
        Todo t = findById(id);
        if (title != null) t.setTitle(title);
        if (completed != null) t.setCompleted(completed);
        return repo.save(t);
    }

    /** 삭제 */
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
