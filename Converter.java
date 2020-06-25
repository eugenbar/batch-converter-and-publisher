/* By eugen.barbula@gmail.com

*/
import java.io.*;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Converter{
  public static void main(String[] args) throws IOException {

        //String pathname = args[0];
        //conversion(pathname);
        String path = "/Users/eugenbarbula/Downloads/data";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

        String img="";
        String audio ="";
        String p = "/Users/eugenbarbula/Downloads/data/";
        String jsn ="";
        String xml ="";
        boolean artist = true;
        ArrayList<String> artistL = new ArrayList<>();


        for (int k = 0;k<listOfFiles.length;k++){
            if (listOfFiles[k].getName().endsWith("jpg")){
                img = listOfFiles[k].getName();
            }
            if (listOfFiles[k].getName().endsWith("json")){
                jsn = listOfFiles[k].getName();
            }
            if (listOfFiles[k].getName().endsWith("xml")){
                xml = listOfFiles[k].getName();
            }
        }
        //xml="";

        for (int i=0;i<listOfFiles.length;i++){

            if (listOfFiles[i].getName().endsWith("3")){
                audio = listOfFiles[i].getName();
                /*
                img = audio.substring(0,audio.lastIndexOf("(")-1);
                for (int j=0;j<listOfFiles.length;j++){
                    if (listOfFiles[j].getName().endsWith("g")){
                        if (listOfFiles[j].getName().contains(img)){
                            img = listOfFiles[j].getName();
                        }
                    }
                }*/
                Path source = Paths.get(p.concat(audio));
                try {
                    Files.copy(source,Paths.get(p.concat("tmp.mp3")), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                source = Paths.get(p.concat(img));
                try {
                    Files.copy(source,Paths.get(p.concat("tmp.jpg")),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String cmd = "ffmpeg -loop 1 -framerate 0.1 -i "+p.concat("tmp.jpg")+" -i "+p.concat("tmp.mp3")+" -c:v libx264 -crf 0 -preset veryfast -tune stillimage -vf scale=-2:720 -c:a copy -shortest "+p.concat("tmp.mkv");
                System.out.println("***********************************");
                System.out.println(cmd);
                Process pp;
                String line="";
                String newName = "";
                String newNameEnd = "";

                try {
                    pp = Runtime.getRuntime().exec(cmd);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(pp.getErrorStream()));
                    while (true) {
                        try {
                            if (!((line = in.readLine()) != null)){
                                break;
                            }
                            if (line.contains("title")&& !(newName.length()>0)){
                                newName = newName.concat(line.substring(line.indexOf(":")+2));
                                if ((audio.contains("part")||audio.contains("Part"))
                                && !(newName.contains("part")||newName.contains("Part"))){
                                    String part = "";
                                    part = audio.substring(audio.indexOf("Part"),audio.indexOf("Part")+9);
                                    part = part.substring(0,part.lastIndexOf("_"));
                                    part = part.replace("_"," ");

                                    if (!(part.length()>0)){
                                        part = audio.substring(audio.indexOf("part")+5,audio.indexOf("part")+9);
                                        part = part.substring(0,part.lastIndexOf("_"));
                                        part = part.replace("_"," ");
                                    }
                                    if (!part.substring(0,1).contains(" ")){
                                        part = " ".concat(part);
                                    }
                                    newName = newName.concat(part);
                                }
                                newName = newName.replaceAll("/"," ");


                            }
                            if (line.contains("artist")&& !(newNameEnd.length()>0)){
                                newNameEnd = line.substring(line.indexOf(":")+1);
                                newName = newName.concat(" -");
                                newName = newName.concat(newNameEnd);
                                newName = newName.replaceAll("/"," ");

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(line);
                    }
                    try {
                        pp.waitFor();
                        in.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("Video converted successfully!");
                System.out.println(newName);

                // Get title and artist from jason file and construct the new file name from it
                if (jsn.length()>0) {
                    FileProcess fp = new FileProcess(p + jsn);
                    try {
                        //Jason file look up for titles
                        ArrayList<String> jasonList = new ArrayList<>();
                        jasonList = fp.getListFromJason();
                        String titleJson = "";
                        for (int index = 0; index < jasonList.size(); index++) {
                            if (jasonList.get(index).contains(audio)) {
                                int start = jasonList.get(index).indexOf("title") + 8;
                                titleJson = jasonList.get(index).substring(start);
                                titleJson = titleJson.substring(0, titleJson.indexOf("\""));
                                System.out.println("---------------------");
                                System.out.println("---------------------");
                                System.out.println("---------------------");
                                System.out.println("---------------------");
                                System.out.println("---------------------");

                                System.out.println(titleJson);
                            }
                        }
                        titleJson = titleJson.replaceAll("/"," ");
                        Files.move(Paths.get(p.concat("tmp.mkv")), Paths.get(p.concat(titleJson + ".mkv")));
                        //Files.move(Paths.get(p.concat("tmp.mkv")),Paths.get(p.concat(audio.substring(0,audio.length()-3).concat("mkv")).replaceAll("_"," - ")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Get title and artist from xml file and construct the new file name from it
                else if (xml.length()>0){

                    try {
                        FileProcess fp = new FileProcess(p + xml);
                        ArrayList<String> xmlList = new ArrayList<>();
                        xmlList = fp.getListFromXml();
                        String titleXml = "";
                        String artistS ="";
                        for (int index=0;index<xmlList.size();index++){
                            if (xmlList.get(index).contains(audio) && xmlList.get(index).contains("name=")){
                                for (int index2 = index+1;index2<xmlList.size();index2++){
                                    if (xmlList.get(index2).contains("title")){
                                        titleXml = xmlList.get(index2).substring(11,xmlList.get(index2).length()-8);
                                    }
                                    if (xmlList.get(index2).contains("artist") && artist){
                                        artistS = xmlList.get(index2).substring(11,xmlList.get(index2).length()-8);
                                    }
                                    if (titleXml.length()>0 && artist && artistS.length()>0){
                                        titleXml = titleXml+ " - ";
                                        titleXml = titleXml.concat(artistS.substring(1,artistS.length()-1));
                                        boolean containsB = false;
                                        for (int ai = 0;ai<artistL.size();ai++){
                                            if (artistL.get(ai).contains(artistS.substring(1,artistS.length()-1))){
                                                containsB = true;
                                            }
                                        }
                                        if (!containsB){
                                            artistL.add(artistS.substring(1,artistS.length()-1));
                                        }
                                        break;
                                    }
                                    else if(titleXml.length()>0 && !artist){
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        System.out.println(audio);
                        System.out.println("---------------------");
                        System.out.println("---------------------");
                        System.out.println("---------------------");
                        System.out.println("---------------------");
                        System.out.println("---------------------");
                        System.out.println(titleXml);

                        titleXml = titleXml.replaceAll("/"," ");
                        Files.move(Paths.get(p.concat("tmp.mkv")), Paths.get(p.concat(titleXml + ".mkv")));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                else {
                    Files.move(Paths.get(p.concat("tmp.mkv")), Paths.get(p.concat(newName + ".mkv")));
                }



                /*Path file = Paths.get(p.concat("tmp.mkv"));
                BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
                System.out.println(attr);*/

            }
        }

        if (artist) {
            for (int arti = 0; arti < artistL.size(); arti++) {
                System.out.print(artistL.get(arti) + ", ");
            }
            System.out.println();
        }


        String output = audio.substring(0,audio.length()-1);
        output = output.concat("4");
        output = p.concat(output);
        output = p.concat("test.mkv");
        audio = p.concat("1.mp3");
        img = p.concat("1.jpg");

        System.out.println(audio);
        System.out.println(img);
        System.out.println(output);
       /* String cmd = "ffmpeg -loop 1 -framerate 1 -i "+img+" -i "+audio+" -c:v libx264 -crf 0 -preset veryfast -c:a copy -strict experimental -shortest "+output;
        //String cmd = "ffmpeg -loop 1 -i "+img+" -i "+audio+" "+output;
        cmd = "ffmpeg -loop 1 -framerate 0.1 -i "+img+" -i "+audio+" -c:v libx264 -crf 0 -preset veryfast -tune stillimage -c:a copy -shortest "+output;
        //cmd = "ffmpeg -loop 1 -framerate 1 -i "+img+" -i "+audio+" -c:a aac -c:v libx264 -crf 0 -preset veryfast -shortest "+output;
        //cmd = "ffmpeg -r 1 -loop 1 -framerate 1 -i "+img+" -i "+audio+" -acodec copy -r 1 -tune stillimage -shortest "+output;
        //cmd = "ffmpeg -loop 1 -framerate 0.01 -i "+img+" -i "+audio+" -c:v libx264 -c:a aac -strict experimental -shortest "+output;
        try {
            Process pp = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(cmd);*/
    }
}
