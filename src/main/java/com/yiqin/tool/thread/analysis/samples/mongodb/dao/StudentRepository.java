package com.yiqin.tool.thread.analysis.samples.mongodb.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yiqin.tool.thread.analysis.samples.mongodb.entity.Student;

public interface StudentRepository extends MongoRepository<Student, String> {

}
