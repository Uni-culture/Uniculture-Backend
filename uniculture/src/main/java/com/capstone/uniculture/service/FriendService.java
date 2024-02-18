package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.OtherProfileDto;
import com.capstone.uniculture.entity.Member.FriendRequest;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.RequestStatus;
import com.capstone.uniculture.repository.FriendRequestRepository;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;

    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                ()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    // 친구 요청 신청 - 일단 신청자는 닉네임이 날아온다고 가정한다
    public void friendRequest(Long id, String toNickname){
        Member sender = findMember(id);
        Member receiver = memberRepository.findByNickname(toNickname).get();
        FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);
        receiver.getReceivedRequests().add(friendRequest);
    }

    // 친구 요청 수락 - 일단 신청한사람의 닉네임이 날아온다고 가정한다
    public void acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).get();
        // 1. 신청자와 수락자 두 Member 를 찾아주고
        Member receiver = friendRequest.getReceiver();
        Member sender = friendRequest.getSender();
        // 2. 서로 친구관계를 맺어주고
        sender.addFriend(receiver);
        receiver.addFriend(sender);
        // 3. 친구 요청은 삭제시킨다
        friendRequestRepository.delete(friendRequest);
    }

    // 친구 요청 거절
    public void rejectFriendRequest(Long requestId){
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).get();
        friendRequestRepository.delete(friendRequest);

    }
    // Stream 은 값이 Null 이더라도 빈 리스트를 반환해주어 NullPointerException 을 방지할수있다.
    // 친구 목록 조회
    public List<OtherProfileDto> listOfFriends(Long id){
        return findMember(id).getFriends()
                .stream().map(member -> new OtherProfileDto(member))
                .collect(Collectors.toList());
    }
    // 나에게 온 친구 신청 목록 조회
    public List<OtherProfileDto> listOfFriendRequest(Long id){
        return findMember(id).getReceivedRequests()
                .stream().map(friendRequest -> new OtherProfileDto(friendRequest.getSender()))
                .collect(Collectors.toList());
    }

    // 내가 보낸 친구 신청목록 조회
    public List<OtherProfileDto> listOfMyFriendRequest(Long id){
        return friendRequestRepository.findBySenderId(id)
                .stream().map(friendRequest -> new OtherProfileDto(friendRequest.getSender()))
                .collect(Collectors.toList());
    }
}