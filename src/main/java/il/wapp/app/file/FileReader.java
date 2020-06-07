package il.wapp.app.file;

import il.wapp.app.dto.contact.*;
import il.wapp.app.exception.*;
import il.wapp.app.validation.*;
import org.apache.commons.io.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileReader {

	private static final String COMMA_DELIMITER = ",";

	public static List<ContactRegisterDto> registerContacts(String fileUrl, InputStream in) throws Exception {
		List<ContactRegisterDto> newContacts = new ArrayList<>();
		int i = 0;
		try {
			fileUrl = fileUrl.replaceAll(" ", "%20");
			String fileExtension = FilenameUtils.getExtension(UriComponentsBuilder.fromUriString(fileUrl).
				replaceQuery(null).toUriString());
			String tempDir = System.getProperty("java.io.tmpdir") + UUID.randomUUID().toString() + "." + fileExtension;
			Files.copy(in, Paths.get(tempDir)
				, StandardCopyOption.REPLACE_EXISTING);
			List<AttrValidationError> errors = new ArrayList();
			if ("csv".equals(fileExtension)) {
				try (BufferedReader br = new BufferedReader(new java.io.FileReader(tempDir))) {
					String line;
					while ((line = br.readLine()) != null) {
						i++;
						String[] values = line.split(COMMA_DELIMITER);
						if (values[1] != null) {
							String phone = ContactValidator.checkPhoneFormat(values[1], errors);
							if (phone == null || "".equals(phone)) {
								throw new FileFormatException(String.format("Row %s: %s, %s", i, values[0], values[1]));
							}
							newContacts.add(ContactRegisterDto.builder().name(values[0])
								.phone(values[1]).build());
						}
					}
				}
			} else if (fileExtension.contains("xls")) {
				Workbook workbook = WorkbookFactory.create(new File(tempDir));
				Sheet sheet = workbook.getSheetAt(0);
				for (int n = 0; n <= sheet.getLastRowNum(); n++) {
					Row row = sheet.getRow(n);
					if (sheet.getRow(n) == null) {
						throw new FileFormatException(String.format("Row %s: %s, %s", n + 1, "", ""));
					}
					Cell nameCell = row.getCell(0);
					Cell phoneCell = row.getCell(1);
					if (phoneCell != null) {
						if (nameCell != null) {
							nameCell.setCellType(CellType.STRING);
						}
						if (phoneCell != null) {
							phoneCell.setCellType(CellType.STRING);
						}
						String name = nameCell != null ? nameCell.getStringCellValue() : "";
						String phoneInput = phoneCell != null ? phoneCell.getStringCellValue() : "";
						String phone = ContactValidator.checkPhoneFormat(phoneInput, errors);
						if (phone == null || "".equals(phone)) {
							throw new FileFormatException(String.format("Row %s: %s, %s", row.getRowNum() + 1, name, phoneInput));
						}
						newContacts.add(ContactRegisterDto.builder()
							.name(name)
							.phone(phone)
							.build());
					}
				}
				workbook.close();
			}
		} catch (Exception ex) {
			throw new FileFormatException(ex instanceof FileFormatException ? ex.getMessage() : "Row:"+i+" Bad format");
		}
		return newContacts;
	}
}
