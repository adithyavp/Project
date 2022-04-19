package com.internship.Project.repository;

import com.internship.Project.entity.Jobs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobsRepo extends CrudRepository<Jobs, Long> {

    Jobs findByJobIdAndName(Long jobId, String name);

    Jobs findByName(String name);

    List<Jobs> findByJobWorkingStatusAndAndMemoryType(String jobWorkingStatus, String memoryType);

    Jobs findByJobId(Long jobId);

    List<Jobs> findByJobWorkingStatus(String jobWorkingStatus);

//    @Modifying(clearAutomatically=true)
//    @Transactional
//    @Query(value = "SELECT * FROM Jobs j WHERE j.job_id = ?1",nativeQuery = true)
//    public Jobs FindJobByJobID(Long jobId);

//    @Modifying(clearAutomatically=true)
//    @Transactional
//    @Query(value = "update QRTZ_SCHEDULER_STATE set INSTANCE_NAME = ?1",nativeQuery = true)
//    public void setInstanceNameInDB(String InstanceName);

}