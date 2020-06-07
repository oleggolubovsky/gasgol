package il.wapp.app.dto.whatsapp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppTemplateDto implements Serializable {

    private String category;

    private Language language;

    private Header header;

    private String body;

    private String footer;

    private String buttonType;

    private List<Button> button;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Language {
        private String code;
        private String policy;
    }

    @Data
    public static class Header {
        private String media;
        private String text;
    }

    @Data
    static class Button {
        private String value;
    }

}
