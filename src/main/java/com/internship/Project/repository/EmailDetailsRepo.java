package com.internship.Project.repository;

import com.internship.Project.entity.EmailDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailDetailsRepo extends CrudRepository<EmailDetails, Long> {

    EmailDetails findByEmailIdAndJobClassName(String emailId, String jobClassName);

    EmailDetails findBySrNoAndEmailId(Long srNo, String emailId);

}
