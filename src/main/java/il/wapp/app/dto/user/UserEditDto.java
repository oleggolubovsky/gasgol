package il.wapp.app.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEditDto implements Serializable {

	private Long id;

	private String firstName;

	private String lastName;

	private String status;

	private String role;

	private String corporation;

}
