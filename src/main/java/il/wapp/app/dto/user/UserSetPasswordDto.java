package il.wapp.app.dto.user;

import lombok.*;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSetPasswordDto implements Serializable {

	private String registerLink;

	private String password;

	private String confirmPassword;

	private String firstName;

	private String lastName;

	private String status;

}
