package il.wapp.app.dto.contact;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterContactListDto {

	private Long id;

	private String name;
}
