package il.wapp.app.dto.user;

import il.wapp.app.dto.contact.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
public class UserDto implements Serializable {

	private Long id;

	private String password;

	private String email;

	private String token;

	private String role;

	private String firstName;

	private String lastName;

	private String corporation;

	private String status;

	private Date createdAt;

	private Date updatedAt;

}
