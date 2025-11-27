package com.miniproject.todolist.dto;

import com.miniproject.todolist.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new todo item")
public class TodoCreateRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Title of the todo item", example = "Complete project documentation", required = true)
    private String title;

    @Schema(description = "Detailed description of the todo item", example = "Write comprehensive documentation for the API endpoints")
    private String description;

    @Schema(description = "Whether the todo is completed", example = "false", defaultValue = "false")
    private Boolean completed = false;

    @Schema(description = "Priority level of the todo", example = "HIGH", defaultValue = "MEDIUM", allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private Priority priority = Priority.MEDIUM;

    @Schema(description = "Due date and time for the todo", example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Comma-separated tags for categorization", example = "work,urgent,project")
    private String tags;
}