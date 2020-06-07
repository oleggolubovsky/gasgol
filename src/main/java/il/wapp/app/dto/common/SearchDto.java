package il.wapp.app.dto.common;

import lombok.*;

import java.io.*;

@Data
@AllArgsConstructor
public class SearchDto implements Serializable {

	private Long id;
	private String name;
}
