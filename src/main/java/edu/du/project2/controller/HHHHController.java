package edu.du.project2.controller;

import edu.du.project2.dto.AuthInfo;
import edu.du.project2.dto.MessageDto;
import edu.du.project2.entity.Member;
import edu.du.project2.entity.Notice;
import edu.du.project2.entity.QnA;
import edu.du.project2.entity.QnAList;
import edu.du.project2.repository.MemberRepository;
import edu.du.project2.repository.QnARepository;
import edu.du.project2.repository.QnA_ListRepository;
import edu.du.project2.service.NoticeService;
import edu.du.project2.service.QnAService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HHHHController {

    private final NoticeService noticeService;
    private final QnA_ListRepository qnAListRepository;
    private final MemberRepository memberRepository;
    private final QnARepository qnARepository;
    private final QnAService qnAService;

    private String showMessageAndRedirect(final MessageDto params, Model model) {
        model.addAttribute("params", params);
        return "/common/messageRedirect";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/adminPage";
    }


    @GetMapping("/admin_notice")
    public String noticeList(Model model) {
        List<Notice> notices = noticeService.getAllNotices();
        model.addAttribute("notices", notices);
        return "admin/admin_notice";
    }

    @GetMapping("/admin_qna")
    public String qnaList(Model model) {
        List<QnA> qna = qnARepository.findAll();
        List<QnAList> qnALists = qnAListRepository.findAll();
        List<Member> members = memberRepository.findAll();
        model.addAttribute("qna", qna);
        model.addAttribute("qnaList", qnALists);
        model.addAttribute("member", members);
        return "admin/admin_qna";
    }

    @GetMapping("/admin/admin_qnaDetail")
    public String qnaDetail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("qna", qnAService.getInquiryDetail(id));
        model.addAttribute("lists", qnAService.getInquiryReplies(id));
        QnAList qnAList = qnAService.getInquiryDetail(id);
        Member member = memberRepository.findById(qnAList.getUid()).get();
        model.addAttribute("member", member);
        return "/admin/admin_qnaDetail";
    }

    @PostMapping("/admin/inquiryInsert")
    public String qnaInsert(@ModelAttribute QnAList list,
                            @RequestParam String content,
                            HttpSession session) {
        qnAService.insertInquiry(list, content, session);
        return "redirect:/admin/admin_qnaDetail";
    }

    @PostMapping("/admin/answerInsert")
    public String answerInsert(@RequestParam String content,
                               @RequestParam String role,
                               @RequestParam Long id) {
        qnAService.insertAnswer(content, role, id);
        return "redirect:/admin/admin_qnaDetail?id=" + id;
    }

    @PostMapping("/admin/end/{id}")
    public String end(@PathVariable Long id, Model model) {
        QnAList qnAList = qnAListRepository.findById(id).get();
        qnAList.setEndYn('Y');
        qnAListRepository.save(qnAList);
        MessageDto message = new MessageDto("문의를 종료하였습니다.", "/admin_qna", RequestMethod.GET, null);
        return showMessageAndRedirect(message, model);
    }


    @GetMapping("/admin/admin_noticeWrite")
    public String noticeWrite(Model model) {
        return "admin/admin_noticeWrite";
    }

    @PostMapping("/admin/createNotice")
    public String createNotice(@RequestParam String title,
                               @RequestParam String content) {
        // 서비스 호출하여 공지사항 생성
        noticeService.createNotice(title, content);

        // 공지사항 작성 후 관리자 페이지로 리디렉션
        return "redirect:/admin_notice";
    }

    @GetMapping("/admin/noticeDetail")
    public String noticeDetail(@RequestParam Long id, Model model, HttpSession session) throws Exception {
        Notice notice = noticeService.selectNoticeDetail(id);
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        if (authInfo != null) {
            model.addAttribute("authInfo", authInfo); // 템플릿에서 접근 가능하도록 모델에 추가
        }
        model.addAttribute("notice", notice);
        return "admin/admin_noticeDetail";
    }

    @PostMapping("/admin/updateNotice")
    public String updateNotice(Notice notice) {
        noticeService.updateNotice(notice);
        return "redirect:/admin_notice";
    }

    @PostMapping("/admin/deleteNotice")
    public String deleteNotice(@RequestParam Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/admin_notice";
    }

}