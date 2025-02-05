package edu.du.project2.controller;

import edu.du.project2.dto.AuthInfo;
import edu.du.project2.dto.MessageDto;
import edu.du.project2.entity.Apply;
import edu.du.project2.repository.ApplyRepository;
import edu.du.project2.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 분석 신청서 관련 컨트롤러.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class ApplyController {
    private final ApplyService applyService;
    private final ApplyRepository applyRepository;

    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "common/messageRedirect";
    }

    // 기본 신청서 목록 페이지를 반환
    @GetMapping("/normalList")
    public String normalList(Model model, HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        // 로그인한 사용자에 따른 신청서 목록 가져오기
        List<Apply> applies = applyRepository.findAllByUidAndRequest(authInfo.getId(),"normal");

        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("applies", applies);
        return "map/normalApply_list";
    }

    // 상세 신청서 목록 페이지를 반환
    @GetMapping("/detailList")
    public String detailList(Model model, HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        // 로그인한 사용자에 따른 신청서 목록 가져오기
        List<Apply> applies = applyRepository.findAllByUidAndRequest(authInfo.getId(),"detail");
        System.out.println(applies);
        model.addAttribute("total", applyService.totalApplies(authInfo, "detail"));
        model.addAttribute("now", LocalDateTime.now());
        model.addAttribute("applies", applies);
        return "map/detailApply_list";
    }

    // 신청서 상세 페이지를 반환
    @GetMapping("/normalDetail")
    public String normalDetail(@RequestParam Long id, Model model) {
        Apply apply = applyService.selectApplyDetail(id);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(apply);
        model.addAttribute("apply", apply);
        model.addAttribute("now", now);
        return "map/normalApply_detail";
    }

    // 신청서 상세 페이지를 반환
    @GetMapping("/detailDetail")
    public String detailDetail(@RequestParam Long id, Model model) {
        Apply apply = applyService.selectApplyDetail(id);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(apply);
        model.addAttribute("apply", apply);
        model.addAttribute("now", now);
        return "map/detailApply_detail";
    }

    // 신청서 작성 페이지를 반환
    @GetMapping("")
    public String write(HttpSession session, Model model) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (session.getAttribute("authInfo") == null) {
            MessageDto message = new MessageDto("로그인이 필요한 서비스입니다", "/", RequestMethod.GET, null);
            return showMessageAndRedirect(message, model);
        }
        return "map/apply_write";
    }

    /**
     * 신청서를 저장.
     *
     * @param author  작성자
     * @param title   제목
     * @param content 내용
     * @param files   첨부 파일 배열
     * @return 신청서 목록 페이지로 리다이렉트
     * @throws IOException 파일 저장 실패 시 발생
     */
    @PostMapping("/analysisApply")
    public String save(@RequestParam String author,
                                @RequestParam String title,
                                @RequestParam String content,
                                @RequestParam(value = "files", required = false) MultipartFile[] files,HttpSession session) throws IOException {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (files == null || files.length == 0 || files[0].isEmpty()) {
            applyService.createBoard(author, title, content, authInfo);
        } else {
            applyService.createBoard(author, title, content, files, authInfo);
        }
        return "redirect:/analysis/normalList";
    }

    // 신청서를 수정
    @PostMapping("/analysisNormalUpdate")
    public String normalUpdate(Apply apply){
        applyService.updateApply(apply);
        return "redirect:/analysis/normalList";
    }

    // 신청서를 수정
    @PostMapping("/analysisDetailUpdate")
    public String detailUpdate(Apply apply){
        applyService.updateApply(apply);
        return "redirect:/analysis/detailList";
    }

    // 신청서를 삭제
    @PostMapping("/analysisNormalDelete")
    public String normalDelete(@RequestParam Long id){
        applyService.deleteApply(id);
        return "redirect:/analysis/normalList";
    }

    // 신청서를 삭제
    @PostMapping("/analysisDetailDelete")
    public String detailDelete(@RequestParam Long id){
        applyService.deleteApply(id);
        return "redirect:/analysis/detailList";
    }

    // 신청 결과 확인
    @GetMapping("/map/map_result")
    public String index(){
        return "map/map_result";
    }

    @PostMapping("/analysisResult")
    public String result(Apply apply, Model model) throws IOException {
        String location = apply.getLocation();
        String type = apply.getType();
        // 파일 내용 읽기
        String filePath = apply.getLink();
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        System.out.println(apply);
        System.out.println(location);
        System.out.println(type);
        model.addAttribute("location", location);
        model.addAttribute("type", type);
        model.addAttribute("content", content);  // 파일 내용을 전달

        return "map/map_result";
    }
}
