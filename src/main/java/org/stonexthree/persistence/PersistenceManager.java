package org.stonexthree.persistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PersistenceManager {
    String baseDirName;

    public PersistenceManager(@Value("${app-config.storage.base-dir}")String baseDirName) {
        //File projectPath = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
        this.baseDirName = baseDirName;
    }

    public File getBaseDir(){
        return new File(baseDirName);
    }

}
