package com.javalite.index;

import org.javalite.common.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Igor Polevoy on 10/16/15.
 */
public class IndexMain {
    public static void main(String[] args) throws IOException {





        String target = "target/output/search_index";
        File targetDir = Paths.get(target).toFile();
        if (targetDir == null || !targetDir.exists() || !targetDir.isDirectory()) {
            throw new RuntimeException("failed to find target directory");
        }

        System.out.println("IndexMain: Completed indexing, processed: "
                + new Indexer(Paths.get(target)).index(args[0]) + " documents");
        System.out.println("IndexMain: Completed indexing, processed: "
                + new Indexer(Paths.get(target)).index(args[1]) + " documents");
    }
}
