package com.vbs.demo.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import com.vbs.demo.models.User;

import java.util.List;


public interface UserRepo extends JpaRepository<User,Integer>
{

   

    User findByUsername(String username);

    User findByName(String value);

    User findByEmail(String value);

    List<User> findAllByRole(String customer, Sort sort);

    User findByusername(String username);

    List<User> findAllByUsernameContainingIgnoreCaseAndRole(String keyword, String customer);
}
