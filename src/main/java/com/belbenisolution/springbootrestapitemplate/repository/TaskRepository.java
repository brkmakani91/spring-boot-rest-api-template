package com.belbenisolution.springbootrestapitemplate.repository;

import com.belbenisolution.springbootrestapitemplate.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> { }