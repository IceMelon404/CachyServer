package com.icemelon404.cachy.storage.file.resolver;

import com.icemelon404.cachy.storage.file.FileWithId;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SingleDirectoryResolver implements IdFileResolver {

    private final String dirPath;
    private final File dir;

    public SingleDirectoryResolver(String dirPath) {
        this.dirPath = dirPath;
        dir = new File(dirPath);
    }

    @Override
    public Collection<FileWithVersion> getFileWithIds() {
        Collection<FileWithVersion> ret = new LinkedList<>();
        makeDirOrThrow(dir);
        Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(this::isSegmentFile)
                .forEach(file-> ret.add(fileWithVersion(file)));
        return ret;
    }

    private void makeDirOrThrow(File dir) {
        if (!dir.exists() && !dir.mkdirs())
            throw new IllegalStateException("디렉토리 생성에 실패하였습니다");
    }

    private boolean isSegmentFile(File file) {
        return file.getName().endsWith(".db");
    }

    private FileWithVersion fileWithVersion(File file) {
        return new FileWithVersion(new FileWithId(idFrom(file), file), versionFrom(file));
    }

    private long idFrom(File file) {
        return Long.parseLong(file.getName().split("-")[0]);
    }

    private int versionFrom(File file) {
        return Integer.parseInt(file.getName().replace(".db", "").split("-")[1]);
    }
    
    @Override
    public File createFileWithId(long newSegmentId) throws IOException {
        File newFile = createWithLatestVersion(newSegmentId);
        createOrThrow(newFile);
        return newFile;
    }

    private File createWithLatestVersion(long newSegmentId) {
        int version = 0;
        for (File file : dir.listFiles()) {
            if (idFrom(file) == newSegmentId)
                version = Math.max(version, versionFrom(file));
        }
        return new File(fileNameWithVersion(newSegmentId, version + 1));
    }

    private String fileNameWithVersion(long segmentId, int version) {
        return dirPath + File.separator + segmentId + "-" + version + ".db";
    }

    private void createOrThrow(File newFile) throws IOException {
        if (!newFile.createNewFile())
            throw new IllegalStateException("파일 생성에 실패하였습니다");
    }

    @Override
    public List<File> getFileWithId(long segmentId) {
        File dir = new File(dirPath);
        makeDirOrThrow(dir);
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(this::isSegmentFile)
                .filter(file -> idFrom(file) == segmentId)
                .collect(Collectors.toList());
    }
}
