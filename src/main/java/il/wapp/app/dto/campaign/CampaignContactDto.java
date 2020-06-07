package il.wapp.app.dto.campaign;

import lombok.*;

import java.io.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignContactDto implements Serializable {

	private Long campaignId;

	private String name;

	private String from;

	private String to;

}
