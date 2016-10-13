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


    public FileBasedProjectManager() {
    }

    @Override
    public void createProject(String projectTemplate, String projectPath) {

        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        copyInputStreamToFile(is, new File(projectPath+File.separator+"netTransformer.pfl"));
        createDirs(projectTemplate,projectPath);
        createFiles(projectTemplate, projectPath);

    }

    private void  createFiles(String projectTemplate,String projectPath){
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        Scanner s =  new Scanner(is);
        if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
        System.out.println("_________" + System.getProperty("base.dir") + "_________");

        while (s.hasNextLine()) {
            String text = s.nextLine();
            if (text.startsWith("#") || text.trim().equals("")) continue;
            InputStream iss =this.getClass().getClassLoader().getResourceAsStream(text);
            if (iss==null){
                System.out.println("File "+text + " is empty!!!!");
                continue;
//                throw new ProjectManagerException("File "+text + " is empty!!!!");
            }
            if (copyInputStreamToFile(iss, new File(projectPath+File.separator+text))){
                logger.trace("File "+text + " successfully created!!!");

            }   else{
                logger.trace("File "+text + " creation failed!!!");
                throw new ProjectManagerException("File "+text + " creation failed!!!");


            }


        }
    }

    private void  createDirs(String projectTemplate,String projectPath){
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        Scanner s =  new Scanner(is);
        if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
        System.out.println("_________"+System.getProperty("base.dir")+"_________");

        while (s.hasNextLine()) {
            String text = s.nextLine();
            if (text.startsWith("#") || text.trim().equals("")) continue;

            File destDir = new File(projectPath, text).getParentFile();
            if(destDir.mkdirs()){
                logger.trace("Dir "+text + " successfully created!!!");
            } else{
                logger.trace("Dir "+text + " creation failed!!!");

            }

        }
    }

    @Override
    public void deleteProject(String projectPath) {

        File projectDir = new File(projectPath);
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
