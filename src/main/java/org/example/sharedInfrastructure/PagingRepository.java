package org.example.sharedInfrastructure;

import org.example.utils.paging.Page;
import org.example.utils.paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {

    public Page<E> findAllOnPage(Pageable pageable);
}
