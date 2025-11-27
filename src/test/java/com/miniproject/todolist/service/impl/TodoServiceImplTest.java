package com.miniproject.todolist.service.impl;

import com.miniproject.todolist.dto.TodoCreateRequest;
import com.miniproject.todolist.dto.TodoResponse;
import com.miniproject.todolist.dto.TodoUpdateRequest;
import com.miniproject.todolist.entity.Todo;
import com.miniproject.todolist.enums.Priority;
import com.miniproject.todolist.exception.TodoNotFoundException;
import com.miniproject.todolist.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceImplTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    private Todo mockTodo;
    private TodoCreateRequest createRequest;
    private TodoUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockTodo = new Todo();
        mockTodo.setId(1L);
        mockTodo.setTitle("Test Todo");
        mockTodo.setDescription("Test Description");
        mockTodo.setCompleted(false);
        mockTodo.setPriority(Priority.MEDIUM);
        mockTodo.setDueDate(LocalDateTime.now().plusDays(7));
        mockTodo.setTags("test,sample");
        mockTodo.setCreatedAt(LocalDateTime.now());
        mockTodo.setUpdatedAt(LocalDateTime.now());

        createRequest = new TodoCreateRequest();
        createRequest.setTitle("New Todo");
        createRequest.setDescription("New Description");
        createRequest.setCompleted(false);
        createRequest.setPriority(Priority.HIGH);
        createRequest.setDueDate(LocalDateTime.now().plusDays(3));
        createRequest.setTags("urgent");

        updateRequest = new TodoUpdateRequest();
        updateRequest.setTitle("Updated Todo");
        updateRequest.setCompleted(true);
    }

    @Test
    void testCreateTodo_Success() {
        // Arrange
        when(todoRepository.save(any(Todo.class))).thenReturn(mockTodo);

        // Act
        TodoResponse response = todoService.createTodo(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(mockTodo.getId(), response.getId());
        assertEquals(mockTodo.getTitle(), response.getTitle());
        assertEquals(mockTodo.getDescription(), response.getDescription());
        assertEquals(mockTodo.getCompleted(), response.getCompleted());
        assertEquals(mockTodo.getPriority(), response.getPriority());
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testCreateTodo_WithNullCompleted_DefaultsToFalse() {
        // Arrange
        createRequest.setCompleted(null);
        when(todoRepository.save(any(Todo.class))).thenReturn(mockTodo);

        // Act
        TodoResponse response = todoService.createTodo(createRequest);

        // Assert
        assertNotNull(response);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testGetTodoById_Found() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(mockTodo));

        // Act
        TodoResponse response = todoService.getTodoById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(mockTodo.getId(), response.getId());
        assertEquals(mockTodo.getTitle(), response.getTitle());
        assertEquals(mockTodo.getDescription(), response.getDescription());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTodoById_NotFound_ThrowsException() {
        // Arrange
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.getTodoById(999L);
        });
        verify(todoRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAllTodos_Success() {
        // Arrange
        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(mockTodo));
        when(todoRepository.findAll(any(Pageable.class))).thenReturn(todoPage);

        // Act
        Page<TodoResponse> response = todoService.getAllTodos(0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(mockTodo.getTitle(), response.getContent().get(0).getTitle());
        verify(todoRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testUpdateTodo_Success() {
        // Arrange
        when(todoRepository.findById(1L)).thenReturn(Optional.of(mockTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(mockTodo);

        // Act
        TodoResponse response = todoService.updateTodo(1L, updateRequest);

        // Assert
        assertNotNull(response);
        verify(todoRepository, times(1)).findById(1L);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testUpdateTodo_NotFound_ThrowsException() {
        // Arrange
        when(todoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.updateTodo(999L, updateRequest);
        });
        verify(todoRepository, times(1)).findById(999L);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    void testUpdateTodo_PartialUpdate() {
        // Arrange
        TodoUpdateRequest partialUpdate = new TodoUpdateRequest();
        partialUpdate.setTitle("Only Title Updated");
        // Other fields are null - should not update

        when(todoRepository.findById(1L)).thenReturn(Optional.of(mockTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(mockTodo);

        // Act
        TodoResponse response = todoService.updateTodo(1L, partialUpdate);

        // Assert
        assertNotNull(response);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void testDeleteTodo_Success() {
        // Arrange
        when(todoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(1L);

        // Act
        todoService.deleteTodo(1L);

        // Assert
        verify(todoRepository, times(1)).existsById(1L);
        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTodo_NotFound_ThrowsException() {
        // Arrange
        when(todoRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(TodoNotFoundException.class, () -> {
            todoService.deleteTodo(999L);
        });
        verify(todoRepository, times(1)).existsById(999L);
        verify(todoRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFilterByCompleted_Success() {
        // Arrange
        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(mockTodo));
        when(todoRepository.findByCompleted(anyBoolean(), any(Pageable.class))).thenReturn(todoPage);

        // Act
        Page<TodoResponse> response = todoService.filterByCompleted(false, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(todoRepository, times(1)).findByCompleted(eq(false), any(Pageable.class));
    }

    @Test
    void testSearchByTitle_Success() {
        // Arrange
        Page<Todo> todoPage = new PageImpl<>(Arrays.asList(mockTodo));
        when(todoRepository.findByTitleContainingIgnoreCase(anyString(), any(Pageable.class))).thenReturn(todoPage);

        // Act
        Page<TodoResponse> response = todoService.searchByTitle("Test", 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Test Todo", response.getContent().get(0).getTitle());
        verify(todoRepository, times(1)).findByTitleContainingIgnoreCase(eq("Test"), any(Pageable.class));
    }
}
