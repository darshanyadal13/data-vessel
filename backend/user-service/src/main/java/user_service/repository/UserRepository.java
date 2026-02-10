package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import user_service.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

    Users findUserByUsername(String username);

}
