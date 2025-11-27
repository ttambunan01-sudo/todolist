package com.miniproject.todolist.dto;

import com.miniproject.todolist.enums.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating an existing todo item. All fields are optional.")
public class TodoUpdateRequest {

    @Schema(description = "Updated title of the todo item", example = "Complete project documentation - Updated")
    private String title;

    @Schema(description = "Updated description of the todo item", example = "Write comprehensive documentation for all API endpoints including examples")
    private String description;

    @Schema(description = "Updated completion status", example = "true")
    private Boolean completed;

    @Schema(description = "Updated priority level", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private Priority priority;

    @Schema(description = "Updated due date and time", example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Updated comma-separated tags", example = "work,completed,project")
    private String tags;
}