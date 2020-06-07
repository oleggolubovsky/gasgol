package il.wapp.app.dto.contact;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.io.*;
import java.util.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactListDto implements Serializable {

	private Long id;

	private String name;

	private Date createdAt;

	private Date updatedAt;

	private boolean enable;

}
