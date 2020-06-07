package il.wapp.app.repository;

import il.wapp.app.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;


public interface CampaignRepository extends JpaRepository<Campaign, Long> {

	@Query(value = "SELECT c FROM Campaign c " +
		"where c.user.corporation=:corporation")
	Page<Campaign> findAllByCorporation(Corporation corporation, Pageable pageable);

}
