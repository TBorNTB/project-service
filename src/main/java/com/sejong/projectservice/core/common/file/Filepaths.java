package com.sejong.archiveservice.core.common.file;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Filepaths {
    private List<Filepath> filepaths;

    private Filepaths(List<Filepath> filepaths) {
        this.filepaths = filepaths;
    }

    public static Filepaths of(String paths) {
        if (paths == null) {
            return null;
        }
        String[] splits = paths.split(",");
        List<Filepath> filePaths = Arrays.stream(splits).map(Filepath::of).toList();
        return new Filepaths(filePaths);
    }

    public static Filepaths of(List<String> filepaths) {
        return new Filepaths(filepaths.stream().map(Filepath::of).toList());
    }

    @Override
    public String toString() {
        return filepaths.stream().map(Filepath::path).collect(Collectors.joining(","));
    }
}
