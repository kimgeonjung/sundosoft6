package edu.du.project2.controller;

import edu.du.project2.dto.MessageDto;
import edu.du.project2.dto.ProfileDto;
import edu.du.project2.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class MyDataController {
    private final ProfileService profileService;

    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "/common/messageRedirect";
    }
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (session.getAttribute("my") == null) {
            MessageDto message = new MessageDto("로그인이 필요한 서비스입니다", "/", RequestMethod.GET, null);
            return showMessageAndRedirect(message, model);
        }
        ProfileDto my = (ProfileDto) session.getAttribute("my");


        // 모델에 'my' 객체 추가
        model.addAttribute("my", my);
        return "user/profile"; // 프로필 페이지 반환
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute ProfileDto profileDto,HttpSession session, RedirectAttributes redirectAttributes) {
        profileService.updateProfile(profileDto);
        session.setAttribute("my", profileDto);
        redirectAttributes.addFlashAttribute("message", "프로필 정보가 변경되었습니다.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/profile";
    }
}
