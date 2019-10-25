package com.siteparser.MyOwnSiteParser.repository;

import com.siteparser.MyOwnSiteParser.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("from User u where u.email = :email")
    User findByEmail(@Param("email") String email);
}
