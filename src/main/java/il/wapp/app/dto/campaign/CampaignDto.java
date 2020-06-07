package il.wapp.app.dto.campaign;

import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDto implements Serializable {

	private Long id;

	private String name;

	private Date createdAt;

	private Date updatedAt;

	private Date sendingAt;

}
