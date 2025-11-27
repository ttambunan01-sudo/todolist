package com.miniproject.todolist.repository;

import com.miniproject.todolist.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByCompleted(Boolean completed, Pageable pageable);

    Page<Todo> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}