package com.example.sampleapp.dal.repo;

import com.example.sampleapp.dal.entity.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepo extends CrudRepository<Employee, Integer> {
}
