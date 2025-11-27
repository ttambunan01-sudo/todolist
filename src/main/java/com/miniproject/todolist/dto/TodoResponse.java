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
@Schema(description = "Response object containing todo item details")
public class TodoResponse {

    @Schema(description = "Unique identifier of the todo item", example = "1")
    private Long id;

    @Schema(description = "Title of the todo item", example = "Complete project documentation")
    private String title;

    @Schema(description = "Detailed description of the todo item", example = "Write comprehensive documentation for the API endpoints")
    private String description;

    @Schema(description = "Whether the todo is completed", example = "false")
    private Boolean completed;

    @Schema(description = "Priority level of the todo", example = "HIGH")
    private Priority priority;

    @Schema(description = "Due date and time for the todo", example = "2025-12-31T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Comma-separated tags for categorization", example = "work,urgent,project")
    private String tags;

    @Schema(description = "Timestamp when the todo was created", example = "2025-11-26T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the todo was last updated", example = "2025-11-26T15:30:00")
    private LocalDateTime updatedAt;
}