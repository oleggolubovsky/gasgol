package il.wapp.app.validation;

import il.wapp.app.exception.*;
import org.apache.commons.io.*;
import org.springframework.stereotype.*;
import org.springframework.web.util.*;

@Service
public class MinioValidator extends BaseEntityValidator {

	public void validateFile(String fileName) {
		String fileExtension = FilenameUtils.getExtension(UriComponentsBuilder.fromUriString(fileName).
			replaceQuery(null).toUriString());
		if (fileExtension == null || (!fileExtension.contains("xls") && !fileExtension.contains("csv"))){
			throw new AttrValidationErrorException(String.format("File is not support format: %s", "xls,csv"));
		}
	}

}
