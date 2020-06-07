package il.wapp.app.repository;

import il.wapp.app.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;


public interface WhatsAppTemplateRepository extends JpaRepository<WhatsAppTemplate, Long> {

	@Query(value = "SELECT wat FROM WhatsAppTemplate wat " +
		"where wat.corporation=:corporation")
	Page<WhatsAppTemplate> findAllByCorporation(Corporation corporation, Pageable pageable);

}
