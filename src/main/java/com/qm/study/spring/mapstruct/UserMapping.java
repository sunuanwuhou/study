package com.qm.study.spring.mapstruct;

import org.mapstruct.Mapper;
import org.springframework.core.convert.converter.Converter;

@Mapper(componentModel = "spring")
public interface UserMapping extends Converter<Student,User> {
    /**
     * Student 转化为 User
     * @return User
     */
    User convert(Student student);

}