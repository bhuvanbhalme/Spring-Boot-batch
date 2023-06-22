package com.cglia.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

	private final JobLauncher jobLauncher;
	private final Job importStudentsJob;
	
	private final Job exportStudentsJob;

	@Autowired
	public JobController(JobLauncher jobLauncher, Job importStudentsJob,Job exportStudentsJob) {
		this.jobLauncher = jobLauncher;
		this.importStudentsJob = importStudentsJob;
		this.exportStudentsJob=exportStudentsJob;
	}

	@PostMapping("/importStudents")
	public void importCsvToDBJob() {
		JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
				.toJobParameters();
		try {
			jobLauncher.run(importStudentsJob, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}
	
	@PostMapping("/exportStudents")
	public void exportStudentsToCsv() {
	    JobParameters jobParameters = new JobParametersBuilder()
	            .addLong("startAt", System.currentTimeMillis())
	            .toJobParameters();
	    try {
	        jobLauncher.run(exportStudentsJob, jobParameters);
	    } catch (JobExecutionAlreadyRunningException | JobRestartException |
	            JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
	        e.printStackTrace();
	    }
	}

}
