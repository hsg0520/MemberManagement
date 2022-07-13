package hsg.membermanagement.repository;

import hsg.membermanagement.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Member findByEmail(String email);
    Boolean existsByEmail(String email);

    Member findByEmailAndPassword(String email, String password);
}
