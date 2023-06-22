package com.cglia.batch.dao;


import org.springframework.batch.item.ItemProcessor;

import com.cglia.model.Student;

public class StudentProcessor implements ItemProcessor<Student, Student> {

    @Override
    public Student process(Student student) throws Exception {
        // Apply any processing logic here
    	
    	// Condition only nagpur person save 
//       if(student.getAddress().equals("nagpur")) {
//           return student;
//       }else{
//           return null;
//       }
        return student;
    }
}
