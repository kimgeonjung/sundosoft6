package edu.du.project2.service;

import edu.du.project2.dto.ProfileDto;
import edu.du.project2.entity.Member;
import edu.du.project2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final MemberRepository memberRepository;

    public void updateProfile(ProfileDto profileDto) {
        // 데이터베이스에서 사용자를 조회한 후, 데이터를 업데이트합니다.
        Member member = memberRepository.findById(profileDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 프로필 정보 업데이트
        member.setLoginId(profileDto.getLoginId());
        member.setEmail(profileDto.getEmail());
        member.setName(profileDto.getName());
        member.setTel(profileDto.getTel());
        member.setZipcode(profileDto.getZipCode());
        member.setAddress(profileDto.getAddress());
        member.setDetailAddress(profileDto.getDetailAddress());

        // 변경된 정보를 데이터베이스에 저장
        memberRepository.save(member);
    }
}
