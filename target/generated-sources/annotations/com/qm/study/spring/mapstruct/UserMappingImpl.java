package com.qm.study.spring.mapstruct;

import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-10-15T14:50:26+0800",
    comments = "version: 1.1.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
@Component
public class UserMappingImpl implements UserMapping {

    @Override
    public User convert(Student student) {
        if ( student == null ) {
            return null;
        }

        User user = new User();

        user.setId( student.getId() );
        user.setName( student.getName() );
        user.setAge( student.getAge() );
        user.setSex( student.getSex() );

        return user;
    }
}
