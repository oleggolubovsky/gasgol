package il.wapp.app.dto.user;

import lombok.*;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterDto implements Serializable {

	private String email;

	private String firstName;

	private String lastName;

	private String status;

	private String corporation;

	private String role;

}
