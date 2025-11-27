package com.miniproject.todolist.service.impl;

import com.miniproject.todolist.dto.TodoCreateRequest;
import com.miniproject.todolist.dto.TodoResponse;
import com.miniproject.todolist.dto.TodoUpdateRequest;
import com.miniproject.todolist.entity.Todo;
import com.miniproject.todolist.exception.TodoNotFoundException;
import com.miniproject.todolist.repository.TodoRepository;
import com.miniproject.todolist.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    @CacheEvict(value = "todos", allEntries = true)
    public TodoResponse createTodo(TodoCreateRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);
        todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setTags(request.getTags());

        Todo savedTodo = todoRepository.save(todo);
        return mapToResponse(savedTodo);
    }

    @Override
    @Cacheable(value = "todos", key = "#id")
    public TodoResponse getTodoById(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        return mapToResponse(todo);
    }

    @Override
    public Page<TodoResponse> getAllTodos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Todo> todos = todoRepository.findAll(pageable);
        return todos.map(this::mapToResponse);
    }

    @Override
    @CacheEvict(value = "todos", allEntries = true)
    public TodoResponse updateTodo(Long id, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription());
        }
        if (request.getCompleted() != null) {
            todo.setCompleted(request.getCompleted());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            todo.setDueDate(request.getDueDate());
        }
        if (request.getTags() != null) {
            todo.setTags(request.getTags());
        }

        Todo updatedTodo = todoRepository.save(todo);
        return mapToResponse(updatedTodo);
    }

    @Override
    @CacheEvict(value = "todos", allEntries = true)
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    @Override
    public Page<TodoResponse> filterByCompleted(Boolean completed, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Todo> todos = todoRepository.findByCompleted(completed, pageable);
        return todos.map(this::mapToResponse);
    }

    @Override
    public Page<TodoResponse> searchByTitle(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Todo> todos = todoRepository.findByTitleContainingIgnoreCase(query, pageable);
        return todos.map(this::mapToResponse);
    }

    private TodoResponse mapToResponse(Todo todo) {
        TodoResponse response = new TodoResponse();
        response.setId(todo.getId());
        response.setTitle(todo.getTitle());
        response.setDescription(todo.getDescription());
        response.setCompleted(todo.getCompleted());
        response.setPriority(todo.getPriority());
        response.setDueDate(todo.getDueDate());
        response.setTags(todo.getTags());
        response.setCreatedAt(todo.getCreatedAt());
        response.setUpdatedAt(todo.getUpdatedAt());
        return response;
    }
}