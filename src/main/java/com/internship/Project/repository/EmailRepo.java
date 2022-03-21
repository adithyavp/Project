package com.internship.Project.repository;

import com.internship.Project.entity.EmailDetails;
import org.springframework.data.repository.CrudRepository;

public interface EmailRepo extends CrudRepository<EmailDetails, Long> {



}
