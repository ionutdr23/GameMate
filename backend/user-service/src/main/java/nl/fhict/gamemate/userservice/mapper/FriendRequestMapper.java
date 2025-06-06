package nl.fhict.gamemate.userservice.mapper;

import nl.fhict.gamemate.userservice.dto.FriendRequestDto;
import nl.fhict.gamemate.userservice.model.FriendRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FriendRequestMapper {
    public static FriendRequestDto toDto(FriendRequest request) {
        if (request == null) return null;

        return FriendRequestDto.builder()
                .id(request.getId())
                .sender(ProfileMapper.toPreview(request.getSender()))
                .receiver(ProfileMapper.toPreview(request.getReceiver()))
                .createdAt(request.getCreatedAt())
                .build();
    }

    public static List<FriendRequestDto> toDtoList(Collection<FriendRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream()
                .map(FriendRequestMapper::toDto)
                .toList();
    }

    public static Set<FriendRequestDto> toDtoSet(Collection<FriendRequest> requests) {
        if (requests == null) return Set.of();
        return requests.stream()
                .map(FriendRequestMapper::toDto)
                .collect(Collectors.toSet());
    }
}
