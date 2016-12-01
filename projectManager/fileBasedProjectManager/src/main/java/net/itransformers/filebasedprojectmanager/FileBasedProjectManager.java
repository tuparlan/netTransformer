package net.itransformers.filebasedprojectmanager;


import de.svenjacobs.loremipsum.LoremIpsum;
import net.itransformers.projectmanagerapi.ProjectManagerAPI;
import net.itransformers.projectmanagerapi.ProjectManagerException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Random;
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
    public void createProject(String projectName, String projectType) throws ProjectManagerException {
        String projectTemplate = ProjectTypeToTemplateResolver.getProjectTemplate(projectType);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(projectTemplate);

        File projectDir = getProjectDir(projectName);
        if (!projectDir.exists()){
            projectDir.mkdir();
        }
        try {
            copyInputStreamToFile(is, new File(projectDir, projectType+".pfl"));
        } catch (IOException e) {
            throw new ProjectManagerException(e.getMessage(),e);
        }
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

    private void   createFiles(String projectTemplate,String projectName) throws ProjectManagerException {
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
            try {
                if (copyInputStreamToFile(iss, new File(getProjectDir(projectName), text))){
                    logger.trace("File "+text + " successfully created!!!");

                }   else{
                    logger.trace("File "+text + " creation failed!!!");
                   // throw new ProjectManagerException("File "+text + " creation failed!!!");
                }
            } catch (IOException e) {
                throw  new ProjectManagerException(e.getMessage(),e);
            }
        }
    }

    private boolean  createDirs(String projectTemplate,String projectName) throws ProjectManagerException{
        InputStream is =this.getClass().getClassLoader().getResourceAsStream(projectTemplate);
        Scanner s =  new Scanner(is);

        while (s.hasNextLine()) {
            String text = s.nextLine();
            if (text.startsWith("#") || text.trim().equals("")) continue;

            File destDir = new File(getProjectDir(projectName), text).getParentFile();
            if(destDir.mkdirs()){
                logger.trace("Dir "+text + " successfully created!!!");
            } else{
              //  throw new ProjectManagerException("Dir "+text + " creation failed!!!");

            }
        }
        return true;
    }

    @Override
    public void deleteProject(String projectName) {

        File projectDir = getProjectDir(projectName);
        try {
            logger.info("Deleting project \""+projectName+"\" in folder \""+baseDir+"\"!!!");
            FileUtils.deleteDirectory(projectDir);
        } catch (IOException e) {
            throw new ProjectManagerException(e.getMessage(),e);
        }
    }

    private boolean copyInputStreamToFile( InputStream in, File file ) throws IOException{
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
        } catch (IOException e) {
            throw new ProjectManagerException(e.getMessage(),e);
        }
    }

    public String randomProjectNameGenerator(String projectsBaseDir) {
        LoremIpsum loremIpsum = new LoremIpsum();
        Random randomGenerator = new Random();

        while (true) {
            int random = randomGenerator.nextInt(50);
            String projectName = loremIpsum.getWords(1, random);
            File projectDir = new File(projectsBaseDir, projectName);
            if (projectDir.exists()) {
               continue;
            }else {
                return projectName;
            }

        }
    }
}
