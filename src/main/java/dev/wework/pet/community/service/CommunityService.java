package dev.wework.pet.community.service;

import dev.wework.pet.community.dto.CommunityRequestDto;
import dev.wework.pet.community.dto.CommunityResponseDto;
import dev.wework.pet.community.entity.Community;
import dev.wework.pet.community.repository.CommunityRepository;
import dev.wework.pet.user.signup.dto.Classification;
import dev.wework.pet.user.signup.entity.User;
import dev.wework.pet.user.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    /** ✅ 게시글 작성 */
    public CommunityResponseDto createPost(String loginID, CommunityRequestDto requestDto, String communityType) {
        User user = userRepository.findByLoginID(loginID);
        if (user == null) throw new RuntimeException("존재하지 않는 사용자입니다.");

        Classification classification = user.getClassification();

        // 🔒 권한 검증 (한글 Enum 기반)
        if (communityType.equalsIgnoreCase("notice")) {
            if (classification != Classification.관리자)
                throw new RuntimeException("관리자만 공지사항을 작성할 수 있습니다.");
        } else if (communityType.equalsIgnoreCase("board")) {
            if (classification != Classification.심사원 && classification != Classification.관리자)
                throw new RuntimeException("심사원과 관리자만 게시판에 글을 작성할 수 있습니다.");
        }

        Community community = new Community();
        community.setTitle(requestDto.getTitle());
        community.setContent(requestDto.getContent());
        community.setAuthor(user.getName());
        community.setType(communityType);
        community.setCreatedAt(LocalDateTime.now());
        community.setUpdatedAt(LocalDateTime.now());

        Community saved = communityRepository.save(community);
        return new CommunityResponseDto(saved);
    }

    /** ✅ 전체 조회 */
    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getAllPosts(String type) {
        return communityRepository.findAllByTypeOrderByCreatedAtDesc(type)
                .stream()
                .map(CommunityResponseDto::new)
                .collect(Collectors.toList());
    }

    /** ✅ 단건 조회 */
    @Transactional(readOnly = true)
    public CommunityResponseDto getPost(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return new CommunityResponseDto(community);
    }

    /** ✅ 수정 (작성자 본인만 가능) */
    public CommunityResponseDto updatePost(Long id, CommunityRequestDto dto, String loginID) {
        User user = userRepository.findByLoginID(loginID);
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!community.getAuthor().equals(user.getName()))
            throw new RuntimeException("작성자만 수정할 수 있습니다.");

        community.setTitle(dto.getTitle());
        community.setContent(dto.getContent());
        community.setUpdatedAt(LocalDateTime.now());

        return new CommunityResponseDto(communityRepository.save(community));
    }

    /** ✅ 삭제 (작성자 본인만 가능) */
    public void deletePost(Long id, String loginID) {
        User user = userRepository.findByLoginID(loginID);
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!community.getAuthor().equals(user.getName()))
            throw new RuntimeException("작성자만 삭제할 수 있습니다.");

        communityRepository.delete(community);
    }
}
