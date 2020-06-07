package il.wapp.app.validation;

import lombok.*;
import org.apache.commons.lang3.*;
import java.io.*;
import java.util.*;

@AllArgsConstructor
@Getter
@Setter
public class AttrValidationError implements Serializable {

	/**
	 * Название атрибута, для которого нужно подсветить ошибку.<br/>
	 */
	private String name;

	/**
	 * Текст ошибки
	 */
	private String error;

	@Override
	public String toString() {
		return StringUtils.isBlank(name) ? error : (name + ": " + error);
	}

	public AttrValidationError(String name, String error, Object... args) {
		this.name = name;
		this.error = new Formatter().format(error, args).toString();
	}
}
