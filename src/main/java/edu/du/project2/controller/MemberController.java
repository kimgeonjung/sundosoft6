package edu.du.project2.controller;


import edu.du.project2.dto.AuthInfo;
import edu.du.project2.dto.LoginDto;
import edu.du.project2.dto.MemberRequest;
import edu.du.project2.dto.ProfileDto;
import edu.du.project2.entity.Member;
import edu.du.project2.repository.MemberRepository;
import edu.du.project2.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (authInfo != null) {
            model.addAttribute("isLoggedIn", true);  // 로그인된 상태
        } else {
            model.addAttribute("isLoggedIn", false); // 로그인되지 않은 상태
        }
        return "main/home";
    }

    @GetMapping("/register")
    public String register() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(MemberRequest request, Model model, RedirectAttributes redirectAttributes) {
        String result = memberService.registerMember(request);
        if ("이미 존재하는 이메일입니다.".equals(result)) {
            model.addAttribute("error", "이미 존재하는 이메일입니다.");
            return "user/register";
        }
        redirectAttributes.addFlashAttribute("message", "회원가입이 성공적으로 완료되었습니다!");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login"; // 로그인 폼 페이지
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto dto, Model model, HttpSession session) {
        boolean isLogin = memberService.loginMember(dto);
        log.info("로그인 isLogin: {}", isLogin);
        if (!isLogin) {
            model.addAttribute("error", "ID 또는 비밀번호가 틀립니다.");
            return "user/login";
        }
        Member member = memberRepository.findByLoginId(dto.getLoginId()).orElse(null); // 이메일로 회원 조회
        if (member != null && member.isAdmin()) { // 관리자인지 확인
            AuthInfo authInfo = new AuthInfo(member.getId(), member.getEmail(), member.getName(), member.getRole()); // 관리자 정보를 AuthInfo에 입력
            session.setAttribute("authInfo", authInfo); // 세션에 AuthInfo 저장
            return "redirect:/admin"; // 관리자는 관리자 페이지로 리디렉션
        }
        AuthInfo authInfo = new AuthInfo(member.getId(), member.getEmail(), member.getName(), member.getRole()); // 회원 정보를 AuthInfo에 입력
        session.setAttribute("authInfo", authInfo); // 세션에 AuthInfo 저장
        ProfileDto my= new ProfileDto(member.getId(),member.getLoginId(),member.getEmail(),member.getName(),
                member.getTel(),member.getZipcode(),member.getAddress(),member.getDetailAddress());
        session.setAttribute("my",my);

        return "redirect:/"; // 일반 사용자는 홈 페이지로 리디렉션
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();  // 세션 무효화 (로그아웃 처리)
        redirectAttributes.addFlashAttribute("message", "로그아웃 하였습니다.");
        return "redirect:/login";  // 로그인 페이지로 리디렉션
    }

    @GetMapping("/findId")
    public String findIdPage() {
        return "user/findId";  // idfor.html로 이동
    }

    @GetMapping("/findPw")
    public String findPwPage() {
        return "user/findPw";  // passwordfor.html로 이동
    }

}
