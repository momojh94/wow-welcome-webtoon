package com.webtoon.core.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUploader {
    @Value("${custom.path.upload-images}")
    private static String BASE_FILE_PATH;
    // TODO : 설정 값으로 동적으로 관리하기 (server port도 달라짐)
    private static final String BASE_STATIC_RESOURCE_PATH = "http://localhost:8081/static";
    private static final String BASE_EPISODE_CONTENTS_PATH = "/ep_contents/";
    private static final String BASE_EPISODE_THUMBNAIL_PATH = "/ep_thumbnail/";
    private static final String BASE_WEBTOON_THUMBNAIL_PATH = "/wt_thumbnail/";

    private static String uniqueFileNameOf(MultipartFile file) {
        return new StringBuilder().append(UUID.randomUUID())
                                  .append("_")
                                  .append(file.getOriginalFilename())
                                  .toString();
    }

    private static String episodeContentsPathOf(String fileName) {
        return new StringBuilder().append(BASE_FILE_PATH)
                                  .append(BASE_EPISODE_CONTENTS_PATH)
                                  .append(fileName)
                                  .toString();
    }

    private static String episodeThumbnailPathOf(String fileName) {
        return new StringBuilder().append(BASE_FILE_PATH)
                                  .append(BASE_EPISODE_THUMBNAIL_PATH)
                                  .append(fileName)
                                  .toString();
    }

    private static String webtoonThumbnailPathOf(String fileName) {
        return new StringBuilder().append(BASE_FILE_PATH)
                                  .append(BASE_WEBTOON_THUMBNAIL_PATH)
                                  .append(fileName)
                                  .toString();
    }

    public static String webtoonThumbnailStaticResourcePathOf(String fileName) {
        return new StringBuilder().append(BASE_STATIC_RESOURCE_PATH)
                                  .append(BASE_WEBTOON_THUMBNAIL_PATH)
                                  .append(fileName)
                                  .toString();
    }

    public static String[] contentsStaticResourcePathOf(String contents) {
        return Arrays.stream(contents.split(";"))
                     .map(content -> BASE_STATIC_RESOURCE_PATH.concat(BASE_WEBTOON_THUMBNAIL_PATH)
                                                              .concat(content))
                     .toArray(String[]::new);
    }

    private static void uploadFile(MultipartFile file, String path) throws IOException {
        // TODO : file IOException CustomException으로 throw
        File destinationFile = new File(path);
        destinationFile.getParentFile().mkdir();
        file.transferTo(destinationFile);
    }

    public static String uploadEpisodeThumbnail(MultipartFile file) throws IOException {
        String thumbnail = uniqueFileNameOf(file);
        String filePath = episodeThumbnailPathOf(thumbnail);
        uploadFile(file, filePath);

        return thumbnail;
    }

    public static String uploadWebtoonThumbnail(MultipartFile file) throws IOException {
        String thumbnail = uniqueFileNameOf(file);
        String filePath = webtoonThumbnailPathOf(thumbnail);
        uploadFile(file, filePath);

        return thumbnail;
    }

    public static String uploadContentImages(MultipartFile[] files) throws IOException {
        StringBuilder contents = new StringBuilder();
        for (MultipartFile file : files) {
            String uniqueFileName = uniqueFileNameOf(file);
            String filePath = webtoonThumbnailPathOf(uniqueFileName);
            uploadFile(file, filePath);
            contents.append(uniqueFileName)
                    .append(";");
        }
        contents.deleteCharAt(contents.length() - 1);

        return contents.toString();
    }
}
