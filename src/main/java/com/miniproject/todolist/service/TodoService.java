package com.miniproject.todolist.service;

import com.miniproject.todolist.dto.TodoCreateRequest;
import com.miniproject.todolist.dto.TodoResponse;
import com.miniproject.todolist.dto.TodoUpdateRequest;
import org.springframework.data.domain.Page;

public interface TodoService {

    TodoResponse createTodo(TodoCreateRequest request);

    TodoResponse getTodoById(Long id);

    Page<TodoResponse> getAllTodos(int page, int size);

    TodoResponse updateTodo(Long id, TodoUpdateRequest request);

    void deleteTodo(Long id);

    Page<TodoResponse> filterByCompleted(Boolean completed, int page, int size);

    Page<TodoResponse> searchByTitle(String query, int page, int size);
}