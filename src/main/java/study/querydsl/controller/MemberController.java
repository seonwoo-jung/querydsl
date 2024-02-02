package study.querydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberRepository memberRepository;

	@GetMapping("/v2/members")
	public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
		return memberRepository.searchPageSimple(condition, pageable);
	}

	@GetMapping("/v3/members")
	public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
		return memberRepository.searchPageComplex(condition, pageable);
	}
}
