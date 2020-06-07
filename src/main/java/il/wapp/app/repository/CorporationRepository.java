package il.wapp.app.repository;

import il.wapp.app.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CorporationRepository extends PagingAndSortingRepository<Corporation, Long> {

    @Query(value = "SELECT crp FROM  Corporation crp")
    Page <Corporation> findAll( Pageable pageable);
}
