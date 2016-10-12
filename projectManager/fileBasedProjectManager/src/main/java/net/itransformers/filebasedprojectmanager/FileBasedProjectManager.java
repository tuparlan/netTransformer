package net.itransformers.filebasedprojectmanager;


import net.itransformers.projectmanagerapi.ProjectManagerAPI;
import net.itransformers.projectmanagerapi.ProjectManagerException;
import net.itransformers.utils.RecursiveCopy;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Scanner;

/**
 * Created by niau on 10/11/16.
 */
public class FileBasedProjectManager implements ProjectManagerAPI {
    static Logger logger = Logger.getLogger(FileBasedProjectManager.class);


    public FileBasedProjectManager() {
    }

    @Override
    public void createProject(String projectTemplate, String projectPath) {

        Scanner s = null;
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);

        copyInputStreamToFile(is, new File(projectPath, "netTransformer.pfl"));
        InputStream iss =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);

        s = new Scanner(iss);

        try {

            while (s.hasNextLine()) {
                String text = s.nextLine();
                if (text.startsWith("#") || text.trim().equals("")) continue;
                if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
                String workDirName = System.getProperty("base.dir");
                File workDir = new File(workDirName);
                File srcDir = new File(workDir, text);
                File destDir = new File(projectPath, text).getParentFile();
                destDir.mkdirs();
                RecursiveCopy.copyDir(srcDir, destDir);

            }

        } catch (IOException e1) {
            throw new ProjectManagerException(e1.getMessage(),e1);
        }

    }

    @Override
    public void deleteProject(String projectPath) {

        File projectDir = new File(projectPath);

        if (projectDir.exists()){
            projectDir.delete();
        }  else {
            throw new ProjectManagerException("ProjectPath "+ projectPath+" does not exist!!!");
        }
    }
    private void copyInputStreamToFile( InputStream in, File file ) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
