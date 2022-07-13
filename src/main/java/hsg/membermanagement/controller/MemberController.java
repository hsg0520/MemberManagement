package hsg.membermanagement.controller;

import hsg.membermanagement.domain.dto.MemberDto;
import hsg.membermanagement.domain.dto.ResponseDto;
import hsg.membermanagement.domain.entity.Member;
import hsg.membermanagement.security.TokenProvider;
import hsg.membermanagement.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody MemberDto memberDto) {
        try {
            // 리퀘스트를 이용해 저장할 유저 만들기
            Member member = Member.builder()
                    .email(memberDto.getEmail())
                    .username(memberDto.getUsername())
                    .password(passwordEncoder.encode(memberDto.getPassword()))
                    .build();
            // 서비스를 이용해 리파지토리에 유저 저장
            Member registeredMember = memberService.create(member);
            MemberDto responseMemberDto = MemberDto.builder()
                    .email(registeredMember.getEmail())
                    .id(registeredMember.getId())
                    .username(registeredMember.getUsername())
                    .build();
            // 유저 정보는 항상 하나이므로 그냥 리스트로 만들어야하는 ResponseDto를 사용하지 않고 그냥 MemberDto 리턴.
            return ResponseEntity.ok(responseMemberDto);
        } catch (Exception e) {
            // 예외가 나는 경우 bad 리스폰스 리턴.
            ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDto);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody MemberDto memberDTO) {
        Member member = memberService.getByCredentials(
                memberDTO.getEmail(),
                memberDTO.getPassword(),
                passwordEncoder);

        if(member != null) {
            // 토큰 생성
            final String token = tokenProvider.create(member);
            final MemberDto responseUserDto = MemberDto.builder()
                    .email(member.getUsername())
                    .id(member.getId())
                    .token(token)
                    .build();
            return ResponseEntity.ok().body(responseUserDto);
        } else {
            ResponseDto responseDto = ResponseDto.builder()
                    .error("Login failed.")
                    .build();
            return ResponseEntity
                    .badRequest()
                    .body(responseDto);
        }
    }
}
