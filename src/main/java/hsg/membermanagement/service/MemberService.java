package hsg.membermanagement.service;

import hsg.membermanagement.domain.entity.Member;
import hsg.membermanagement.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public Member create(final Member member) {
        if(member == null || member.getEmail() == null ) {
            throw new RuntimeException("Invalid arguments");
        }
        final String email = member.getEmail();
        if(memberRepository.existsByEmail(email)) {
            log.warn("Email already exists {}", email);
            throw new RuntimeException("Email already exists");
        }

        return memberRepository.save(member);
    }

    public Member getByCredentials(final String email, final String password, final PasswordEncoder encoder) {
        final Member originalMember = memberRepository.findByEmail(email);

        // matches 메서드를 이용해 패스워드가 같은지 확인
        if(originalMember != null && encoder.matches(password, originalMember.getPassword())) {
            return originalMember;
        }
        return null;
    }
}
