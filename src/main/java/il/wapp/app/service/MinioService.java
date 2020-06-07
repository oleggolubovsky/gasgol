package il.wapp.app.service;

import java.io.*;

public interface MinioService {
    String uploadFile(String name, byte[] content) throws Exception;
    InputStream getFile(String key) throws Exception;

    String uploadCampaignFile(String name, byte[] content) throws Exception;
}
