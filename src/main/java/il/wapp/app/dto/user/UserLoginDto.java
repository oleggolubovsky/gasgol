package il.wapp.app.dto.user;

import lombok.*;

import java.io.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginDto implements Serializable {

	private String email;

	private String password;

}
