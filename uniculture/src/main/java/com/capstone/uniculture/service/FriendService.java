package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.*;

import com.capstone.uniculture.dto.Member.Response.ProfileResponseDto;
import com.capstone.uniculture.dto.Recommend.ProfileRecommendRequestDto;
import com.capstone.uniculture.dto.Recommend.ProfileRecommendResponseDto;
import com.capstone.uniculture.dto.Recommend.ToFlaskRequestDto;
import com.capstone.uniculture.entity.Friend.*;
import com.capstone.uniculture.entity.Member.*;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.entity.Notification.NotificationType;
import com.capstone.uniculture.repository.*;
import com.deepl.api.Usage;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.SharedSessionContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final NotificationRepository notificationRepository;
    private final MyHobbyRepository myHobbyRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRecommendRepository friendRecommendRepository;
    private final EntityManager entityManager;

    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                ()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    private FriendRequest findFriendRequest(Long senderId, Long receiverId) {
        FriendRequest friendRequest = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 친구 요청이 존재하지 않습니다")
        );
        return friendRequest;
    }

    // 친구 요청 신청
    public ResponseEntity friendRequest(Long id, Long toId){

        // 1. 먼저, 두 사람이 이미 친구관계가 아닌지 확인
        if(friendshipRepository.existsByFromMember_IdAndToMember_Id(id, toId)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 친구관계 입니다");
        }
        // 2. 두 사람이 서로에게 친구신청을 한것이 있는지 확인(둘다 없어야함)
        if(friendRequestRepository.existsBySender_IdAndReceiver_Id(id, toId) ||
        friendRequestRepository.existsBySender_IdAndReceiver_Id(toId,id)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("친구신청 한 것이 있습니다");
        }

        // 2. 두 사람(신청자, 상대)의 정보 획득
        Member sender = memberRepository.getReferenceById(id);
        Member receiver = memberRepository.getReferenceById(toId);


        Notification notification = Notification.builder()
                .notificationType(NotificationType.FRIEND)
                .member(receiver)
                .isCheck(Boolean.FALSE)
                .content(sender.getNickname() + "님이 나에게 친구 신청을 보냈습니다!")
                .relatedNum(id)
                .build();
        notificationRepository.save(notification);

        // 3. 친구신청 해주기
        FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);

        return ResponseEntity.ok("친구 요청에 성공하였습니다");
    }

    // 친구 요청 취소
    public String revokeFriendRequest(Long senderId, Long receiverId){
        FriendRequest friendRequest = findFriendRequest(senderId, receiverId);
        friendRequestRepository.delete(friendRequest);
        return "친구 요청 취소에 성공하였습니다";
    }

    // 친구 삭제 - 양방향 모두 삭제해줘야한다
    public String deleteFriend(Long memberId, Long targetId) {
        if(friendshipRepository.existsByFromMember_IdAndToMember_Id(memberId, targetId) &&
                friendshipRepository.existsByFromMember_IdAndToMember_Id(targetId,memberId))
        {
            friendshipRepository.delete(
                    friendshipRepository.findByFromMember_IdAndToMember_Id(memberId, targetId)
            );
            friendshipRepository.delete(
                    friendshipRepository.findByFromMember_IdAndToMember_Id(targetId, memberId)
            );
        }else{
            throw new InvalidRequestStateException("두 상대가 서로 친구 관계가 아닙니다");
        }
        return "친구 삭제 성공";
    }

    // 친구 요청 수락 - 신청한사람의 닉네임이 날아온다고 가정한다
    public String acceptFriendRequest(Long receiverId, Long senderId) {
        FriendRequest friendRequest = findFriendRequest(senderId,receiverId);
        // 1. 신청자와 수락자 두 Member 를 찾아주고
        Member receiver = friendRequest.getReceiver();
        Member sender = friendRequest.getSender();
        // 2. 서로 친구관계를 맺어주고
        sender.addFriend(receiver);
        receiver.addFriend(sender);
        // 3. 친구 요청은 삭제시킨다
        friendRequestRepository.delete(friendRequest);
        return "친구 수락 성공";
    }

    // 친구 요청 거절
    public String rejectFriendRequest(Long receiverId, Long senderId){
        FriendRequest friendRequest = findFriendRequest(senderId, receiverId);
        friendRequestRepository.delete(friendRequest);
        // Cascade 옵션에 의해 Receiver 쪽의 Request 는 자동으로 삭제된다.
        return "친구 거절 성공";
    }

    // Stream 은 값이 Null 이더라도 빈 리스트를 반환해주어 NullPointerException 을 방지할수있다.
    // 간단 친구 목록 조회 (프로필창에서 친구 눌러 조회할때)
    public List<FriendResponseDto> listOfFriends(Long id){
        return friendshipRepository.findAllByFromMember_Id(id)
                .stream().map(FriendResponseDto::fromMember)
                .collect(Collectors.toList());
    }


    // 친구 목록 상세조회
    public Page<DetailFriendResponseDto> listOfFriends2(String name, Long id, Pageable pageable){

        Page<Member> friendList = null;

        if(name != null){ // 만약, 이름 검색조건이 입력되었다면
            friendList = friendshipRepository.findFriendsByNickname(id, name, pageable);
        }
        else { // 아니면 내 친구 전체 상세조회
            friendList = friendshipRepository.findAllByFromMember_Id_Paging(id, pageable);
        }
        List<DetailFriendResponseDto> list = friendList.stream().map(DetailFriendResponseDto::fromMember).toList();
        return new PageImpl<>(list, pageable, friendList.getTotalElements());
    }
    // 나에게 온 친구 신청 목록 조회
    public List<FriendResponseDto> listOfFriendRequest(Long id){
        return friendRequestRepository.findByReceiverId(id)
                .stream().map(friendRequest -> FriendResponseDto.fromMember(friendRequest.getSender()))
                .collect(Collectors.toList());
    }

    // 내가 보낸 친구 신청목록 조회
    public List<FriendResponseDto> listOfMyFriendRequest(Long id){
        return friendRequestRepository.findBySenderId(id)
                .stream().map(friendRequest -> FriendResponseDto.fromMember(friendRequest.getReceiver()))
                .collect(Collectors.toList());
    }

    public Page<DetailFriendResponseDto> getMyFriendBySearch2(String hobby, String myLanguage, String wantLanguage, Integer minAge, Integer maxAge, Gender gender, Pageable pageable) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Specification<Friendship> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("fromMember").get("id"), memberId));

            Join<Friendship, Member> toMemberJoin = root.join("toMember", JoinType.INNER);

            if (hobby != null) {
                Join<Member, MyHobby> myHobbyJoin = toMemberJoin.join("myHobbyList", JoinType.INNER); // 조인 수정
                predicates.add(criteriaBuilder.equal(myHobbyJoin.get("hobbyName"), hobby));
            }

            if (myLanguage != null) {
                Join<Member, MyLanguage> myLanguageJoin = toMemberJoin.join("myLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(myLanguageJoin.get("language"), myLanguage));
            }

            if (wantLanguage != null) {
                Join<Member, WantLanguage> wantLanguageJoin = toMemberJoin.join("wantLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(wantLanguageJoin.get("language"), wantLanguage));
            }

            if (minAge != null && maxAge != null) {
                predicates.add(criteriaBuilder.between(toMemberJoin.get("age"), minAge, maxAge));
            }

            if (gender != null) {
                predicates.add(criteriaBuilder.equal(toMemberJoin.get("gender"), gender));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Friendship> page = friendshipRepository.findAll(specification, pageable);

        List<DetailFriendResponseDto> list = page.getContent().stream()
                .map(friendship -> DetailFriendResponseDto.fromMember(friendship.getToMember()))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public Page<DetailFriendResponseDto> getMyFriendBySearch4(String hobby, String myLanguage, String wantLanguage, Integer minAge, Integer maxAge, Gender gender, Pageable pageable) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        Specification<Member> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("id"), memberId));

            if (hobby != null) {
                Join<Member, MyHobby> myHobbyJoin = root.join("myHobbyList", JoinType.INNER); // 조인 수정
                predicates.add(criteriaBuilder.equal(myHobbyJoin.get("hobbyName"), hobby));
            }

            if (myLanguage != null) {
                Join<Member, MyLanguage> myLanguageJoin = root.join("myLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(myLanguageJoin.get("language"), myLanguage));
            }

            if (wantLanguage != null) {
                Join<Member, WantLanguage> wantLanguageJoin = root.join("wantLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(wantLanguageJoin.get("language"), wantLanguage));
            }

            if (minAge != null && maxAge != null) {
                predicates.add(criteriaBuilder.between(root.get("age"), minAge, maxAge));
            }

            if (gender != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Member> page = memberRepository.findAll(specification, pageable);

        List<DetailFriendResponseDto> list = page.getContent().stream()
                .map(member -> {
                    System.out.println("Member 한명 조회했습니다");
                    return DetailFriendResponseDto.fromMember(member);
                }).toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }
    public List<DetailFriendResponseDto> searchMembers(String hobby, String myLanguage, String wantLanguage, Integer minAge, Integer maxAge, Gender gender, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        Root<Member> memberRoot = criteriaQuery.from(Member.class);

        List<Predicate> predicates = new ArrayList<>();

        if (hobby != null) {
            Join<Member, MyHobby> myHobbyJoin = memberRoot.join("myHobbyList", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(myHobbyJoin.get("hobbyName"), hobby));
        }

        if (myLanguage != null) {
            Join<Member, MyLanguage> myLanguageJoin = memberRoot.join("myLanguages", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(myLanguageJoin.get("language"), myLanguage));
        }

        if (wantLanguage != null) {
            Join<Member, WantLanguage> wantLanguageJoin = memberRoot.join("wantLanguages", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(wantLanguageJoin.get("language"), wantLanguage));
        }

        if (minAge != null && maxAge != null) {
            predicates.add(criteriaBuilder.between(memberRoot.get("age"), minAge, maxAge));
        }

        if (gender != null) {
            predicates.add(criteriaBuilder.equal(memberRoot.get("gender"), gender));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Member> typedQuery = entityManager.createQuery(criteriaQuery);

        // 페이징 적용
        typedQuery.setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Member> members = typedQuery.getResultList();

        return members.stream().map(DetailFriendResponseDto::fromMember).toList();
    }


    public void checkCache(){
        Long memberId1 = SecurityUtil.getCurrentMemberId();
        if(friendRecommendRepository.findAlreadyRecommend(LocalDateTime.now().minusDays(1),memberId1) != null){
            System.out.println("와 존재하네요");
        }
        else{
            System.out.println("친구추천 기록이 없어요 ㅠㅠ ");
        }
    }

    public List<RecommendFriendResponseDto> recommendFriend(){

        Long memberId = SecurityUtil.getCurrentMemberId();

        List<FriendRecommend> idList = friendRecommendRepository.findAlreadyRecommend(LocalDateTime.now().minusDays(1), memberId);

        if(idList != null && !idList.isEmpty()){ // 캐시에서 가져올수있으면 가져오기
            return idList.stream().map(friendRecommend ->
                RecommendFriendResponseDto.fromMember(friendRecommend.getFriendRecommendPK().getToMember(), friendRecommend.getIsOpen(), friendRecommend.getSimilarity())).toList();
        }
        else{ // 플라스크 실행시켜야됨
            return recommendFriends(memberId);
        }
    }

    public Long recommendCountCheck(Long memberId){
        return memberRepository.countRemainCount(memberId);
    }

    public Boolean recommendCountDown(Long memberId){
        if(recommendCountCheck(memberId) != 0){
            memberRepository.decrementRemainCount(memberId);
            return true;
        }
        else{
            return false;
        }
    }

    public List<RecommendFriendResponseDto> recommendFriends(Long memberId) {

        // 1. 일단 친구추천 테이블의 모든 데이터를 삭제하자
        friendRecommendRepository.deleteAllByFriendRecommendPK_FromMemberId(memberId);

        // 2. 내 취미정보 찾아놓기(추후, 취미 비교를 위함)
        List<String> myHobby = myHobbyRepository.findAllByMemberId(memberId);

        // 2. 내 친구를 제외한 모든 멤버의 정보중 20명 가져오기 -> 목적, 취미, 언어는 Proxy 상태
        Pageable pageable = PageRequest.of(0, 20);
        Page<Member> memberList = memberRepository.findNonFriendMemberEdit(memberId, pageable);

        System.out.println("memberList = " + memberList);

        // 3. 모든 멤버를 돌면서 추천에 필요한 DTO 객체로 생성하기
        List<ProfileRecommendRequestDto> recommendRequestItems = memberList.getContent().stream().map(ProfileRecommendRequestDto::fromEntity).toList();

        // 4. Flask로 보내서 받아오기
        ProfileRecommendResponseDto responseDto = sendRequestToFlask(ToFlaskRequestDto.builder()
                .id(memberId)
                .profiles(recommendRequestItems)
                .build());

        // 5. 추천받은 아이디로 멤버 상세 객체 만들어서 반환


        Set<Map.Entry<Long, Long>> entrySet = responseDto.getData().getSortedIdList().entrySet();

        List<FriendRecommend> friendRecommends = entrySet.stream().map(set -> {
            Member toMember = memberRepository.getReferenceById(set.getKey());
            Member fromMember = memberRepository.getReferenceById(memberId);
            FriendRecommend friendRecommend = new FriendRecommend(fromMember, toMember, set.getValue());
            return friendRecommend;
        }).toList();

        friendRecommendRepository.saveAll(friendRecommends);

        return entrySet.stream()
                .map(entry ->
                {
                    Member member = findMember(entry.getKey());
                    List<RecommendHobby> hobbies = new ArrayList<>(); //
                    member.getMyHobbyList().forEach(p -> {
                        hobbies.add(new RecommendHobby(p.getHobbyName(), myHobby.contains(p.getHobbyName())));
                    });
                    RecommendFriendResponseDto recommendFriendResponseDto = RecommendFriendResponseDto.fromMember(member, false, entry.getValue());
                    recommendFriendResponseDto.setHobbies(hobbies);
                    return recommendFriendResponseDto;
                }).toList();
    }

    private ProfileRecommendResponseDto sendRequestToFlask(ToFlaskRequestDto requestDto) {


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ProfileRecommendResponseDto> responseEntity = restTemplate.postForEntity(
                "http://localhost:8000/api/v1/profile/recommend",
                new HttpEntity<>(requestDto, headers),
                ProfileRecommendResponseDto.class
        );

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        else{
            throw new RuntimeException("친구 추천 서버 오류입니다");
        }
    }

    public void openProfile(Long targetId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Member fromMember = memberRepository.getReferenceById(memberId);
        Member toMember = memberRepository.getReferenceById(targetId);

        FriendRecommend friendRecommend = friendRecommendRepository.findById(new FriendRecommendPK(fromMember, toMember)).get();
        friendRecommend.changeStatus(true);
    }

}
