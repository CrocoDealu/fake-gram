package org.example.friendRequestManagement.repository;

import org.example.utils.Pair;
import org.example.dto.FriendRequestFilterDTO;
import org.example.friendRequestManagement.entity.FriendRequest;
import org.example.sharedInfrastructure.PagingRepository;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface FriendRequestRepository extends PagingRepository<Pair<String, String>, FriendRequest> {
    public Page<FriendRequest> findAllOnPage(Pageable pageable, FriendRequestFilterDTO friendRequestFilterDTO);
    public void deleteAcceptedOrDeclined();
}
