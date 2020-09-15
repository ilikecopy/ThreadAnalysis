package com.yiqin.tool.thread.analysis.samples.mysql.dao;

import org.springframework.data.repository.CrudRepository;

import com.yiqin.tool.thread.analysis.samples.mysql.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
