package com.miniproject.todolist.controller;

import com.miniproject.todolist.dto.TodoCreateRequest;
import com.miniproject.todolist.dto.TodoResponse;
import com.miniproject.todolist.dto.TodoUpdateRequest;
import com.miniproject.todolist.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Tag(name = "Todo", description = "Todo management APIs")
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    @Operation(
            summary = "Create a new todo",
            description = "Creates a new todo item with the provided details including title, description, priority, due date, and tags"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Todo created successfully",
                    content = @Content(schema = @Schema(implementation = TodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<TodoResponse> createTodo(
            @Parameter(description = "Todo creation request body", required = true)
            @Valid @RequestBody TodoCreateRequest request) {
        TodoResponse response = todoService.createTodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
            summary = "Get all todos with pagination",
            description = "Retrieves a paginated list of all todo items"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved todos",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public ResponseEntity<Page<TodoResponse>> getAllTodos(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Page<TodoResponse> todos = todoService.getAllTodos(page, size);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get todo by ID",
            description = "Retrieves a specific todo item by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Todo found",
                    content = @Content(schema = @Schema(implementation = TodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Todo not found"
            )
    })
    public ResponseEntity<TodoResponse> getTodoById(
            @Parameter(description = "ID of the todo to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        TodoResponse response = todoService.getTodoById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update todo by ID",
            description = "Updates an existing todo item with new values. All fields are optional."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Todo updated successfully",
                    content = @Content(schema = @Schema(implementation = TodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Todo not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data"
            )
    })
    public ResponseEntity<TodoResponse> updateTodo(
            @Parameter(description = "ID of the todo to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Todo update request body", required = true)
            @RequestBody TodoUpdateRequest request) {
        TodoResponse response = todoService.updateTodo(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete todo by ID",
            description = "Permanently deletes a todo item from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Todo deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Todo not found"
            )
    })
    public ResponseEntity<Void> deleteTodo(
            @Parameter(description = "ID of the todo to delete", required = true, example = "1")
            @PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    @Operation(
            summary = "Filter todos by completion status",
            description = "Retrieves todos filtered by their completion status with pagination support"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered todos",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public ResponseEntity<Page<TodoResponse>> filterByCompleted(
            @Parameter(description = "Completion status to filter by", required = true, example = "true")
            @RequestParam Boolean completed,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Page<TodoResponse> todos = todoService.filterByCompleted(completed, page, size);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search todos by title",
            description = "Searches for todos containing the specified query string in their title (case-insensitive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved search results",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public ResponseEntity<Page<TodoResponse>> searchByTitle(
            @Parameter(description = "Search query string", required = true, example = "meeting")
            @RequestParam String query,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Page<TodoResponse> todos = todoService.searchByTitle(query, page, size);
        return ResponseEntity.ok(todos);
    }
}