package net.itransformers.filebasedprojectmanager;


import net.itransformers.projectmanagerapi.ProjectManagerAPI;
import net.itransformers.projectmanagerapi.ProjectManagerException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Scanner;

/**
 * Created by niau on 10/11/16.
 */
public class FileBasedProjectManager implements ProjectManagerAPI {
    static Logger logger = Logger.getLogger(FileBasedProjectManager.class);

    File baseDir;

    public FileBasedProjectManager(File baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void createProject(String projectName, String projectTemplate) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        File projectDir = getProjectDir(projectName);
        copyInputStreamToFile(is, new File(projectDir, "netTransformer.pfl"));
        createDirs(projectTemplate,projectName);
        createFiles(projectTemplate, projectName);
    }

    private File getProjectDir(String projectName){
        return new File(baseDir,projectName);
    }

    @Override
    public String[] getProjectNames() {
        return baseDir.list();
    }

    private void  createFiles(String projectTemplate,String projectName){
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        Scanner s =  new Scanner(is);

        while (s.hasNextLine()) {
            String text = s.nextLine();
            if (text.startsWith("#") || text.trim().equals("")) continue;
            InputStream iss =this.getClass().getClassLoader().getResourceAsStream(text);
            if (iss==null){
                System.out.println("File "+text + " is empty!!!!");
                continue;
            }
            if (copyInputStreamToFile(iss, new File(getProjectDir(projectName), text))){
                logger.trace("File "+text + " successfully created!!!");

            }   else{
                logger.trace("File "+text + " creation failed!!!");
                throw new ProjectManagerException("File "+text + " creation failed!!!");
            }
        }
    }

    private void  createDirs(String projectTemplate,String projectName){
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        Scanner s =  new Scanner(is);

        while (s.hasNextLine()) {
            String text = s.nextLine();
            if (text.startsWith("#") || text.trim().equals("")) continue;

            File destDir = new File(getProjectDir(projectName), text).getParentFile();
            if(destDir.mkdirs()){
                logger.trace("Dir "+text + " successfully created!!!");
            } else{
                logger.trace("Dir "+text + " creation failed!!!");
            }
        }
    }

    @Override
    public void deleteProject(String projectName) {

        File projectDir = getProjectDir(projectName);
        try {
            FileUtils.deleteDirectory(projectDir);
        } catch (IOException e) {
            throw new ProjectManagerException(e.getMessage(),e);
        }
    }

    private boolean copyInputStreamToFile( InputStream in, File file ) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }
}
