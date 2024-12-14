package org.example.friendshipManagement.repository;

import org.example.utils.Pair;
import org.example.dto.FriendshipFilterDTO;
import org.example.friendshipManagement.entity.Friendship;
import org.example.sharedInfrastructure.PagingRepository;
import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface FriendshipRepository extends PagingRepository<Pair<String, String>, Friendship> {
    public Page<Friendship> findAllOnPage(Pageable pageable, FriendshipFilterDTO friendshipFilterDTO);

    public void deleteForUsername(String username);
}
